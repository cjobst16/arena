package at.arena.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import at.arena.location.ArenaLocation;
import at.arena.location.LocationManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class Lobby implements Listener {

	private Inventory inventory;
	private List<Player> teamA;
	private List<Player> teamB;
	private Boolean isInviting = false;
	private String team = "";
	private Boolean isGameRunning = false;
	
	public Lobby(Inventory inventory, List<Player> teamA, List<Player> teamB) {
		this.inventory = inventory;
		this.teamA = teamA;
		this.teamB = teamB;
		
	}
	
	public void openInventory(Player p) {
		p.openInventory(getInventory());
	}

	@EventHandler
	public void onPutOut(InventoryClickEvent e) {
		if (e.getView().getTitle().equals("§3Arena Lobby | host: " + teamA.get(0).getName())) {
			if (e.getWhoClicked() instanceof Player) {
				Player player = (Player) e.getWhoClicked();
				e.setCancelled(true);
				if (player.getName().equals(teamA.get(0).getName())) {
					switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
					case "§6Spieler hinzufügen":
						int slot = e.getSlot();
						invitePlayer(slot);
						break;
					case "§6Spiel starten!":
						if(gameIsStartable()) {
							if(isGameRunning == false) {
								Inventory inv = createArenaInventory();
								teamA.get(0).openInventory(inv);
							}			
						}	
					case "§6Lobby verlassen":
						
						break;
					}
				}
			}
		}else if(e.getView().getTitle().equals("§3Wähle eine Map")) {
			if(e.getWhoClicked() instanceof Player) {
				Player player = (Player) e.getWhoClicked();
				e.setCancelled(true);
				String arenaName = e.getCurrentItem().getItemMeta().getDisplayName();
				LocationManager locationManager = new LocationManager();
				List<ArenaLocation> arenaLocationList = locationManager.readYAML();
				List<ArenaLocation> filteredLocationList = arenaLocationList.stream().filter(a->a.getArenaName().equals(arenaName)).collect(Collectors.toList());
				isGameRunning = true;			
				Game game = new Game(filteredLocationList,teamA,teamB);
				PluginManager pm = Bukkit.getServer().getPluginManager();
				pm.registerEvents(game, pm.getPlugin("Arena"));
				
				
			}
		}
	}

	public void invitePlayer(int slot) {
		int size = 0;
		switch (slot) {
		case 5:
			team = "Blau";
			size = getTeamA().size();
			
			break;
		case 14:
			team = "Rot";
			size = getTeamB().size();
		}
		if(size<=4) {
			teamA.get(0).closeInventory();
			BaseComponent component = new net.md_5.bungee.api.chat.TextComponent("Abbrechen");
			component.setColor(ChatColor.RED);
			component.setBold(true);
			component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/arena"));
			teamA.get(0).sendMessage("§c[Arena] §f[Team " + team + "]" + " (" + size
					+ "/4) Gib den Namen des Spielers ein den du einladen möchtest! ");
			teamA.get(0).spigot().sendMessage(component);
			isInviting = true;
		}else {
			teamA.get(0).sendMessage("§c[Arena]§f Dieses Team ist bereits voll");
		}
		
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (isInviting) {
			e.setCancelled(true);
			String name = e.getMessage();
			Player invitedPlayer = Bukkit.getPlayer(name);
			if (invitedPlayer != null) {
				if(getTeamA().contains(invitedPlayer)||getTeamB().contains(invitedPlayer)) {
					e.getPlayer().sendMessage("§c[Arena]§f Dieser Spieler befindet sich bereits in deiner Lobby!");
					isInviting = false;
				 }else {
					 isInviting = false;
						e.getPlayer().sendMessage(
								"§c[Arena]§f Du hast " + invitedPlayer.getName() + " eingeladen! Kehre mit /arena zum Menü zurück");
						invitedPlayer.sendMessage("§c[Arena]§f Du wurdest von " + e.getPlayer().getName()
								+ " in eine Arena eingeladen! Klicke hier um anzunehmen");
						BaseComponent component = new net.md_5.bungee.api.chat.TextComponent("Annehmen");
						component.setColor(ChatColor.GREEN);
						component.setBold(true);
						component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/arena accept "+teamA.get(0).getName()));
						invitedPlayer.spigot().sendMessage(component);
				 }
				} else {
					e.getPlayer().sendMessage("§c[Arena]§f Dieser Spieler existiert nicht!");
					isInviting = false;
				}
			}
	}

	public void addPlayer(Player p) {
		PlayerHeadTask hostPlayerHead = new PlayerHeadTask(p.getUniqueId());
		hostPlayerHead.runTask((hostPlayerHeadItem)->{
			switch (team) {
			case "Blau":
				getInventory().setItem(1 + getTeamA().size(), hostPlayerHeadItem);
				getTeamA().add(p);
				p.openInventory(getInventory());
				break;
			case "Rot":
				getInventory().setItem(10 + getTeamB().size(), hostPlayerHeadItem);
				getTeamB().add(p);
				p.openInventory(getInventory());
				break;
			}
		});
	}
	
	public void updateInventory(Player p) {
		
		
	}
	
	
	
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	public List<Player> getTeamA() {
		return teamA;
	}
	public void setTeamA(List<Player> teamA) {
		this.teamA = teamA;
	}
	public List<Player> getTeamB() {
		return teamB;
	}
	public void setTeamB(List<Player> teamB) {
		this.teamB = teamB;
	}
	
	public void setIsInviting(Boolean b) {
		isInviting = b;
	}
	
	private Inventory createArenaInventory(){
		Inventory inventory = Bukkit.createInventory(null, 3*9,"§3Wähle eine Map");
		
		LocationManager locationManager = new LocationManager();
		List<ArenaLocation> arenaLocationList = locationManager.readYAML();
		for(ArenaLocation l:arenaLocationList) {
			org.bukkit.inventory.ItemStack map = new org.bukkit.inventory.ItemStack(org.bukkit.Material.MAP);
			ItemMeta itemMetaMap =  map.getItemMeta();
			itemMetaMap.setDisplayName(l.getArenaName());
			itemMetaMap.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
			map.setItemMeta(itemMetaMap);
			inventory.addItem(map);
		}
		
		return inventory;

	}
	
	private Boolean gameIsStartable() {
		Boolean b = true;
		if(teamB.size()==0) {
			b = false;
		}
		return b;
	}
	
	
	
}
