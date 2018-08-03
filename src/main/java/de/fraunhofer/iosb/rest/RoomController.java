package de.fraunhofer.iosb.rest;

import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.services.RoomService;
import de.fraunhofer.iosb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class RoomController {
	@Autowired
	RoomService service;

	@Autowired
	UserService userService;

	@RequestMapping(value = "/web", method = RequestMethod.GET)
	public String roomIndex(Model model, Model model1, Principal principal) {
		Iterable<Room> roomIterable = service.findAll();
		model.addAttribute("rooms", roomIterable);
		model1.addAttribute("room", new Room());
		return "rooms";
	}

	@RequestMapping("/web/rooms/{id}")
	public String roomDetails(@PathVariable("id") String id, Model model, Principal principal) {
		Room room = service.findRoom(id);
		model.addAttribute("room", room);
		Iterable<Room> roomIterable = service.findAll();
		model.addAttribute("rooms", roomIterable);
		return "roomDetails";
	}

	@RequestMapping("/web/rooms/delete/{id}")
	public String roomDelete(@PathVariable("id") String id, Model model) {
		// delete room from internal data
		service.delete(id);
		return "redirect:/web";

		// TODO: what about the rooms in the STS?
		// currently they will reappear after restart, as they are managed externally by the smart building service
	}

	@RequestMapping(value = "/web/rooms", method = RequestMethod.POST)
	public String newRoom(@ModelAttribute Room room, Model model) {
		model.addAttribute("room", room);
		if (service.notExists(room.getRoomID())) {
			service.newRoom(room);
			return "redirect:/web";
		} else
			return "redirect:/web/rooms/error";
	}

	@RequestMapping("/web/room/users/{id}")
	public String userInRoom(@PathVariable("id") String id, Model model) {
		Iterable<User> userIterable = userService.findAllInRoom(id);
		model.addAttribute("users", userIterable);
		return "curentusers";
	}

	@RequestMapping(value = "/web/rooms/update/{id}", method = RequestMethod.POST)
	public String roomUpdate(@PathVariable("id") String id, Room room) {
		service.update(room, id);
		return "redirect:/web/rooms/" + id;
	}
}
