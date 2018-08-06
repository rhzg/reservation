package de.fraunhofer.iosb.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.fraunhofer.iosb.representation.BeaconRepresentation;
import de.fraunhofer.iosb.services.BeaconService;
import de.fraunhofer.iosb.services.RoomService;
import de.fraunhofer.iosb.services.UserService;

@Controller
public class BeaconController {
	@Autowired
	BeaconService beaconService;

	@GetMapping("/web/beacons")
	public String beacons(@RequestParam(name="name", required=false) String name, Model model) {
		model.addAttribute("name", name);
		model.addAttribute("beacons", beaconService.getBeacons());
		return "beacons";
	}
	
	@GetMapping("/assignRoom")
	public String assignRoom(@RequestParam(name="name") String name, Model model) {
		model.addAttribute("name", name);
		return "assignRoom";
	}
}
