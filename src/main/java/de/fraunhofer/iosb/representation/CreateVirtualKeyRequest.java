package de.fraunhofer.iosb.representation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class CreateVirtualKeyRequest {

    private String valid_from;
    private String valid_until;
    private String guest;
    private String guest_email;
    private List<String> authorized_groups;

    public CreateVirtualKeyRequest() {
        authorized_groups = new ArrayList<>();
    }

    public CreateVirtualKeyRequest(String valid_from, String valid_until, String guest, String guest_email, List<String> authorized_groups) {
        this.valid_from = valid_from;
        this.valid_until = valid_until;
        this.guest = guest;
        this.guest_email = guest_email;
        this.authorized_groups = authorized_groups;
    }

    public CreateVirtualKeyRequest(String valid_from, String valid_until, String guest, String guest_email, String... authorized_groups) {
        this(valid_from, valid_until, guest, guest_email, Arrays.asList(authorized_groups));
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getGuest_email() {
        return guest_email;
    }

    public void setGuest_email(String guest_email) {
        this.guest_email = guest_email;
    }

    public List<String> getAuthorized_groups() {
        return authorized_groups;
    }

    public void setAuthorized_groups(List<String> authorized_groups) {
        this.authorized_groups = authorized_groups;
    }
}

