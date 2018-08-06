package de.fraunhofer.iosb.representation;

public class BeaconRepresentation {
	private String minor;
	private String major;

	public BeaconRepresentation(String major, String minor) {
		this.minor = minor;
		this.major = major;
	}

	public String toString () {
		return "BEACON " + major + ":"+ minor;
	}
}
