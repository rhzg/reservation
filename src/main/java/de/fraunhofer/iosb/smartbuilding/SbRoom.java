package de.fraunhofer.iosb.smartbuilding;

public class SbRoom {
	private String name;
	private String description;
	private String roomNr;
	private int floor;
	private String token = "undefined";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoomNr() {
		return roomNr;
	}

	public void setRoomNr(String roomNr) {
		this.roomNr = roomNr;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String toString() {
		return "Room (" + "name=" + name + ", description=" + description + ", roomNr=" + roomNr + ", floor=" + floor
				+ ")";
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
