package de.fraunhofer.iosb.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.representation.BeaconRepresentation;
import de.fraunhofer.iosb.services.BeaconService;
import de.fraunhofer.iosb.services.RoomService;
import de.fraunhofer.iosb.services.UserService;

@Controller
public class BeaconController {
	@Autowired
	BeaconService beaconService;
	
	@Autowired
	RoomService roomService;

	@GetMapping("/web/beacons")
	public String beacons(@RequestParam(name="name", required=false) String name, Model model) {
		model.addAttribute("name", name);
		model.addAttribute("beacons", beaconService.getBeacons());
		return "beacons";
	}
	
	@GetMapping("/assignRoom")
	public String assignRoom(@RequestParam(name="name") String name, Model model, Model roomModel) {
		model.addAttribute("name", name);
		Iterable<Room> roomIterable = roomService.findAll();
		roomModel.addAttribute("rooms", roomIterable);
		return "assignRoom";
	}
	
	@PostMapping("/web/beacons/{beaconId}/assign/{roomId}")
	public String assignBeaconToRoom(@PathVariable("beaconId") String beaconId, @PathVariable("roomId") String roomId, Model model) {
		beaconService.assignToRoom(beaconId, roomId);
		
		return "redirect:/web/beacons";
	}
	
	@RequestMapping("/web/beacons/delete/{id}")
	public String deleteBeacon(@PathVariable("id") String id, Model model) {
		beaconService.delete(id);
		
		return "redirect:/web/beacons";
	}
}
