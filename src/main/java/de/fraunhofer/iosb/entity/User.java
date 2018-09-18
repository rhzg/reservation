package de.fraunhofer.iosb.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class User
{
    @Override
    public String toString() {
        return username;
    }

    @Id
    public String username;

    @NotNull
    public String password;

    @Column(unique=true)
    public String token;

    @NotNull
    public String name;

    @Column(unique=true)
    public String nfccode;

    @NotNull
    public String email;

    @NotNull
    public String number;

    @ManyToOne(optional=true)
    @JoinColumn(name="RoomID",referencedColumnName="roomID")
    private Room curentRoom;

    @ManyToMany
    private List<Term> terms = new ArrayList<>();

    @OneToMany(mappedBy="user")
    private List<Role> role = new ArrayList<>();

    @ManyToMany
    private Map<String, Room> favorites = new HashMap<>();

    public User(String username, String password, String name, String email, String number) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public User(String username, String password, String token, String name, String email, String number) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public User(){
    }

}
