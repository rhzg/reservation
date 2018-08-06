package de.fraunhofer.iosb.smartbuilding;

import static org.junit.Assert.*;
import de.fraunhofer.iosb.Constants;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;

public class FactoryTest {

	@Before
	public void initializeFactory () {
		try {
			SbFactory.initialize(Constants.getService());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}

	@Test
	public void testGetRoomList() {
		try {
			List<SbRoom> rooms = SbFactory.getRoomList();

			for (SbRoom r : rooms) {
				String s = r.toString();
				assertNotNull("room object should have a string representation", s);

			}

		} catch (ServiceFailureException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetRoom() {
		try {
			SbRoom room = SbFactory.getRoom("S012");
			String s = room.toString();
			assertNotNull("room object should have a string representation", s);
			
			room = SbFactory.getRoom("XYZ000");
			assertNull("romm object should not be found",room);

		} catch (

		ServiceFailureException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateBeacon () {
		try {
			SbBeacon beacon = SbFactory.createBeacon("BLE0815", "test beacon");
			String s = beacon.toString();
			assertNotNull("room object should have a string representation", s);
			
		} catch (

		ServiceFailureException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
