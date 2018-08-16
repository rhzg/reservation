package de.fraunhofer.iosb.services.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.geojson.Point;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fraunhofer.iosb.Constants;
import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.Term;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.repository.RoomRepository;
import de.fraunhofer.iosb.repository.TermRepository;
import de.fraunhofer.iosb.repository.UserRepository;
import de.fraunhofer.iosb.representation.NearbyRequest;
import de.fraunhofer.iosb.representation.NearbyRoom;
import de.fraunhofer.iosb.representation.RoomDetailsRepresentation;
import de.fraunhofer.iosb.representation.RoomRepresentation;
import de.fraunhofer.iosb.representation.TermsResponse;
import de.fraunhofer.iosb.representation.UserRepresentation;
import de.fraunhofer.iosb.services.RoomService;
import de.fraunhofer.iosb.services.UserService;
import de.fraunhofer.iosb.smartbuilding.SbBeacon;
import de.fraunhofer.iosb.smartbuilding.SbFactory;
import de.fraunhofer.iosb.smartbuilding.SbRoom;
import javafx.util.Pair;

@Service
public class RoomServiceImplementation implements RoomService {
    private static final long HOUR = 3_600_000;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RoomRepresentation> getListOfRooms(NearbyRequest request, User user) {
        Map<String, RoomRepresentation> roomRepositoryHashMap = new HashMap<>();
        Map<String, Double> roomDistance = new HashMap<>();

        for (NearbyRoom nearbyRoom : request.getIds()) {
          String id = nearbyRoom.getUUID() + ":" + nearbyRoom.getMajor() + ":" + nearbyRoom.getMinor();  
            Room room = roomRepository.findByBleIds(id);
            if (room == null) {
                continue;
            }
            boolean favorite = user.getFavorites().containsKey(room.getRoomID());
            Term term;
            RoomRepresentation representation = new RoomRepresentation();

            if (room.getOccupied()) {
                term = getCurrentTerm(room);
                if (term != null) {
                    representation = new RoomRepresentation(room.roomID, room.name, room.occupied,
                            term.getTermID().getStartDate(), term.getTermID().getEndDate(), favorite);
                }
            } else {
                term = getNextTerm(room);
                if (term != null) {
                    representation = new RoomRepresentation(room.roomID, room.name, room.occupied,
                            term.getTermID().getStartDate(), term.getTermID().getEndDate(), favorite);
                }
            }

            if (term == null) {
                // If is available and hasn't upcoming event than set free next 8 hours
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();
                Date hour = new Date(t + 8 * HOUR);
                representation = new RoomRepresentation(room.roomID, room.name, room.occupied, hour, hour, favorite);
            }
            if (roomDistance.containsKey(room.getRoomID())) {
                if (roomDistance.get(room.getRoomID()) > nearbyRoom.getDistance()) {
                    representation.setBleId(id);
                    roomDistance.put(room.getRoomID(), nearbyRoom.getDistance());
                    roomRepositoryHashMap.put(room.getRoomID(), representation);
                }
            } else {
                roomDistance.put(room.getRoomID(), nearbyRoom.getDistance());
                representation.setBleId(id);
                roomRepositoryHashMap.put(room.getRoomID(), representation);
            }
        }
        return new ArrayList<>(roomRepositoryHashMap.values());
    }

    public Pair<Date, Date> parseDates(String startTime, String endTime, String date) {
        String[] dateSeparate = date.split("\\.");
        int year = Integer.parseInt(dateSeparate[2]) - 1900;
        int month = Integer.parseInt(dateSeparate[1]) - 1;
        int day = Integer.parseInt(dateSeparate[0]);

        String[] startTimeSeparate = startTime.split(":");
        int hourS = Integer.parseInt(startTimeSeparate[0]);
        int minuteS = Integer.parseInt(startTimeSeparate[1]);
        Date date1 = new Date(year, month, day, hourS, minuteS, 0);

        String[] endTimeSeparate = endTime.split(":");
        int hourE = Integer.parseInt(endTimeSeparate[0]);
        int minuteE = Integer.parseInt(endTimeSeparate[1]);
        Date date2 = new Date(year, month, day, hourE, minuteE, 0);

        return new Pair<>(date1, date2);
    }

    @Override
    public Room findRoom(String id) {
        return roomRepository.findByRoomID(id);
    }

    @Override
    public void makeRoomOccupied(String id) {
        Room room = roomRepository.findByRoomID(id);
        room.setOccupied(true);
        roomRepository.save(room);
    }

    @Override
    public void makeRoomUnoccupied(String id) {
        Room room = roomRepository.findByRoomID(id);
        room.setOccupied(false);
        roomRepository.save(room);
    }

    @Override
    public boolean checkIfRoomIsAvailable(String id, Date startTime, Date endTime) {
        Room room = roomRepository.findByRoomID(id);
        List<Term> terms = room.getTerms();
        if (terms != null) {
            for (Term term : terms) {
                Interval interval1 = new Interval(startTime.getTime(), endTime.getTime());
                Interval interval2 = new Interval(term.getTermID().getStartDate().getTime(),
                        term.getTermID().getEndDate().getTime());
                if (interval1.overlaps(interval2)) {
                    return false;
                }
            }
        }
        scheduleOccupation(id, startTime, endTime);
        return true;
    }

    public void scheduleOccupation(String id, Date startTime, Date endTime) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                makeRoomOccupied(id);
            }
        }, startTime);
        timer.schedule(new TimerTask() {
            public void run() {
                makeRoomUnoccupied(id);
            }
        }, endTime);
    }

    @Override
    public Term getNextTerm(Room room) {
        List<Term> terms = termRepository.findByRoomAndTermID_StartDateGreaterThanOrderByTermID(room, new Date());
        if (!terms.isEmpty()) {
            return terms.get(0);
        } else
            return null;
    }

    @Override
    public Term getCurrentTerm(Room room) {
        Term term = termRepository.findByRoomAndTermID_StartDateLessThanEqualAndTermID_EndDateGreaterThanEqual(room,
                new Date(), new Date());
        return term;
    }

    @Override
    public List<UserRepresentation> getQueryResponse(String query) {
        List<Room> rooms = roomRepository.findRoomsByNameContainingIgnoreCase(query);
        List<UserRepresentation> userRepresentations = new ArrayList<>();
        for (Room room : rooms) {
            UserRepresentation userRepresentation = new UserRepresentation(room.name, room.getRoomID());
            userRepresentations.add(userRepresentation);
        }
        return userRepresentations;
    }

    @Override
    public RoomDetailsRepresentation getRoomDetails(String query, String username) {
        RoomDetailsRepresentation roomDetailsRepresentation = new RoomDetailsRepresentation();
        Room room = findRoom(query);
        User user = userService.findUser(username);
        Term term1;

        roomDetailsRepresentation.setFavorite(user.getFavorites().containsKey(room.getRoomID()));
        roomDetailsRepresentation.setOccupied(room.getOccupied());

        if (room.getOccupied()) {
            term1 = getCurrentTerm(room);
        } else {
            term1 = getNextTerm(room);
        }

        if (term1 != null) {
            roomDetailsRepresentation.setUntil(term1.getTermID().getEndDate());
        } else {
            Calendar date = Calendar.getInstance();
            long t = date.getTimeInMillis();
            Date hour = new Date(t + 8 * HOUR);
            roomDetailsRepresentation.setUntil(hour);
        }

        List<TermsResponse> terms = new ArrayList<>();
        for (Term term : room.getTerms()) {
            TermsResponse termsResponse = new TermsResponse(term.getTermID().getStartDate(),
                    term.getTermID().getEndDate(), term.getRoom().getName(), term.getTitle(),
                    term.getRoom().getRoomID());
            terms.add(termsResponse);
        }
        roomDetailsRepresentation.setTerms(terms);

        return roomDetailsRepresentation;
    }

    @Override
    public Iterable<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public void delete(String id) {
        Room room = roomRepository.findByRoomID(id);
        List<User> users = userRepository.findByCurentRoom(room);
        for (User user : users) {
            user.setCurentRoom(null);
        }
        userRepository.save(users);

        users = (List<User>) userRepository.findAll();
        for (User user : users) {
            if (user.getFavorites().containsKey(room.getRoomID())) {
                user.getFavorites().remove(room.getRoomID());
            }
        }
        userRepository.save(users);
        List<Term> terms = termRepository.findByRoom(room);
        termRepository.delete(terms);
        roomRepository.delete(room);
    }

    @Override
    public boolean notExists(String roomID) {
        if (roomRepository.findByRoomID(roomID) != null)
            return false;
        return true;
    }

    @Override
    public void newRoom(Room room) {
        try {
            room.setRoomID(addToSensorThingsServer(room) + "");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ServiceFailureException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        roomRepository.save(room);
    }

    @Override
    public void update(Room room, String id) {
        room.setRoomID(id);
        roomRepository.save(room);
        // TODO UPDATING IN SENSOR THINGS SERVER
        SbRoom sbRoom;
        sbRoom = SbFactory.findRoom(id);
        sbRoom.setDescription(room.getName());
        for (String ble : room.getBleIds()) {
            SbBeacon beacon = SbFactory.findOrCreateSbBeacon(ble, room.getName());
            sbRoom.assignBeacon(beacon);
        }
    }

    private Id addToSensorThingsServer(Room room)
            throws URISyntaxException, ServiceFailureException, MalformedURLException {
        SensorThingsService service = null;
        try {
            service = Constants.getService();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }

        Map<String, Long> bleBeaconMap = new HashMap<>();
        Thing thing = new Thing();
        thing.setName(room.getName());
        thing.setDescription(room.getName());

        Location location = new Location();
        location.setName("location name 1");
        location.setDescription("location 1");
        location.setLocation(new Point(-117.05, 51.05));
        location.setEncodingType("application/vnd.geo+json");
        thing.getLocations().add(location);

        service.create(thing);
        {
            for (String ble : room.getBleIds()) {
                UnitOfMeasurement um1 = new UnitOfMeasurement("Meter", "m",
                        "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Meter");
                Datastream ds1 = new Datastream("datastream name 1", "datastream 1",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement", um1);
                ds1.setObservedProperty(new ObservedProperty("Proximity m",
                        new URI("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/property"), "proximity"));
                ds1.setSensor(new Sensor(ble, "Ble beacon of room", "application/pdf", "BLE proximity sensor"));
                ds1.setThing(thing);
                service.create(ds1);

                Object idValue = ds1.getId().getValue();
                if (idValue instanceof IdLong) {
                    IdLong idl = (IdLong) idValue;
                    bleBeaconMap.put(ble, idl.value);
                }
            }
        }
        room.setBleDataStream(bleBeaconMap);

        return thing.getId();
    }
}
