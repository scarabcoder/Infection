package com.scarabcoder.infection.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.scarabcoder.infection.game.GameManager;

public class DropItemListener implements Listener{
	@EventHandler
	public void itemDrop(PlayerDropItemEvent e){
		if(GameManager.isPlayerIngame(e.getPlayer())){
			e.setCancelled(true);
		}
	}
}
