package de.fraunhofer.iosb.rest;


import de.fraunhofer.iosb.entity.Role;
import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.repository.RoleRepository;
import de.fraunhofer.iosb.repository.RoomRepository;
import de.fraunhofer.iosb.repository.UserRepository;
import de.fraunhofer.iosb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.fraunhofer.iosb.services.TermService;
import de.fraunhofer.iosb.services.RoomService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserController
{
    @Autowired
    UserService service;
    
    @Autowired
    TermService termService;
    
    @Autowired
    RoomService roomService;
    
	@Autowired
    private static RoomRepository roomRepo;
	
	@Autowired
    private static RoleRepository roleRepo;

    @RequestMapping("/web/users")
    public String userIndex(Model model, Model model1, Principal principal)
    {

        Iterable<User> userIterable = service.findAll();
        model.addAttribute("users", userIterable);
        model1.addAttribute("user", new User());
        return "users";
    }

    @RequestMapping("/web/users/{id}")
    public String userDetails(@PathVariable("id") String id, Model model)
    {
        if(id.contains("@fer"))
        {
            id += ".hr";
        }
        if(id.contains("@iosb"))
        {
            id +=".de";
        }
        User user = service.findUser(id);
        model.addAttribute("user", user);
        return "userDetails";
    }

    @RequestMapping("/web/users/delete/{id}")
    public String userDelete(@PathVariable("id") String id, Model model)
    {
        service.delete(id);
        return "redirect:/web/users";
    }
    @RequestMapping("/web/users/edit/{id}")
    public String userEdit(@PathVariable("id") String id, Model model)
    {
        User user = service.findUser(id);
        model.addAttribute("user", user);
        return "userForm";
    }

    @RequestMapping(value = "/web/users/{id}", method = RequestMethod.POST)
    public String userUpdate(@PathVariable("id") String id,User user)
    {
        if(!(user.getUsername().equals(id))){
            if(service.notexists(user.getUsername())){
                if(user.getUsername() == null)
                    service.save(user);
                else
                    service.update(user, id	);
                return "redirect:/web/users/" + user.getUsername();
            }
            else return "redirect:/web/users/error";
        }
        else{
            if(user.getUsername() == null)
                service.save(user);
            else
                service.update(user, id	);
            return "redirect:/web/users/" + user.getUsername();
        }
    }

    @RequestMapping(value= "/web/user/new", method = RequestMethod.POST)
    public String newUser(User user){
    	
//    	User user1 = service.findUser("admin1@fer.hr");   
		
//		Room all = new Room("all", "http://localhost:8080/rooms/all", "neki token");
//        roomRepo.save(all);
//
//
//        Role roleAdmin = new Role();
//        roleAdmin.setRoom(all);
//        roleAdmin.setRole("admin");
//        roleAdmin.setUser(user1);
//        roleRepo.save(roleAdmin);
        
        //userService.save(user);
    	
//    	Date from = new Date();
//    	User visitor = new User("dada", "dada", "dada", "da@da.com", "0");
//    	//service.save(visitor);
//    	ArrayList<User> list = new ArrayList<User>();
//    	//list.add(visitor);
//    	//list.add(user);
//    	list.add(user1);
//    	
//    	ArrayList<Room> rooms = (ArrayList<Room>) roomService.findAll();
//    	
//    	Date until = new Date();
//    	
//    	String title = "Hello";
//    	
//    	termService.addTerm(user1, list, rooms.get(0), from, until, title);
//    	
//    	return "redirect:/web/users";

        if(service.notexists(user.getUsername())){
            service.save(user);
            return "redirect:/web/users";
        }
        else return "redirect:/web/users/error";
    }

}
