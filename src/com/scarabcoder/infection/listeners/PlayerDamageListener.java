package com.scarabcoder.infection.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.scarabcoder.infection.game.GameManager;

public class PlayerDamageListener implements Listener {
	@EventHandler
	public void playerDamagePlayer(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			if(e.getDamager() instanceof Player){
				Player attacked = (Player) e.getEntity();
				Player attacker = (Player) e.getDamager();
				if(GameManager.isPlayerIngame(attacked)){
					GameManager.getGamePlayerIsIn(attacked).playerDamagePlayer(attacker, attacked);
					e.setCancelled(true);
				}
			}else if(e.getDamager() instanceof Arrow){
				Arrow arrow = (Arrow) e.getDamager();
				Player attacked = (Player) e.getEntity();
				if(GameManager.isPlayerIngame(attacked)){
					GameManager.getGamePlayerIsIn(attacked).arrowHitPlayerEvent(attacked, arrow);
					e.setCancelled(true);
				}
			}
		}
	}
	
	
	@EventHandler
	public void playerDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(GameManager.isPlayerIngame(p)){
				if(e.getCause().equals(DamageCause.SUFFOCATION)  || e.getCause().equals(DamageCause.FALL)){
					e.setCancelled(true);
				}
			}
		}
	}
	
	
	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(GameManager.isPlayerIngame(p)){
				e.setCancelled(true);
			}
		}
	}
}
