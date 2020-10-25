package at.arena.location;

import org.bukkit.Location;

public class ArenaLocation {
	private String locationNumber;
	private Location location;
	private String arenaName;
	public ArenaLocation(String locationNumber, Location location,String arenaName) {
		this.locationNumber = locationNumber;
		this.location = location;
		this.arenaName = arenaName;
	}
	public String getLocationNumber() {
		return locationNumber;
	}
	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getArenaName() {
		return arenaName;
	}
	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}
	
	
	
}
