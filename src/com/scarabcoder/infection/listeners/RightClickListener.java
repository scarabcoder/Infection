package com.scarabcoder.infection.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.scarabcoder.infection.game.GameManager;

public class RightClickListener implements Listener{
	@EventHandler
	public void playerRightClick(PlayerInteractEvent e){
		if(GameManager.isPlayerIngame(e.getPlayer())){
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
				Arrow arrow = e.getPlayer().getWorld().spawnArrow(e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(),3, 12);
				arrow.setShooter(e.getPlayer());
				e.setCancelled(true);
				
			}
		}
	}
}
