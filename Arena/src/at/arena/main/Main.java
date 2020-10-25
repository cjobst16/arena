package at.arena.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import at.arena.game.ArenaCommand;

public class Main extends JavaPlugin{
	  
	public void onEnable() {
		Logger log = getLogger();
		log.info("*********Arena++**********");
		log.info("********starting...*******");
		log.info("***********50%************");
		log.info("***********99%************");
		log.info("********complete!*********");
		
		getCommand("arena").setExecutor(new ArenaCommand());
		
	}
	
	public void onDisable() {
	
	}
	
	
}