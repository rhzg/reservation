package de.fraunhofer.iosb.smartbuilding;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geojson.Point;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.Utils;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbFactory {

	private static final String TAG_ROOM_NR = "roomNr";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SENSOR_TYPE = "sensorType";
	private static final String TAG_SENSOR_ID = "sensorId";
	private static final String TAG_FLOOR = "floor";

	private static final String TAG_BEACON_ID = "beaconId";
	private static final String TAG_BEACON_LOCATION = "@toThing";
	private static final String VALUE_TYPE_BEACON = "beacon";

	private static final String VALUE_TYPE_ROOM = "room";
	private static final String VALUE_TYPE_HALL = "hallway";
	private static final String VALUE_TYPE_OUTSIDE = "outside";
	private static final String VALUE_TYPE_SENSOR = "sensor";
	private static final String VALUE_TYPE_SENSORTYPE = "sensorType";

	private static SensorThingsService service;

	public static void initialize(SensorThingsService myService) {
		service = myService;
	}

	public static List<SbRoom> getRoomList() throws ServiceFailureException {

		EntityList<Thing> things = service.things().query().filter("properties/type eq 'room'").list();
		List<SbRoom> rooms = new ArrayList<SbRoom>();
		for (Thing t : things) {
			SbRoom r = new SbRoom();
			r.setName(t.getName());
			r.setDescription(t.getDescription());
			Map<String, Object> p = t.getProperties();
			r.setRoomNr(p.get("roomNr").toString());
			r.setFloor((Integer) (p.get("floor")));
			rooms.add(r);
		}

		return rooms;
	}

	public static SbRoom getRoom(String name) throws ServiceFailureException {
		Thing t = service.things().query().filter("properties/type eq 'room' and name eq '" + name + "'").first();
		SbRoom r = null;
		if (t != null) {
			r = new SbRoom();
			r.setName(t.getName());
			r.setDescription(t.getDescription());
			Map<String, Object> p = t.getProperties();
			r.setRoomNr(p.get("roomNr").toString());
			r.setFloor((Integer) (p.get("floor")));
		}
		return r;
	}

	public static SbRoom createSbRoom(String name, String description) throws ServiceFailureException {
		SbRoom room = new SbRoom();
		Map<String, Object> props = new HashMap<>();
		props.put(TAG_TYPE, VALUE_TYPE_ROOM);
		props.put(TAG_ROOM_NR, name);
		Thing thing = findOrCreateThing(service, filterProperty(props, TAG_TYPE), name, description, 0, 0, props);
		room.setMyThing(thing);
		return room;
	}

	public static Thing createBeaconThing(String beaconId, Id roomId) throws ServiceFailureException {
		Map<String, Object> props = new HashMap<>();
		props.put(TAG_BEACON_ID, beaconId);
		props.put(TAG_TYPE, VALUE_TYPE_BEACON);
		props.put(TAG_BEACON_LOCATION, roomId);
		String description = "Beacon";
		if (roomId != null)
			description = description + " for room " + roomId;
		else
			description = description + " not yet assigned";

		Thing thing = findOrCreateThing(service, filterProperty(props, TAG_BEACON_ID), beaconId, description, 0, 0,
				props);
		return thing;
	}

	public static String quoteForUrl(Object in) {
		if (in instanceof Number) {
			return in.toString();
		}
		return "'" + Utils.escapeForStringConstant(in.toString()) + "'";
	}

	private static String filterProperty(Map<String, Object> properties, String property) {
		Object value = properties.get(property);
		String valueString = quoteForUrl(value);
		return "properties/" + property + " eq " + valueString;
	}

	private static Thing findOrCreateThing(SensorThingsService service, String filter, String name, String description,
			double lon, double lat, Map<String, Object> properties) throws ServiceFailureException {
		EntityList<Thing> thingList;
		if (filter == null) {
			filter = "name eq " + quoteForUrl(name);
		}
		thingList = service.things().query().filter(filter).expand("Locations").list();
		if (thingList.size() > 1) {
			throw new IllegalStateException("More than one thing found with filter " + filter);
		}
		Thing thing;
		if (thingList.size() == 1) {
			thing = thingList.iterator().next();
		} else {
			thing = new Thing(name, description);
			thing.setProperties(properties);
			service.create(thing);

			if (lat != 0 && lon != 0) {
				Location location = new Location(name, "Location of Thing " + name + ".", "application/vnd.geo+json",
						new Point(lon, lat));
				location.getThings().add(thing);
				service.create(location);
				thing.getLocations().add(location);
			}
		}
		return thing;
	}

}
