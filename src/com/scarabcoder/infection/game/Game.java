package com.scarabcoder.infection.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.scarabcoder.infection.Main;
import com.scarabcoder.infection.enums.GameStatus;
import com.scarabcoder.infection.enums.PlayerType;

import de.robingrether.idisguise.disguise.ZombieDisguise;
import net.md_5.bungee.api.ChatColor;

public class Game {
	
	private GameStatus status;
	
	private int minPlayers;
	
	private Location preSpawn;
	
	private int maxPlayers;
	
	private int countdown;
	
	private String id;
	
	private HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
	
	private int counter;
	
	private List<String> players = new ArrayList<String>();
	
	private HashMap<String, PlayerType> pTypes = new HashMap<String, PlayerType>();

	private Location infectedSpawn;

	private Location humanSpawn;
	
	public Game(String id, int minPlayers, int maxPlayers, int countdown, Location preSpawn, Location infectedSpawn, Location humanSpawn){
		this.countdown = countdown;
		this.counter = countdown + 1;
		this.id = id;
		this.minPlayers = minPlayers;
		this.status = GameStatus.WAITING;
		this.maxPlayers = maxPlayers;
		this.preSpawn = preSpawn;
		this.infectedSpawn = infectedSpawn;
		this.humanSpawn = humanSpawn;
		GameManager.registerGame(this);
		
	}
	
	public void doSecond(){
		if(this.getStatus().equals(GameStatus.WAITING)){
			if(counter == countdown + 1){
				if(this.getPlayerUUIDs().size() >= this.minPlayers){
					this.sendMessage(ChatColor.GREEN + "Game starting in " + ChatColor.BOLD +  countdown + ChatColor.RESET + ChatColor.GREEN.toString() + " seconds!");
					if(this.getPlayerUUIDs().size() != this.maxPlayers){
						Bukkit.broadcastMessage(ChatColor.RESET + "[" + ChatColor.RED + "Infection" + ChatColor.RESET + "] " + ChatColor.GREEN + "Infection \"" + this.getID() + "\" starting in " + countdown + " seconds! (" + this.getPlayerUUIDs().size() + "/" + this.maxPlayers + ")");
						counter = countdown;
					}
				}
			}else{
				System.out.println(counter);
				counter -= 1;
				for(Player p : this.getPlayers()){
					p.setLevel(counter);
				}
				if(counter != 0){
					if(counter % 10 == 0){
						this.sendMessage(ChatColor.GREEN + "Game starting in " + ChatColor.BOLD + counter + ChatColor.RESET + ChatColor.GREEN.toString() + " seconds!");
						for(Player p : this.getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1,1);
						}
					}
					if(counter == 1){
						this.sendMessage(ChatColor.GREEN + "Game starting in " + ChatColor.BOLD + counter + ChatColor.RESET + ChatColor.GREEN.toString() + " second!");
						for(Player p : this.getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1,1);
						}
					}else if(counter < 6){
						this.sendMessage(ChatColor.GREEN + "Game starting in " + ChatColor.BOLD + counter + ChatColor.RESET + ChatColor.GREEN.toString() + " seconds!");
						for(Player p : this.getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1,1);
						}
					}
				}else{
					if(this.getPlayerUUIDs().size() >= this.minPlayers){
						for(Player p : this.getPlayers()){
							p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1,1);
						}
						this.setGameStatus(GameStatus.INGAME);
						long seed = System.nanoTime();
						Collections.shuffle(this.players, new Random(seed));
						this.infect(this.getPlayers().get(0));
						this.infect(this.getPlayers().get(1));
						for(Player p : this.getPlayers()){
							if(!this.isInfected(p)){
								p.teleport(this.humanSpawn);
								p.sendMessage(ChatColor.RED + "OH NO! THE ZOMBIES ARE COMING!");
								ItemStack gun = new ItemStack(Material.IRON_HOE);
								ItemMeta meta = gun.getItemMeta();
								meta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.GREEN + "Shotgun");
								gun.setItemMeta(meta);
								p.getInventory().addItem(gun);
								p.updateInventory();
							}else{
								p.sendMessage(ChatColor.RED + "Bleghhhh....");
							}
						}
						
					}else{
						this.sendMessage(ChatColor.RED + "Not enough players, cancelling countdown.");
						this.counter = countdown + 1;
					}
				}
				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void arrowHitPlayerEvent(Player hit, Arrow arrow){
		if(arrow.getShooter() instanceof Player){
			Player p = (Player) arrow.getShooter();
			if(this.isInfected(hit)){
				hit.getWorld().playEffect(hit.getLocation(), Effect.TILE_BREAK, Material.REDSTONE_BLOCK.getId());
				hit.teleport(this.infectedSpawn);
				
			}
		}
	}
	
	private boolean isInfected(Player p){
		return this.pTypes.get(p.getUniqueId().toString()).equals(PlayerType.INFECTED);
	}
	
	public void setGameStatus(GameStatus status){
		this.status = status;
	}
	
	public void addPlayer(Player p){
		if(this.getStatus().equals(GameStatus.WAITING)){
			this.players.add(p.getUniqueId().toString());
			this.pTypes.put(p.getUniqueId().toString(), PlayerType.HUMAN);
			this.playerData.put(p.getUniqueId().toString(), new PlayerData(p.getUniqueId().toString(), p.getLocation(), p.getInventory().getContents(), p.getHealth(), p.getFoodLevel(), p.getTotalExperience()));
			p.teleport(this.preSpawn);
			p.getInventory().clear();
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setTotalExperience(0);
			this.sendMessage(ChatColor.GREEN + p.getName() + " joined the game!");
			
			
		}else{
			p.sendMessage(ChatColor.RED + "Game already in progress!");
		}
	}
	
	public void playerDamagePlayer(Player attacker, Player attacked) {
		if(this.getStatus().equals(GameStatus.INGAME)){
			if(this.getPlayerType(attacker).equals(PlayerType.INFECTED)){
				if(this.getPlayerType(attacked).equals(PlayerType.HUMAN)){
					attacked.getWorld().playEffect(attacked.getLocation(), Effect.TILE_BREAK, Material.REDSTONE_BLOCK.getId());
					this.sendMessage(ChatColor.RED + attacked.getName() + " was infected by " + attacker.getName() + "!");
					this.infect(attacked);
					attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1, 1);
				}
			}
		}
	}
	
	private void infect(Player p){
		p.getInventory().clear();
		this.pTypes.put(p.getUniqueId().toString(), PlayerType.INFECTED);
		p.sendMessage(ChatColor.RED + "YOU ARE " + ChatColor.DARK_RED + ChatColor.UNDERLINE + "INFECTED");
		p.teleport(this.infectedSpawn);
		ZombieDisguise disguise = new ZombieDisguise();
		disguise.setCustomName(p.getName());
		Main.api.disguise(p, disguise);
	}
	
	private PlayerType getPlayerType(Player p){
		return this.pTypes.get(p.getUniqueId().toString());
	}
	
	public void removePlayer(Player p){
		Main.api.undisguise(p);
		this.players.remove(p.getUniqueId().toString());
		this.pTypes.remove(p.getUniqueId().toString());
		this.playerData.get(p.getUniqueId().toString()).applyToPlayer();
	}
	
	public void sendMessage(String msg){
		for(Player p : this.getPlayers()){
			p.sendMessage(ChatColor.RESET + "[" + ChatColor.RED + "Infection" + ChatColor.RESET + "] " + msg);
		}
	}
	
	public String getID(){
		return this.id;
	}
	
	public GameStatus getStatus(){
		return this.status;
	}
	
	public List<Player> getPlayers(){
		List<Player> players = new ArrayList<Player>();
		for(String str : this.players){
			players.add(Bukkit.getPlayer(UUID.fromString(str)));
		}
		return players;
	}
	
	public List<String> getPlayerUUIDs(){
		return this.players;
	}

	
	
}
