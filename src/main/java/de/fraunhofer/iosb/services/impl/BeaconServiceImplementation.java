package de.fraunhofer.iosb.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.services.BeaconService;
import de.fraunhofer.iosb.smartbuilding.SbBeacon;
import de.fraunhofer.iosb.smartbuilding.SbFactory;
//import groovy.util.logging.Log;

@Service
public class BeaconServiceImplementation implements BeaconService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeaconServiceImplementation.class);

    @Override
    public List<SbBeacon> getBeacons() {
        // TODO Auto-generated method stub
        List<SbBeacon> beaconList = null;
        beaconList = SbFactory.getBeaconList();
        return beaconList;
    }

	@Override
	public SbBeacon assignToRoom(String beaconName, String roomId) {
		System.err.println("beacon name is " + beaconName);
		System.err.println("room id is " + roomId);
//		SbBeacon beacon = SbFactory.findOrCreateSbBeacon(beaconId, "");
		SbBeacon beacon = SbFactory.findBeacon(beaconName);
		
		System.err.println("beacon got " + beacon.getId() + "id and the name: " + beacon.getName());
		

//		LOGGER.trace("beacon with {} UUID found or created", beacon.getId());

		if (roomId != null && beacon != null) {
			beacon.assignRoom(roomId);
			System.err.println("beacon was assigned " + beacon.getRoomName());
		}
			
		return beacon;

	}
    

}
