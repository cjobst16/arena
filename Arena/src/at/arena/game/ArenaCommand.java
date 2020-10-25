package at.arena.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import at.arena.location.ArenaLocation;
import at.arena.location.LocationManager;

public class ArenaCommand implements CommandExecutor {
	List<Lobby> lobbyList = new ArrayList<>();
	
	public org.bukkit.inventory.ItemStack hostPlayerHeadItem;
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String argument, String[] arguments) {
		if (argument.equalsIgnoreCase("Arena")) {
			if (commandSender instanceof Player) {	
				Player player = (Player) commandSender;
				switch (arguments.length) {
				case 0:
					if(getIfPlayerIsInGame(player)==false) {
						Player host = player;
						PlayerHeadTask hostPlayerHead = new PlayerHeadTask(host.getUniqueId());
						hostPlayerHead.runTask((hostPlayerHeadItem)->{
							
							createLobby(host, hostPlayerHeadItem);
						});
					}else {
						Lobby l = getLobbyPlayerIsIn(player);
						l.setIsInviting(false);
						if(l != null) {
							l.openInventory(player);
						}else {						
						}
					}					
					break;								
				case 1:
					if (arguments[0].equalsIgnoreCase("location")) {
						// if(host.hasPermission("xyz"));
						player.sendMessage("§c[Arena]§f Falsche Eingabe -> /arena location a1-4 || b1-4  world");
					
					} else {
						player.sendMessage("§c[Arena]§f Falsche Eingabe -> /help Arena");
					}
					break;
				
				
				case 2:				
					if (arguments[0].equalsIgnoreCase("accept")) { 
						if(getIfPlayerIsInGame(player)) {
							player.sendMessage("§c[Arena]§f Du befindest dich bereits in einer Arena Lobby");
						}else {
							String hostPlayerName = arguments[1];
							Player host = Bukkit.getPlayer(hostPlayerName);
							Lobby lobby = getLobbyPlayerIsIn(host);
							if(lobby != null) {
								lobby.addPlayer(player);
							}
							
						}

					} else {
					player.sendMessage("§c[Arena]§f Falsche Eingabe -> /help Arena");
					}
					break;
				case 3:
					if (arguments[0].equalsIgnoreCase("location")) {
						// if(host.hasPermission("xyz"));
						if (arguments[1].matches("^[a,b][1-4]$")) { // REGEX
							player.sendMessage("§c[Arena]§f location " + arguments[1] + " was set");
							String locationNumber = arguments[1];
							String arenaName = arguments[2];
							Location location = player.getLocation();
							ArenaLocation arenaLocation = new ArenaLocation(locationNumber, location,arenaName);
							if(arenaName !=null) {
								LocationManager locationManager = new LocationManager();
								locationManager.writeToYAML(arenaLocation);
							}else {
								player.sendMessage("§c[Arena]§f Du musst eine Welt angeben");
							}
						} else {
							player.sendMessage("§c[Arena]§f Wrong Regex");
						}
						break;
					}
				
				default:
					player.sendMessage("§c[Arena]§f Falsche Eingabe -> /help Arena");
				}
			}
		}
		return false;
	}
	
	private void createLobby(Player host, org.bukkit.inventory.ItemStack hostPlayerHeadItem) {
		Inventory inventory = createInventory(hostPlayerHeadItem,host.getName());
		List<Player> teamA = new ArrayList<>();
		List<Player> teamB = new ArrayList<>();
		teamA.add(host);
		Lobby lobby = new Lobby(inventory,teamA,teamB);
		lobbyList.add(lobby);
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(lobby, pm.getPlugin("Arena"));
		lobby.openInventory(host);
	}
	
	private Boolean getIfPlayerIsInGame(Player p) {
		Boolean b = false;
		for(Lobby lobby:lobbyList) {
			if(lobby.getTeamA().contains(p)|| lobby.getTeamB().contains(p)) {
				b = true;
			}
		}	
		return b;
	}
	
	private Lobby getLobbyPlayerIsIn(Player p) {
		Lobby l = null;
		for(Lobby lobby:lobbyList) {
			if(lobby.getTeamA().contains(p)|| lobby.getTeamB().contains(p)) {
				l = lobby;
			}
		}
		return l;
	}
	
	
	
	private Inventory createInventory(org.bukkit.inventory.ItemStack playerHead,String playerName) {
		List <org.bukkit.inventory.ItemStack> itemStackList = new ArrayList<>();
		
		
		//hosts head
	  
		
		//
		
		//add player button
		org.bukkit.inventory.ItemStack button = new org.bukkit.inventory.ItemStack(org.bukkit.Material.OAK_BUTTON);
		ItemMeta itemMetaButton = button.getItemMeta();
		itemMetaButton.setDisplayName("§6Spieler hinzufügen");
		button.setItemMeta(itemMetaButton);
        //
		
		
		//grey dye
		org.bukkit.inventory.ItemStack greyDye = new org.bukkit.inventory.ItemStack(org.bukkit.Material.GRAY_DYE);
		ItemMeta itemMetaGreyDye = greyDye.getItemMeta();
		itemMetaGreyDye.setDisplayName(" ");
		greyDye.setItemMeta(itemMetaGreyDye);
		//
		
		//black stained glass pane
		org.bukkit.inventory.ItemStack blackGlass = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta itemMetaBlackGlass = blackGlass.getItemMeta();
		itemMetaBlackGlass.setDisplayName(" ");
		blackGlass.setItemMeta(itemMetaBlackGlass);
		//
		
		//lime concrete powder
		org.bukkit.inventory.ItemStack limeConcretePowder = new org.bukkit.inventory.ItemStack(org.bukkit.Material.LIME_CONCRETE_POWDER);
		ItemMeta itemMetaLimeConcretePowder = limeConcretePowder.getItemMeta();
		itemMetaLimeConcretePowder.setDisplayName("§6Spiel starten!");
		limeConcretePowder.setItemMeta(itemMetaLimeConcretePowder);
		//
		
		//red concrete powder
		org.bukkit.inventory.ItemStack redConcretePowder = new org.bukkit.inventory.ItemStack(org.bukkit.Material.RED_CONCRETE_POWDER);
		ItemMeta itemMetaRedConcretePowder = redConcretePowder.getItemMeta();
		itemMetaRedConcretePowder.setDisplayName("§6Lobby verlassen");
		redConcretePowder.setItemMeta(itemMetaRedConcretePowder);
		//
		
		//blue stained glass pane
		org.bukkit.inventory.ItemStack blueStainedGlassPane = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLUE_STAINED_GLASS_PANE);
		ItemMeta itemMetaBlueStainedGlassPane = blueStainedGlassPane.getItemMeta();
		itemMetaBlueStainedGlassPane.setDisplayName("§9Team Blau");
		blueStainedGlassPane.setItemMeta(itemMetaBlueStainedGlassPane);
		//
		
		//red stained glass pane
		org.bukkit.inventory.ItemStack redStainedGlassPane = new org.bukkit.inventory.ItemStack(org.bukkit.Material.RED_STAINED_GLASS_PANE);
		ItemMeta itemMetaRedStainedGlassPane = redStainedGlassPane.getItemMeta();
		itemMetaRedStainedGlassPane.setDisplayName("§cTeam Rot");
		redStainedGlassPane.setItemMeta(itemMetaRedStainedGlassPane);
		//
		
		//gold ingot
		org.bukkit.inventory.ItemStack goldIngot = new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLD_INGOT);
		ItemMeta itemMetaGoldIngot = goldIngot.getItemMeta();
		itemMetaGoldIngot.setDisplayName("§6Preisgeld festlegen");
		goldIngot.setItemMeta(itemMetaGoldIngot);
		
		
		itemStackList.add(0, blueStainedGlassPane);
		itemStackList.add(1, playerHead);
		itemStackList.add(2,greyDye);
		itemStackList.add(3,greyDye);
		itemStackList.add(4,greyDye);
		
		itemStackList.add(5,button);
		itemStackList.add(6,blackGlass);
		itemStackList.add(7,blackGlass);
		itemStackList.add(8,blackGlass);
		itemStackList.add(9,redStainedGlassPane);
			
		itemStackList.add(10,greyDye);
		itemStackList.add(11,greyDye);
		itemStackList.add(12,greyDye);
		itemStackList.add(13,greyDye);
		
		itemStackList.add(14,button);
		
		itemStackList.add(15,blackGlass);
		itemStackList.add(16,blackGlass);

		itemStackList.add(17,limeConcretePowder);
		itemStackList.add(18,redConcretePowder);
		
		itemStackList.add(19,blackGlass);
		itemStackList.add(20,blackGlass);
		itemStackList.add(21,blackGlass);
		itemStackList.add(22,blackGlass);
		
		itemStackList.add(23,goldIngot);
		
		itemStackList.add(24,blackGlass);
		itemStackList.add(25,blackGlass);
		itemStackList.add(26,blackGlass);
		
		
		Inventory inventory = Bukkit.createInventory(null, 3*9,"§3Arena Lobby | host: "+playerName);
		
		for(int i=0;i<itemStackList.size();++i) {
			inventory.setItem(i, itemStackList.get(i));
		}
		return inventory;
	}
}
