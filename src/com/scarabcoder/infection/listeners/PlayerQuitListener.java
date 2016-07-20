package com.scarabcoder.infection.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.scarabcoder.infection.game.GameManager;

public class PlayerQuitListener implements Listener {
	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent e){
		if(GameManager.isPlayerIngame(e.getPlayer())){
			GameManager.getGamePlayerIsIn(e.getPlayer()).removePlayer(e.getPlayer());
		}
	}
}
