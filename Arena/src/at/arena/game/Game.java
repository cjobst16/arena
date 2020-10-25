package at.arena.game;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import at.arena.location.ArenaLocation;

public class Game implements Listener {
	private List<ArenaLocation> arenaLocationList;
	private List<Player> teamA;
	private List<Player> teamB;
	private Boolean isCountdown;
	public Game(List<ArenaLocation> arenaLocationList, List<Player> teamA, List<Player> teamB) {
		this.arenaLocationList = arenaLocationList;
		this.teamA = teamA;
		this.teamB = teamB;
		startGame();
		//clearTeams();
	}
	
	private void startGame() {
		for(ArenaLocation arenaLocation : arenaLocationList) {
			String locationNumber = arenaLocation.getLocationNumber();
			Location location = arenaLocation.getLocation();
			for(int i =1;i<=teamA.size();i++) {
				if(locationNumber.equals("a"+i)) {
					teamA.get(i-1).sendMessage("§c[Arena]§6 Du wirst nun in die Arena teleportiert...");
					teamA.get(i-1).teleport(location);
				}
			}
			for(int i =1;i<=teamB.size();i++) {
				if(locationNumber.equals("b"+i)) {
					teamB.get(i-1).sendMessage("§c[Arena]§6 Du wirst nun in die Arena teleportiert...");
					teamB.get(i-1).teleport(location);
				}
			}
		}
		isCountdown = true;
		for(Player p: teamA) {
			displayCountdown(p);
			setPotionEffects(p);
		}
		
		for(Player p: teamB) {
			displayCountdown(p);
			setPotionEffects(p);
		}
	}
	
	private void displayCountdown(Player p) {	
		
		new BukkitRunnable() {
			int timer = 10;
			public void run() {
				p.sendMessage("§c[Arena]§f "+ timer+ " Sekunden bis zum Kampf");
			   
				timer--;
				if(timer<=0) {
					p.sendMessage("§c[Arena]§6 Der Kampf beginnt!");
					isCountdown = false;
					cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Arena"), 20, 20);
	}
	
	private void setPotionEffects(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,5*20,1));
	}
	
	
	
	private List<Player> getTeamPlayerIsIn(Player p) {
		List<Player> team = null;
		if(teamA.contains(p)) {
			team = teamA;
			
		}else if(teamB.contains(p)){
			team = teamB;
		}
		return team;
	}
	
	private List<Player> getOppositeTeam(List<Player> team){
		List<Player> oppositeTeam = null;
		if(teamA.equals(team)) {
			oppositeTeam = teamB;
			
		}else {
			oppositeTeam = teamA;
		}
		
		return oppositeTeam;
	}
	
	private void removePlayer(Player p) {
		List<Player> team = getTeamPlayerIsIn(p);
		team.remove(p);
	}
	
	private void clearTeams() {
		Location spawn = Bukkit.getWorld("world").getSpawnLocation();
		for(Player p: teamA)		{
			p.teleport(spawn);
			p.setGameMode(GameMode.SURVIVAL);
		}
		for(Player p: teamB)		{
			p.teleport(spawn);
			p.setGameMode(GameMode.SURVIVAL);
		}
		teamA.clear();
		teamB.clear();
		
	}
	
	
	
	@EventHandler 
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(getTeamPlayerIsIn(p)!= null) {
			e.setKeepInventory(true);
			p.setHealth(0.5);
			e.setKeepLevel(true);
			e.setDeathMessage("");
			p.teleport(p.getLocation());
			p.setGameMode(GameMode.SURVIVAL);
			List<Player> team = getTeamPlayerIsIn(p);
			List<Player> oppositeTeam = getOppositeTeam(team);
			removePlayer(p);
			if(team.size()<=0||oppositeTeam.size()<=0) {
				for(Player player:team) {
					player.sendMessage("§c [Arena] §fDein Team hat das Duell verloren");
					p.teleport(new Location(Bukkit.getWorld("world"), 0, 0, 0));
				}
				
				for(Player player:oppositeTeam) {
					player.sendMessage("§c [Arena] §fDein Team hat das Duell gewonnen");
					p.teleport(new Location(Bukkit.getWorld("world"), 0, 0, 0));
				}
				clearTeams();
			}
		}
	
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(getTeamPlayerIsIn(p)!= null) {		
			p.teleport(new Location(Bukkit.getWorld("world"), 0, 0, 0));	
			List<Player> team = getTeamPlayerIsIn(p);
			List<Player> oppositeTeam = getOppositeTeam(team);
			removePlayer(p);
			if(team.size()<=0||oppositeTeam.size()<=0) {
				for(Player player:team) {
					player.sendMessage("§c [Arena] §fDein Team hat das Duell verloren");
					p.teleport(new Location(Bukkit.getWorld("world"), 0, 0, 0));
				}
				
				for(Player player:oppositeTeam) {
					player.sendMessage("§c [Arena] §fDein Team hat das Duell gewonnen");
					p.teleport(new Location(Bukkit.getWorld("world"), 0, 0, 0));
				}
				clearTeams();
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(isCountdown) {
			e.setCancelled(true);
		}
	}
}
