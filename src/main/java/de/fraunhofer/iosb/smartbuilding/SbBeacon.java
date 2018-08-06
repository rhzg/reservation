package de.fraunhofer.iosb.smartbuilding;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Thing;

public class SbBeacon {
	private Thing myThing;
	
	public SbBeacon(Thing thing) {
		// TODO Auto-generated constructor stub
		myThing = thing;
	}
	
	public String toString() {
		return "BLE Beacon: " + getName() + " " + getDescription();
	}

	public String getName () {
		return myThing.getName();
	}
	
	public String getDescription() {
		return myThing.getDescription();
	}
	
	public String getRoom() {
		return "undefined";
	}
	
	public void assignRoom(String roomName) throws ServiceFailureException {
		Datastream ds = myThing.datastreams().find(SbFactory.BEACON_PROXIMITY_SENSOR);
		SbRoom room = SbFactory.getRoom(roomName);
		room.assignProximityDatastream (ds);
	}
}
