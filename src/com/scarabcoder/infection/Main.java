package com.scarabcoder.infection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.scarabcoder.infection.enums.GameStatus;
import com.scarabcoder.infection.game.Game;
import com.scarabcoder.infection.game.GameManager;
import com.scarabcoder.infection.listeners.CommandListener;
import com.scarabcoder.infection.listeners.DropItemListener;
import com.scarabcoder.infection.listeners.InteractSignListener;
import com.scarabcoder.infection.listeners.PlayerDamageListener;
import com.scarabcoder.infection.listeners.RightClickListener;
import com.scarabcoder.infection.listeners.SignPlaceListener;

import de.robingrether.idisguise.api.DisguiseAPI;

public class Main extends JavaPlugin{
	
	public static DisguiseAPI api;
	
	private static Plugin plugin;
	
	public void loadConfiguration(){
    	List<Location> locs = new ArrayList<Location>();
    	this.getConfig().addDefault("signs", locs);
    	this.getConfig().addDefault("countdownTime", 60);
        //See "Creating you're defaults"
        plugin.getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        plugin.saveConfig();
	}
	
	
	
	@Override
	public void onEnable(){
		plugin = this;
		api = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
		this.loadConfiguration();
		this.getCommand("infection").setExecutor(new InfectionCommand());
		this.getLogger().log(Level.FINE, "Started Infection by ScarabCoder");
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
		this.getServer().getPluginManager().registerEvents(new DropItemListener(), this);
		this.getServer().getPluginManager().registerEvents(new RightClickListener(), this);
		this.getServer().getPluginManager().registerEvents(new InteractSignListener(), this);
		this.getServer().getPluginManager().registerEvents(new SignPlaceListener(), this);
		FileConfiguration config = this.getConfig();
		
		Game game = new Game("infected1", 3, 30, config.getInt("countdownTime"), new Location(Bukkit.getWorld("infected1"), 11.5, 63, -38), new Location(Bukkit.getWorld("infected1"), -19, 62, -29), new Location(Bukkit.getWorld("infected1"), -2, 62, 32));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				for(Game game : GameManager.getGames()){
					game.doSecond();
				}
				@SuppressWarnings("unchecked")
				List<Location> locs = new CopyOnWriteArrayList<Location>((Collection<? extends Location>) Main.getPlugin().getConfig().getList("signs"));
				for(Location loc : locs){
					if(loc.getBlock().getType().equals(Material.WALL_SIGN)){
						Sign sign = (Sign) loc.getBlock().getState();
						if(GameManager.getGame(sign.getLine(1)) != null){
							Game game = GameManager.getGame(sign.getLine(1));
							String str;
							if(game.getStatus().equals(GameStatus.WAITING)){
								str = ChatColor.GREEN + "Open";
							}else{
								str = ChatColor.RED + "Ingame";
							}
							sign.setLine(2, ChatColor.AQUA + game.getStatus().toString());
							sign.setLine(3, game.getPlayerUUIDs().size() + "/" + game.getMaxPlayers());
							sign.update();
						}else{
							locs.remove(loc);
							loc.getBlock().breakNaturally();
						}
					}else{
						locs.remove(loc);
						loc.getBlock().breakNaturally();
					}
				}
				Main.getPlugin().getConfig().set("signs", locs);
Main.getPlugin().saveConfig();
			}
			
		}, 0L, 20L);
	}
	
	public static Plugin getPlugin(){
		return plugin;
	}
	
	
	
	@Override
	public void onDisable(){
		for(Game game : GameManager.getGames()){
			game.sendMessage("Server reloading, kicking players.");
			for(Player p : game.getPlayers()){
				game.removePlayer(p);
			}
		}
	}
}
