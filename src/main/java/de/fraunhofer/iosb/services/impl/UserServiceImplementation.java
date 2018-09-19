package de.fraunhofer.iosb.services.impl;

import de.fraunhofer.iosb.entity.Role;
import de.fraunhofer.iosb.entity.Room;
import de.fraunhofer.iosb.entity.Term;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.repository.RoleRepository;
import de.fraunhofer.iosb.repository.RoomRepository;
import de.fraunhofer.iosb.repository.UserRepository;
import de.fraunhofer.iosb.representation.RoomRepresentation;
import de.fraunhofer.iosb.representation.TermsResponse;
import de.fraunhofer.iosb.representation.UserDetailsRepresentation;
import de.fraunhofer.iosb.representation.UserRepresentation;
import de.fraunhofer.iosb.services.RoomService;
import de.fraunhofer.iosb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserServiceImplementation implements UserService
{
    @Autowired
    private UserRepository repo;

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoomService roomService;

    private static final long HOUR = 3600*1000;

    @Override
    public List<UserRepresentation> getAllUsersInRepresentation()
    {
        List<UserRepresentation> userRepresentations = new ArrayList<>();

        for (User user : repo.findAll())
        {
            UserRepresentation userRepresentation = new UserRepresentation(user.getName(), user.getUsername());
            userRepresentations.add(userRepresentation);
        }
        return userRepresentations;
    }

    @Override
    public List<User> getUsersByIds(List<String> ids)
    {
        List<User> users = new ArrayList<>();
        for (String id : ids)
        {
            User tmp = repo.findByUsername(id);
            if(tmp != null)
            {
                users.add(repo.findByUsername(id));
            }
        }
        return users;
    }

    @Override
    public User findUser(String name)
    {
        return repo.findByUsername(name);
    }

    @Override
    public void scheduleCurrentRoom(List<User> users, Room room, Date startTime, Date endTime)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                setCurrentRoom(room, users);
            }
        }, startTime);
        timer.schedule(new TimerTask() {
            public void run() {
                unSetCurrentRoom(users);
            }
        }, endTime);
    }

    public void setCurrentRoom(Room room, List<User> users)
    {
        for(User user : users)
        {
            user.setCurentRoom(room);
        }
    }

    public void unSetCurrentRoom(List<User> users)
    {
        for(User user : users)
        {
            user.setCurentRoom(null);
        }
    }

    @Override
    public void makeFavorite(String roomId, User user)
    {
        Room room = roomRepository.findByRoomID(roomId);
        if (user.getFavorites().containsKey(roomId))
        {
            user.getFavorites().remove(roomId);
            repo.save(user);
            return;
        }
        user.getFavorites().put(roomId, room);
        repo.save(user);
    }

    @Override
    public List<RoomRepresentation> getFavoritesRoom(String username)
    {
        List<RoomRepresentation> result = new ArrayList<>();
        User user = repo.findByUsername(username);
        for (Room room : user.getFavorites().values())
        {
            Term term;
            RoomRepresentation representation = new RoomRepresentation();
            if(room.getOccupied())
            {
                term = roomService.getCurrentTerm(room);
                if(term != null)
                {
                    representation = new RoomRepresentation(room.roomID, room.name, room.occupied, term.getTermID().getStartDate(), term.getTermID().getEndDate(), true);
                    representation.setBleIds(room.getBleIds());
                }
            }else
            {
                term = roomService.getNextTerm(room);
                if(term != null)
                {
                    representation = new RoomRepresentation(room.roomID, room.name, room.occupied, term.getTermID().getStartDate(), term.getTermID().getEndDate(), true);
                    representation.setBleIds(room.getBleIds());
                }
            }

            if(term == null)
            {
                //If is available and hasn't upcoming event than set free next 8 hours
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();
                Date hour = new Date(t + 8 * HOUR);
                representation = new RoomRepresentation(room.roomID, room.name, room.occupied, hour, hour, true);
                representation.setBleIds(room.getBleIds());
            }

            result.add(representation);
        }
        return result;
    }

    @Override
    public List<TermsResponse> getTerms(String username)
    {
        User user = findUser(username);
        List<TermsResponse> terms = new ArrayList<>();
        for(Term term : user.getTerms())
        {
            TermsResponse termsResponse = new TermsResponse(term.getTermID().getStartDate(),
                    term.getTermID().getEndDate(), term.getRoom().getName(), term.getTitle(), term.getRoom().getRoomID());
            terms.add(termsResponse);
        }
        return terms;
    }

    @Override
    public List<UserRepresentation> getQueryResponse(String query)
    {

        List<User> users =repo.findUsersByNameContainingIgnoreCase(query);

        List<UserRepresentation> userRepresentations = new ArrayList<>();
        for (User user : users)
        {
            UserRepresentation userRepresentation = new UserRepresentation(user.getName(), user.getUsername());
            userRepresentations.add(userRepresentation);
        }
        return userRepresentations;
    }

    @Override
    public UserDetailsRepresentation getUserDetails(String id)
    {
        User user = findUser(id);

        UserDetailsRepresentation userDetailsRepresentation = new UserDetailsRepresentation();
        userDetailsRepresentation.setEmail(user.getEmail());
        userDetailsRepresentation.setPhoneNumber(user.getNumber());
        userDetailsRepresentation.setName(user.getName());
        userDetailsRepresentation.setTerms(getTerms(id));

        return userDetailsRepresentation;
    }

    @Override
    public User getUserByNFC(String nfcCode)
    {
        return repo.findByNfccode(nfcCode);
    }

    @Override
    public Iterable<User> findAllInRoom(String id)
    {
        List<User> userIterable= new ArrayList<>();
        for(User user:repo.findAll()){
            if(user.getCurentRoom()!=null){
                if(user.getCurentRoom().getRoomID().equals(id))
                    userIterable.add(user);
            }
        }
        return userIterable;
    }

    @Override
    public Iterable<User> findAll() {
        return  repo.findAll();
    }

    @Override
    public void delete(String id) {
        repo.delete(id);
    }

    @Override
    public boolean notexists(String username) {
        if(repo.findOne(username) != null){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    @Transactional
    public User save(User user) {
        if(repo.findByUsername(user.getUsername())==null) {
        	Room all = new Room("all", "http://localhost:8080/rooms/all", "nekis token");
        	User savedUser = repo.save(user);
        	Role admin = new Role();
        	admin.setRoom(all);
        	admin.setRole(savedUser.nfccode);
        	admin.setUser(savedUser);
        	roleRepository.save(admin);
        	return savedUser;
        }
        else
            throw new IllegalArgumentException("User with that username already exists");
    }

    @Override
    @Transactional
    public void update(User user, String id) {
        repo.save(user);

    }
}

