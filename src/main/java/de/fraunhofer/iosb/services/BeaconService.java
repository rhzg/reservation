package de.fraunhofer.iosb.services;

import java.util.List;

import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.smartbuilding.SbBeacon;

public interface BeaconService {
	List<SbBeacon> getBeacons();
	
	SbBeacon assignToRoom(String beaconId, String roomId);
}
