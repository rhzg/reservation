package de.fraunhofer.iosb;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.fraunhofer.iosb.entity.Role;
import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.repository.RoleRepository;
import de.fraunhofer.iosb.repository.RoomRepository;
import de.fraunhofer.iosb.repository.UserRepository;
import de.fraunhofer.iosb.smartbuilding.SbFactory;
import de.fraunhofer.iosb.smartbuilding.SbRoom;

@Component
public class Initialization implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(Initialization.class);

    private RoomRepository repoRoom;
    private UserRepository userRepo;
    private RoleRepository roleRepo;
    private SensorThingsService service;

    @Autowired
    public Initialization(RoomRepository repoRoom, UserRepository userRepo, RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
        this.repoRoom = repoRoom;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Room r18 = new Room("hiwi_1", "HIWI 1", "token");
        r18.getBleIds().add("10655:42221");
        r18.getBleIds().add("62834:31868");
        repoRoom.save(r18);

        Room r19 = new Room("hiwi_2", "HIWI 2", "token");
        r19.getBleIds().add("623:61687");
        repoRoom.save(r19);

        Room r22 = new Room("C8-22", "http://localhost:8080/rooms/2", "neki token");
        repoRoom.save(r22);

        Room all = new Room("all", "http://localhost:8080/rooms/all", "neki token");
        repoRoom.save(all);

        User userAdmin = new User("admin@fer.hr", "$2a$06$/TmUi.A5awl8wBaqkjbHtuInaEGn8ly4onEwPkK/dBy3YK6MXWebq",
                "Admin Admin", "admin@fer.hr", "0");
        userRepo.save(userAdmin);

        User userAdmin1 = new User("admin1@fer.hr", "admin1", "Viseslav Admin", "admin@fer.hr", "0");
        userAdmin1.getFavorites().put(r22.getRoomID(), r22);
        userAdmin1.setToken("e67a3e52-24d8-44cc-bec7-5bd2371c55d9");
        userRepo.save(userAdmin1);

        User user18 = new User("user18", "user18", "User18 User18", "user18@fer.hr", "1");
        user18.setToken("1fa5fbc4-83b7-49cf-b40b-5cc61a3cf657");
        user18.setCurentRoom(r18);
        userRepo.save(user18);

        User user22 = new User("user22", "user22", "User22 User22", "user22@fer.hr", "2");
        user22.setNfccode("12345678");
        user22.setCurentRoom(r18);
        userRepo.save(user22);

        Role roleAdmin = new Role();
        roleAdmin.setRoom(all);
        roleAdmin.setRole("admin");
        roleAdmin.setUser(userAdmin1);
        roleRepo.save(roleAdmin);

        addUserForRoom(r18, "bb46865", "bilokoji", "Boris Belaj", "boris.belaj@fer.hr", "3");
        addUserForRoom(r18, "luka", "svejedno", "Luka Ušalj", "luka.usalj@fer.hr", "4");
        addUserForRoom(r18, "vatroslav", "neznam", "Vatroslav Bajt", "vatroslav.bajt@fer.hr", "5");
        addUserForRoom(r18, "mario ", "kusek", "Mario Kušek", "mario.kusek@fer.hr", "6");
        addUserForRoom(r18, "marko", "pavelic", "Marko Pavelić", "marko.pavelic@fer.hr", "7");

        /*
         * use the following code only during testing service =
         * Constants.createService(); Constants.deleteAll(service);
         * 
         * for(Room room : repoRoom.findAll()) { addToSensorThingsServer(room);
         * repoRoom.save(room); }
         */
        SbFactory.initialize(Constants.getService());
        List<SbRoom> rl = SbFactory.getRoomList();
        for (SbRoom sbr : rl) {
            Room newRoom = new Room(sbr.getName(), sbr.getDescription(), sbr.getToken());
            repoRoom.save(newRoom);
        }
    }

    private void addUserForRoom(Room room, String username, String password, String name, String email, String id) {
        LOG.info("Initialized user '{}' pass '{}' with open roll for room {}", username, password, room.getRoomID());
        User userBB = new User(username, password, name, email, id);
        userRepo.save(userBB);
    }

    private void addToSensorThingsServer(Room room)
            throws URISyntaxException, ServiceFailureException, MalformedURLException {
        Map<String, Long> bleBeaconMap = new HashMap<>();
        Thing thing = new Thing();
        thing.setName(room.getRoomID());
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
                /*
                 * what to do if id is not a IdLong? bleBeaconMap.put(ble, ds1.getId());
                 */
            }
        }
        room.setBleDataStream(bleBeaconMap);
    }

}
