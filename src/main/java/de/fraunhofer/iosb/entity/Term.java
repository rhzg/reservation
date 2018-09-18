package de.fraunhofer.iosb.entity;

import de.fraunhofer.iosb.entity.key.TermId;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Term
{
    @EmbeddedId
    private TermId termID;

    private String title;

    @ManyToOne(optional=false)
    @MapsId("roomId")
    @JoinColumn(name = "RoomID")
    private Room room;


    @ManyToOne(optional=false)
    @JoinColumn(name = "Username")
    private User user;

    @ManyToMany
    private List<User> users = new ArrayList<>();

    public Term(TermId termID, String title, Room room, User user, List<User> users) {
        this.termID = termID;
        this.title = title;
        this.room = room;
        this.user = user;
        this.users = users;
    }

    public Term() {
    }

    
}
