package de.fraunhofer.iosb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.services.BeaconService;
import de.fraunhofer.iosb.smartbuilding.SbBeacon;
import de.fraunhofer.iosb.smartbuilding.SbFactory;

@Service
public class BeaconServiceImplementation implements BeaconService {

    @Override
    public List<SbBeacon> getBeacons() {
        // TODO Auto-generated method stub
        List<SbBeacon> beaconList = null;
        beaconList = SbFactory.getBeaconList();
        return beaconList;
    }

}
