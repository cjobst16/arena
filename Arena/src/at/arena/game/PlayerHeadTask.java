package at.arena.game;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PlayerHeadTask{
	private UUID uuid;
	
	public PlayerHeadTask(UUID uuid){
		this.uuid = uuid;
	}
	
	public org.bukkit.inventory.ItemStack getHead() {
		//org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
		
	   // SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		//skullMeta.setOwner(playerName);
		//skullMeta.setDisplayName(playerName+" Barbar level 100");
		//itemStack.setItemMeta(skullMeta);
		//String respon = runAsysncTask();
		//System.out.println(respon);
		return null;
		
	}
	
	public void runTask(Consumer<org.bukkit.inventory.ItemStack> callback) {
		new BukkitRunnable() {	
			
			@Override
			public void run() {
				try {
					org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
					SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
					skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
					skullMeta.setDisplayName(Bukkit.getPlayer(uuid).getName()+" Barbar level 100");
					itemStack.setItemMeta(skullMeta);
					cancel();
					callback.accept(itemStack);			
				}catch(Exception e) {
						
				}				
			}
		}.runTask(Bukkit.getPluginManager().getPlugin("Arena"));
	}
	
	 
	
	
	
 
}
