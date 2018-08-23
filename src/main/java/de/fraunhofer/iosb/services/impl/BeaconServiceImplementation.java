package de.fraunhofer.iosb.services.impl;

import java.util.List;

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

    @Override
    public List<SbBeacon> getBeacons() {
        // TODO Auto-generated method stub
        List<SbBeacon> beaconList = null;
        beaconList = SbFactory.getBeaconList();
        return beaconList;
    }

	@Override
	public void assignToRoom(String beaconId, String roomId) {
		SbBeacon beacon = SbFactory.findOrCreateSbBeacon(beaconId, "");

		if (roomId != null && beacon != null)
			beacon.assignRoom(roomId);

	}
    

}
