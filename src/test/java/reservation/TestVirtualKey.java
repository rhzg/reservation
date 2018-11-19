package reservation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fraunhofer.iosb.entity.Role;
import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.repository.RoleRepository;
import de.fraunhofer.iosb.repository.RoomRepository;
import de.fraunhofer.iosb.repository.UserRepository;
import de.fraunhofer.iosb.services.RoomService;
import de.fraunhofer.iosb.services.TermService;
import de.fraunhofer.iosb.services.UserService;

public class TestVirtualKey {
	
	@Autowired
    private static UserRepository userRepo;
	
	@Autowired
    private static RoleRepository roleRepo;
	
	@Autowired
	private static RoomRepository repoRoom;
	
	@Autowired
	private static RoomService roomService;
	
	@Autowired
	private static UserService userService;
	
	@Autowired
	private static TermService termService;
	
	static User user1;
	ArrayList<User> list;
	ArrayList<Room> rooms;
	static Date from;
	static Date until;
	static String title;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		user1 = new User("admin@fer.hr", "$2a$06$/TmUi.A5awl8wBaqkjbHtuInaEGn8ly4onEwPkK/dBy3YK6MXWebq",
                "Admin Admin", "admin@fer.hr", "0");
        userRepo.save(user1);
        
        Room all = new Room("all", "http://localhost:8080/rooms/all", "neki token");
        repoRoom.save(all);


        Role roleAdmin = new Role();
        roleAdmin.setRoom(all);
        roleAdmin.setRole("admin");
        roleAdmin.setUser(user1);
        roleRepo.save(roleAdmin);
        
        //userService.save(user);
    	
    	from = new Date();
    	User visitor = new User("dada", "dada", "dada", "da@da.com", "0");
    	//service.save(visitor);
    	ArrayList<User> list = new ArrayList<User>();
    	//list.add(visitor);
    	//list.add(user);
    	list.add(user1);
    	
    	ArrayList<Room> rooms = (ArrayList<Room>) roomService.findAll();
    	
    	until = new Date();
    	
    	title = "Hello";
    	
    	termService.addTerm(user1, list, rooms.get(0), from, until, title);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTheKey() {
		termService.addTerm(user1, list, rooms.get(0), from, until, title);
	}

}
