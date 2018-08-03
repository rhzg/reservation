package de.fraunhofer.iosb.smartbuilding;

import static org.junit.Assert.*;
import de.fraunhofer.iosb.Constants;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;

public class FactoryTest {

	@Test
	public void testGetRoomList() {
		try {
			SbFactory.initialize(Constants.getService());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

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
			SbFactory.initialize(Constants.getService());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

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
}
