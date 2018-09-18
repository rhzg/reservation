package de.fraunhofer.iosb.entity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

import java.util.*;

@Data
@Entity
public class Room {

    @Override
    public String toString() {
        return roomID;
    }

    @Id
    public String roomID;

    @ElementCollection(targetClass=String.class)
    public Set<String> bleIds = new HashSet<>();

    @ElementCollection(targetClass=Long.class)
    public Map<String, Long> bleDataStream= new HashMap<>();

    public Boolean occupied = false;

    public String name;

    public String token;

    @OneToMany(mappedBy="curentRoom")
    private final List<User> curentUsers = new ArrayList<User>();

    @OneToMany(mappedBy="room")
    private final List<Term> terms = new ArrayList<Term>();

    public Room(){}

    public Room(String roomID, String name, String token)
    {
        this.roomID = roomID;
        this.name = name;
        this.token = token;
    }

    public Room(String name, String token) {
        this.name = name;
        this.token = token;
    }

}
