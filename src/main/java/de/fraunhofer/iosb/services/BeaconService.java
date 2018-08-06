package de.fraunhofer.iosb.services;

import java.util.List;

import de.fraunhofer.iosb.smartbuilding.SbBeacon;

public interface BeaconService {
	List<SbBeacon> getBeacons();
}
