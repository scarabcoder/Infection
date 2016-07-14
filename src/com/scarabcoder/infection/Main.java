package com.scarabcoder.infection;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.scarabcoder.infection.game.Game;
import com.scarabcoder.infection.game.GameManager;
import com.scarabcoder.infection.listeners.CommandListener;
import com.scarabcoder.infection.listeners.DropItemListener;
import com.scarabcoder.infection.listeners.PlayerDamageListener;
import com.scarabcoder.infection.listeners.RightClickListener;

import de.robingrether.idisguise.api.DisguiseAPI;

public class Main extends JavaPlugin{
	
	public static DisguiseAPI api;
	
	@Override
	public void onEnable(){
		api = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
		this.getCommand("infection").setExecutor(new InfectionCommand());
		this.getLogger().log(Level.FINE, "Started Infection by ScarabCoder");
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
		this.getServer().getPluginManager().registerEvents(new DropItemListener(), this);
		this.getServer().getPluginManager().registerEvents(new RightClickListener(), this);
		Game game = new Game("infected1", 3, 30, 10, new Location(Bukkit.getWorld("infected1"), 11.5, 63, -38), new Location(Bukkit.getWorld("infected1"), -19, 62, -29), new Location(Bukkit.getWorld("infected1"), -2, 62, 32));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				for(Game game : GameManager.getGames()){
					game.doSecond();
				}
			}
			
		}, 0L, 20L);
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
