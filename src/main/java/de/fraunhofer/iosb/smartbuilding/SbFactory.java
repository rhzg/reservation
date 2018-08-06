package de.fraunhofer.iosb.smartbuilding;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.Utils;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SbFactory.class);

	private static final String TAG_ROOM_NR = "roomNr";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SENSOR_TYPE = "sensorType";
	private static final String TAG_SENSOR_ID = "sensorId";
	private static final String TAG_FLOOR = "floor";

	private static final String TAG_BEACON_ID = "beaconId";
	private static final String TAG_BEACON_LOCATION = "@toThing";
	private static final String VALUE_TYPE_BEACON = "beacon";
	static final String BEACON_PROXIMITY_SENSOR = "Proximity";
	static final String BEACON_BATTERY_SENSOR = "Battery";

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

	public static List<SbBeacon> getBeaconList() throws ServiceFailureException {
		EntityList<Thing> things = service.things().query().filter("properties/type eq 'beacon'").list();
		List<SbBeacon> beacons = new ArrayList<SbBeacon>();
		for (Thing t : things) {
			SbBeacon r = new SbBeacon(t);
			beacons.add(r);
		}
		return beacons;
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

	public static SbBeacon createBeacon(String beaconId, String description) throws ServiceFailureException, URISyntaxException {
		Map<String, Object> props = new HashMap<>();
		props.put(TAG_BEACON_ID, beaconId);
		props.put(TAG_TYPE, VALUE_TYPE_BEACON);

		Thing beaconThing = findOrCreateThing(service, filterProperty(props, TAG_BEACON_ID), beaconId, description, 0, 0, props);

		Map<String, Object> sensorProperties = new HashMap<>();
		sensorProperties.put(TAG_BEACON_ID, beaconId);
		sensorProperties.put(TAG_TYPE, VALUE_TYPE_BEACON);

		Sensor proximitySensor = findOrCreateSensor(service, BEACON_PROXIMITY_SENSOR, "Kontakt.IO proximity sensor", sensorProperties);
        UnitOfMeasurement um1 = new UnitOfMeasurement("Meter", "m", "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Meter");
        ObservedProperty op1 = new ObservedProperty("Proximity m", new URI("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/property"), "proximity");        
        Datastream ds1 = findOrCreateDatastream(service, BEACON_PROXIMITY_SENSOR, "Proximity in Meter", null, um1, beaconThing, op1, proximitySensor);

		Sensor batterySensor = findOrCreateSensor(service, BEACON_BATTERY_SENSOR, "Kontakt.IO battery sensor", sensorProperties);
        UnitOfMeasurement um2 = new UnitOfMeasurement("Percent", "%", "http://www.qudt.org/qudt/owl/1.0.0/unit/index.html#CountingUnit");
        ObservedProperty op2 = new ObservedProperty("battery level", new URI("http://www.qudt.org/qudt/owl/1.0.0/unit/index.html#CountingUnit"), "percent");
        Datastream ds2 = findOrCreateDatastream(service, BEACON_BATTERY_SENSOR, "Battery Level in Percent", null, um2, beaconThing, op2, batterySensor);
		
        SbBeacon beacon = new SbBeacon(beaconThing);
		return beacon;
	}

	// Helper functions ************************************************************************************************
	
	
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

    public static Sensor findOrCreateSensor(SensorThingsService service, String name, String description, Map<String, Object> properties) throws ServiceFailureException {
        EntityList<Sensor> sensorList = service.sensors().query().filter("name eq '" + name + "'").list();
        if (sensorList.size() > 1) {
            throw new IllegalStateException("More than one sensor with name " + name);
        }
        Sensor sensor;
        if (sensorList.size() == 1) {
            sensor = sensorList.iterator().next();
        } else {
            LOGGER.info("Creating Sensor {}.", name);
            sensor = new Sensor(name, description, "text", "Properties not known");
            sensor.setProperties(properties);
            service.create(sensor);
        }
        return sensor;
    }

    
    public static Datastream findOrCreateDatastream(SensorThingsService service, String name, String desc, Map<String, Object> properties, UnitOfMeasurement uom, Thing t, ObservedProperty op, Sensor s) throws ServiceFailureException {
        EntityList<Datastream> datastreamList = service.datastreams().query().filter("name eq '" + Utils.escapeForStringConstant(name) + "'").list();
        if (datastreamList.size() > 1) {
            throw new IllegalStateException("More than one datastream with name " + name);
        }
        Datastream ds;
        if (datastreamList.size() == 1) {
            ds = datastreamList.iterator().next();
        } else {
            LOGGER.info("Creating Datastream {}.", name);
            ds = new Datastream(
                    name,
                    desc,
                    "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                    uom);
            ds.setProperties(properties);
            ds.setThing(t);
            ds.setSensor(s);
            ds.setObservedProperty(op);
            service.create(ds);
        }
        return ds;
    }

}
