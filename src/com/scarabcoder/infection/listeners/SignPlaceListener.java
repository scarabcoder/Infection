package com.scarabcoder.infection.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.scarabcoder.infection.Main;
import com.scarabcoder.infection.game.Game;
import com.scarabcoder.infection.game.GameManager;

public class SignPlaceListener implements Listener {
	@SuppressWarnings("unchecked")
	@EventHandler
	public void signPlace(SignChangeEvent e){
		if(e.getLine(0).equals("[infect]")){
			for(Game game : GameManager.getGames()){
				if(game.getID().equalsIgnoreCase(e.getLine(1))){
					if(e.getPlayer().hasPermission("infection.placesign")){
						e.setLine(0, ChatColor.RESET + "[" + ChatColor.RED + "Infection" + ChatColor.RESET + "]");
						List<Location> locs = (List<Location>) Main.getPlugin().getConfig().getList("signs");
						
						locs.add(e.getBlock().getLocation());
						Main.getPlugin().getConfig().set("signs", locs);
						Main.getPlugin().saveConfig();
					}
				}
			}
		}else if(e.getLine(0).equals("[iboard]")){
			
			if(e.getPlayer().hasPermission("infection.placesign")){
				e.setLine(0, ChatColor.RESET + "[" + ChatColor.GREEN + "Leaderboard" + ChatColor.RESET + "]");
				List<Location> locs = (List<Location>) Main.getPlugin().getConfig().getList("leadboards");
				locs.add(e.getBlock().getLocation());
				Main.getPlugin().getConfig().set("leadboards", locs);
				Main.getPlugin().saveConfig();
				e.setLine(3, e.getLine(1));
				e.setLine(1, "");
			}
		}
}
}
