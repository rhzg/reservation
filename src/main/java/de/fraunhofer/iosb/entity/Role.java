package de.fraunhofer.iosb.entity;



import de.fraunhofer.iosb.entity.key.RoleId;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Data
@Entity
public class Role {
	@EmbeddedId
	RoleId id;

	private String role;
	
	@ManyToOne(optional=false)
	@MapsId("roomId")
	private Room room;

	@ManyToOne(optional=false)
    @MapsId("userName")
	private User user;
	
	
	public Role() {
		id = new RoleId();
	}

	
}
