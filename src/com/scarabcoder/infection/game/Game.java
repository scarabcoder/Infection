package com.scarabcoder.infection.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.connorlinfoot.titleapi.TitleAPI;
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
	
	private HashMap<String, Integer> heat = new HashMap<String, Integer>();
	
	private int countdown;
	
	private HashMap<String, Integer> points = new HashMap<String, Integer>();
	
	private HashMap<String, Material> arrowGun = new HashMap<String, Material>();
	
	private HashMap<String, Integer> invincible = new HashMap<String, Integer>();
	
	private String id;
	
	private HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
	
	private int counter;
	
	private List<String> players = new ArrayList<String>();
	
	private HashMap<String, PlayerType> pTypes = new HashMap<String, PlayerType>();

	private int flare = 0;
	
	private int time = 0;
	
	private int speed = 1;
	
	private boolean baby = false;
	
	private Location infectedSpawn;
	
	private HashMap<String, Integer> sCooldown = new HashMap<String, Integer>();
	
	private HashMap<String, Integer> hCooldown = new HashMap<String, Integer>();
	
	private HashMap<String, Integer> mCooldown = new HashMap<String, Integer>();
	
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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				for(Player p : getPlayers()){
						if(p.isSneaking()){
							if(p.getItemInHand().getType().equals(Material.WOOD_HOE)){
								if(!isZoomed(p)){
									zoomIn(p);
								}
						}
					}else{
						if(isZoomed(p)){
							zoomOut(p);
						}
					}
				}
				for(String str : sCooldown.keySet()){
					if(sCooldown.get(str).equals(0)){
						sCooldown.remove(str);
					}else{
						sCooldown.put(str, sCooldown.get(str) - 1);
					}
				}
				for(String str : hCooldown.keySet()){
					if(hCooldown.get(str).equals(0)){
						hCooldown.remove(str);
					}else{
						hCooldown.put(str, hCooldown.get(str) - 1);
					}
				}
				for(String str : mCooldown.keySet()){
					if(mCooldown.get(str).equals(0)){
						mCooldown.remove(str);
					}else{
						mCooldown.put(str, mCooldown.get(str) - 1);
					}
}
				
			}
			
		}, 0L, 1L);
	}
	
	public void registerArrow(Arrow arrow, Material gun){
		this.arrowGun.put(arrow.getUniqueId().toString(), gun);
	}
	
	public void setShotgunCooldown(Player p, int cooldown){
		this.sCooldown.put(p.getUniqueId().toString(), cooldown);
	}
	
	
	public boolean canFireShotgun(Player p){
		if(this.sCooldown.containsKey(p.getUniqueId().toString())){
			return false;
		}else{
			return true;
		}
	}
	public void setHandgunCooldown(Player p, int cooldown){
		this.hCooldown.put(p.getUniqueId().toString(), cooldown);
	}
	
	
	public boolean canFireHandgun(Player p){
		if(this.hCooldown.containsKey(p.getUniqueId().toString())){
			return false;
		}else{
			return true;
		}
	}
	public void setMachineCooldown(Player p, int cooldown){
		this.mCooldown.put(p.getUniqueId().toString(), cooldown);
	}
	
	
	public boolean canFireMachine(Player p){
		if(this.mCooldown.containsKey(p.getUniqueId().toString())){
			return false;
		}else{
			return true;
		}
	}
	
	public int getMaxPlayers(){
		return this.maxPlayers;
	}
	
	public void zoomIn(Player p){
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 15));
	}
	
	public void zoomOut(Player p){
		p.removePotionEffect(PotionEffectType.SLOW);
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
						this.startGame();
						
					}else{
						this.sendMessage(ChatColor.RED + "Not enough players, cancelling countdown.");
						this.counter = countdown + 1;
					}
				}
				
			}
		}else{
			for(String str : this.invincible.keySet()){
				if(invincible.get(str) != 0){
					invincible.put(str, invincible.get(str) - 1);
				}
			}
			time += 1;
			if(time == 8 * 60){
				this.baby = true;
				for(Player p : this.getPlayers()){
					if(this.isInfected(p)){
						ZombieDisguise disguise = new ZombieDisguise();
						disguise.setCustomName(p.getName());
						disguise.setAdult(!this.baby);
						this.speed = 1;
						p.removePotionEffect(PotionEffectType.SPEED);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
						Main.api.disguise(p, disguise);
					}
				}
				this.sendMessage(ChatColor.RED + "Zombies have gotten smaller and faster!");
			}
			int infected = 0;
			for(Player p : this.getPlayers()){
				if(this.isInfected(p)){
					infected += 1;
				}
				if(this.heat.get(p.getUniqueId().toString()) < 101){
					this.heat.put(p.getUniqueId().toString(), 0);
				}else{
					this.heat.put(p.getUniqueId().toString(), this.heat.get(p.getUniqueId().toString()) - 100);
				}
			}
		
			if(infected == this.players.size()){
				Player winner = this.getPlayers().get(0);
				for(Player p : this.getPlayers()){
					if(this.points.get(winner.getUniqueId().toString()) < this.points.get(p.getUniqueId().toString())){
						winner = p;
					}
				}
				
				Bukkit.broadcastMessage(ChatColor.RESET + "[" + ChatColor.RED + "Infection" + ChatColor.RESET + "] " + ChatColor.GREEN + ChatColor.BOLD.toString() + winner.getName() + " won the game on " + this.getID() + " with " + this.points.get(winner.getUniqueId().toString()) + " kills!");
				List<String> players = (List<String>) Main.getPlugin().getConfig().getList("players");
				
				for(Player p : this.getPlayers()){
					if(!players.contains(p.getUniqueId().toString())){
						players.add(p.getUniqueId().toString());
					}
					Main.getPlugin().getConfig().set("players", players);
					if(Main.getPlugin().getConfig().contains("player." + p.getUniqueId().toString())){
						Main.getPlugin().getConfig().set("player." + p.getUniqueId().toString(), Main.getPlugin().getConfig().getInt("player." + p.getUniqueId().toString()) + this.points.get(p.getUniqueId().toString()));
					}else{
						Main.getPlugin().getConfig().set("player." + p.getUniqueId().toString(), this.points.get(p.getUniqueId().toString()));
					}
				}
				Main.getPlugin().saveConfig();
				this.endGame();
			}
			if(infected == 0){
				this.infect(this.getPlayers().get(new Random().nextInt(this.players.size())));
			}
			if(this.flare == 60){
				this.flare = 0;
				for(Player p : this.getPlayers()){
					if(!this.isInfected(p)){
						p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*10, 1));
					}
				}
			}else{
				flare += 1;
			}
		}
	}
	
	public void startGame(){
		for(Player p : this.getPlayers()){
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1,1);
			p.setTotalExperience(0);
		}
		this.setGameStatus(GameStatus.INGAME);
		List<Player> infected = new ArrayList<Player>(this.getPlayers());
		int random = new Random().nextInt(infected.size());
		this.infect(infected.get(random));
		infected.remove(random);
		random = new Random().nextInt(infected.size());
		this.infect(infected.get(random));
		infected.remove(random);
		
		for(Player p : this.getPlayers()){
			if(!this.isInfected(p)){
				p.teleport(this.humanSpawn);
				p.sendMessage(ChatColor.RED + "OH NO! THE ZOMBIES ARE COMING!");
				ItemStack gun = new ItemStack(Material.IRON_HOE);
				ItemMeta meta = gun.getItemMeta();
				meta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.GREEN + "Shotgun");
				gun.setItemMeta(meta);
				ItemStack gun1 = new ItemStack(Material.WOOD_HOE);
				meta = gun.getItemMeta();
				meta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.GREEN + "Rifle");
				gun1.setItemMeta(meta);
				ItemStack gun2 = new ItemStack(Material.STONE_HOE);
				meta = gun.getItemMeta();
				meta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.GREEN + "Machine gun");
				gun2.setItemMeta(meta);
				p.getInventory().addItem(gun);
				p.getInventory().addItem(gun1);
				p.getInventory().addItem(gun2);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1));
				p.updateInventory();
				TitleAPI.sendTitle(p, 10, 75, 10, ChatColor.GREEN + "Stay alive!", "Shoot the zombies before they infect you.");
			}else{
				TitleAPI.sendTitle(p, 10, 75, 10, ChatColor.RED + "Braaaains...", "Infect the humans.");
				p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 0.5f);
			}
		}
	}
	
	public void endGame(){
		this.sendMessage("Game ending!");
		for(Player p : this.getPlayers()){
			this.removePlayer(p);
			this.zoomOut(p);
		}
		this.flare = 0;
		this.baby = false;
		time = 0;
		this.counter = countdown + 1;
		this.setGameStatus(GameStatus.WAITING);
	}
	
	public boolean isZoomed(Player p){
		return p.hasPotionEffect(PotionEffectType.SLOW);
	}
	
	public void addHeat(Player p, int amount){
		this.heat.put(p.getUniqueId().toString(), this.heat.get(p.getUniqueId().toString()) + amount);
		
	}
	
	public int getHeat(Player p){
		return this.heat.get(p.getUniqueId().toString());
	}
	
	@SuppressWarnings("deprecation")
	public void arrowHitPlayerEvent(Player hit, Arrow arrow, double damage){
		if(arrow.getShooter() instanceof CraftPlayer){
			Player p = (Player) arrow.getShooter();
			if(this.isInfected(hit)){
				if(this.invincible.get(hit.getUniqueId().toString()) == 0){
					int doDamage = 0;
					if(this.arrowGun.get(arrow.getUniqueId().toString()).equals(Material.IRON_HOE)){
						doDamage = 20;
					}else if(this.arrowGun.get(arrow.getUniqueId().toString()).equals(Material.WOOD_HOE)){
						double y = hit.getEyeLocation().getY();
						y = y - 0.2;
						System.out.println(arrow.getLocation().getY());
						System.out.println(y);
						if(arrow.getLocation().getY() >= y){
							doDamage = 20;
							p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "BOOM! HEADSHOT!");
						}else{
							doDamage = 15;
						}
					}else{
						doDamage = 5;
					}
					
					if(hit.getHealth() - doDamage <= 0){
						p.sendMessage(ChatColor.GREEN + "You shot " + hit.getName() + "!");
						this.points.put(p.getUniqueId().toString(), this.points.get(p.getUniqueId().toString()) + 1);
						hit.getWorld().spigot().playEffect(hit.getEyeLocation(), Effect.TILE_BREAK, Material.REDSTONE_BLOCK.getId(), 0, 0, 0, 0, 1, 50, 50);
						hit.teleport(this.infectedSpawn);
						this.invincible.put(hit.getUniqueId().toString(), 4);
						hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 0));
						for(Player p1 : this.getPlayers()){
							p1.playSound(hit.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 0.8f);
						}
						hit.setHealth(20);
					}else{
						hit.damage(doDamage);
					}
					this.arrowGun.remove(arrow.getUniqueId().toString());
					arrow.remove();
				}
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
			if(this.players.size() != this.getMaxPlayers()){
				p.setAllowFlight(false);
				p.setGameMode(GameMode.ADVENTURE);
				this.players.add(p.getUniqueId().toString());
				this.pTypes.put(p.getUniqueId().toString(), PlayerType.HUMAN);
				this.playerData.put(p.getUniqueId().toString(), new PlayerData(p.getUniqueId().toString(), p.getLocation(), p.getInventory().getContents(), p.getHealth(), p.getFoodLevel(), p.getTotalExperience()));
				p.teleport(this.preSpawn);
				p.getInventory().clear();
				p.setHealth(20);
				p.setFoodLevel(20);
				p.setTotalExperience(0);
				invincible.put(p.getUniqueId().toString(), 0);
				this.sendMessage(ChatColor.GREEN + p.getName() + " joined the game!");
				this.points.put(p.getUniqueId().toString(), 0);
				this.heat.put(p.getUniqueId().toString(), 0);
			}else{
				p.sendMessage(ChatColor.RED + "Game full!");
			}
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
					this.points.put(attacker.getUniqueId().toString(), this.points.get(attacker.getUniqueId().toString()) + 1);
					this.infect(attacked);
					attacker.playSound(attacker.getLocation(), Sound.ENTITY_ZOMBIE_STEP, 1, 0.2f);
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
		disguise.setAdult(!this.baby);
		Main.api.disguise(p, disguise);
		TitleAPI.sendTitle(p, 10, 120, 10, ChatColor.RED + "Braaaains...", "Infect the humans.");
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, this.speed));
		this.invincible.put(p.getUniqueId().toString(), 4);
	}
	
	private PlayerType getPlayerType(Player p){
		return this.pTypes.get(p.getUniqueId().toString());
	}
	
	public void removePlayer(Player p){
		Main.api.undisguise(p);
		this.players.remove(p.getUniqueId().toString());
		this.pTypes.remove(p.getUniqueId().toString());
		this.playerData.get(p.getUniqueId().toString()).applyToPlayer();
		p.removePotionEffect(PotionEffectType.SPEED);
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
