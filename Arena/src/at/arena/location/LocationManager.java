package at.arena.location;


import java.io.File;

import java.io.IOException;


import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;



public class LocationManager {
	private final String FILEPATH = "C:\\Users\\chris\\Desktop\\localhost\\plugins\\TicketsToMyDownfall\\arenalocations.yml";

	
	public LocationManager() {
		
	}
	
	public void writeToYAML(ArenaLocation arenaLocation) {
		File file = new File(FILEPATH);
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		//yamlConfiguration.set("a", "b");#
		ConfigurationSection baseSection = getSection(yamlConfiguration,"arenas");
		ConfigurationSection arenaSection = getSection(baseSection,arenaLocation.getArenaName());	
		List<ArenaLocation> arenaLocationList = readYAML();				
		arenaSection.set(arenaLocation.getLocationNumber(), serializeLocation(arenaLocation.getLocation()));	    
		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {}
	}

	
	public List<ArenaLocation> readYAML() { 
		List<ArenaLocation> arenaLocationList = new ArrayList<>();
		File file = new File(FILEPATH);
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection baseSection = getSection(yamlConfiguration, "arenas");
		Object[] arenaNames = baseSection.getKeys(false).toArray();
		for(Object arenaName:arenaNames) {
			ConfigurationSection arenaSection = getSection(baseSection, arenaName.toString());
			Map<String, Object> arenaMap = arenaSection.getValues(false);
			for(Map.Entry<String, Object> entry:arenaMap.entrySet()) {
				ArenaLocation arenaLocation = new ArenaLocation(entry.getKey(), deserializeLocation(entry.getValue().toString()), arenaName.toString());
				arenaLocationList.add(arenaLocation);
			}		
		}
		return arenaLocationList;
	}
	
	public String serializeLocation(Location location) { 
		return location.getWorld().getName()+";"+location.getX()+";"+location.getY()+";"+location.getZ()+";"+location.getYaw()+";"+location.getPitch();	
	}
	
	public Location deserializeLocation(String s) { 
		String[] parts = s.split(";");
		Location location = new Location(Bukkit.getServer().getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));		
		return location;
	}
	
	public ConfigurationSection getSection(ConfigurationSection section,String name) {	
		return (section.contains(name)) ? section.getConfigurationSection(name.toLowerCase()) : section.createSection(name.toLowerCase());	
	}

}
