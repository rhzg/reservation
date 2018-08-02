package de.fraunhofer.iosb.rest;

import de.fraunhofer.iosb.Constants;
import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.repository.RoomRepository;
import de.fraunhofer.iosb.representation.NearbyRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SensorServerController
{
    private RoomRepository repoRoom;

    private SensorThingsService service;



    @Autowired
    public SensorServerController(RoomRepository repoRoom)
    {
        try {
            service = Constants.getService();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.repoRoom = repoRoom;
    }

    public void addDistanceMeasurement(ArrayList<NearbyRoom>  rooms, User user) throws ServiceFailureException, URISyntaxException {
        for (NearbyRoom nearbyRoom : rooms)
        {
            Room room = repoRoom.findByBleIds(nearbyRoom.getId());
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("user", createHashUsername(user.getUsername()));

            Datastream datastream = service.datastreams().find(room.getBleDataStream().get(nearbyRoom.getId()));
            if(datastream != null)
            {
                Observation observation = new Observation(nearbyRoom.getDistance(), ZonedDateTime.now());
                observation.setDatastream(datastream);
                observation.setParameters(stringMap);
                service.create(observation);
            }
        }
    }

    private String createHashUsername(String username)
    {
        String generatedUsername = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(username.getBytes());

            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedUsername = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedUsername;
    }
}
