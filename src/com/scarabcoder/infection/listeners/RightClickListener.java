package com.scarabcoder.infection.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.scarabcoder.infection.Main;
import com.scarabcoder.infection.game.Game;
import com.scarabcoder.infection.game.GameManager;

public class RightClickListener implements Listener{
	@EventHandler
	public void playerRightClick(PlayerInteractEvent e){
		if(GameManager.isPlayerIngame(e.getPlayer())){
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
				Game game = GameManager.getGamePlayerIsIn(e.getPlayer());
				if(e.getPlayer().getItemInHand().getType().equals(Material.IRON_HOE)){
					if(game.canFireShotgun(e.getPlayer())){
						Arrow arrow = e.getPlayer().getWorld().spawnArrow(e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(),3, 8);
						arrow.setGravity(false);
						arrow.setBounce(false);
						arrow.setShooter(e.getPlayer());
						game.setShotgunCooldown(e.getPlayer(), 30);
						game.registerArrow(arrow, Material.IRON_HOE);
						for(Player p : game.getPlayers()){
							p.playSound(e.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 4.5f);
						}
					}
				e.setCancelled(true);
				}else if(e.getPlayer().getItemInHand().getType().equals(Material.WOOD_HOE)){
					if(game.canFireHandgun(e.getPlayer())){
						Arrow arrow = e.getPlayer().getWorld().spawnArrow(e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(),3, 2);
						arrow.setGravity(false);
						arrow.setShooter(e.getPlayer());
						arrow.setBounce(false);
						game.setHandgunCooldown(e.getPlayer(), 8);
						game.registerArrow(arrow, Material.WOOD_HOE);
						for(Player p : game.getPlayers()){
							p.playSound(e.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 8f);
						}
					}
				}else if(e.getPlayer().getItemInHand().getType().equals(Material.STONE_HOE)){
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){

						@Override
						public void run() {
							Arrow arrow = e.getPlayer().getWorld().spawnArrow(e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(),8, 10);
							arrow.setGravity(false);
							arrow.setBounce(false);
							arrow.setShooter(e.getPlayer());
							game.registerArrow(arrow, Material.STONE_HOE);
						}
						
					}, 2L);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
						
						@Override
						public void run() {
							Arrow arrow = e.getPlayer().getWorld().spawnArrow(e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(),8, 10);
							arrow.setGravity(false);
							arrow.setShooter(e.getPlayer());
							arrow.setBounce(false);
							game.registerArrow(arrow, Material.STONE_HOE);
						}
						
					}, 4L);
					Arrow arrow = e.getPlayer().getWorld().spawnArrow(e.getPlayer().getEyeLocation(), e.getPlayer().getLocation().getDirection(),8, 10);
					arrow.setGravity(false);
					arrow.setBounce(false);
					arrow.setShooter(e.getPlayer());
					game.registerArrow(arrow, Material.STONE_HOE);
					game.addHeat(e.getPlayer(), 3);
					for(Player p : game.getPlayers()){
						p.playSound(e.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 8f);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void arrowLandEvent(ProjectileHitEvent e){
		if(e.getEntity() instanceof Arrow){
			for(Game game : GameManager.getGames()){
				if(e.getEntity().getWorld().getName().equals(game.getID())){
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){

						@Override
						public void run() {
							e.getEntity().remove();
						}
						
					}, 6 * 20);
				}
			}
		}
	}
}
