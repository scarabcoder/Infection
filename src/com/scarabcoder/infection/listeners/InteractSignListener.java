package com.scarabcoder.infection.listeners;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.scarabcoder.infection.Main;
import com.scarabcoder.infection.game.GameManager;

public class InteractSignListener implements Listener{
	@EventHandler
	public void playerRightclick(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(e.getClickedBlock().getType().equals(Material.WALL_SIGN)){
				Sign sign = (Sign) e.getClickedBlock().getState();
				@SuppressWarnings("unchecked")
				List<Location> locs = new CopyOnWriteArrayList<Location>((Collection<? extends Location>) Main.getPlugin().getConfig().getList("signs"));
				for(Location loc : locs){
					if(loc.equals(sign.getLocation())){
						GameManager.getGame(sign.getLine(1)).addPlayer(e.getPlayer());
					}
				}
			}
		}
}
}
