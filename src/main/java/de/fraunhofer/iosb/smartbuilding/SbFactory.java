package de.fraunhofer.iosb.smartbuilding;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbFactory {
    private static SensorThingsService service;

    public static void initialize (SensorThingsService myService) {
    	service = myService;
    }
    
    public static List<SbRoom> getRoomList () throws ServiceFailureException {
    	
    	EntityList<Thing> things = service.things().query().filter("properties/type eq 'room'").list();
    	List<SbRoom> rooms = new ArrayList<SbRoom>();
    	for (Thing t : things) {
    		SbRoom r = new SbRoom();
    		r.setName(t.getName());
    		r.setDescription(t.getDescription());
    		Map<String, Object> p = t.getProperties();
    		r.setRoomNr(p.get("roomNr").toString());
    		r.setFloor((Integer)(p.get("floor")));
    		rooms.add(r);
    	}
    	
    	return rooms;
    }
    
    public static SbRoom getRoom (String name) throws ServiceFailureException {
    	Thing t = service.things().query().filter("properties/type eq 'room' and name eq '" + name + "'").first();
		SbRoom r = new SbRoom();
		r.setName(t.getName());
		r.setDescription(t.getDescription());
		Map<String, Object> p = t.getProperties();
		r.setRoomNr(p.get("roomNr").toString());
		r.setFloor((Integer)(p.get("floor")));
		return r;
    }
}
