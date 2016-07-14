package com.scarabcoder.infection.game;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {
	
	private Location loc;
	
	private ItemStack[] inv;
	
	private double health;
	
	private int hunger;
	
	private int exp;
	
	private String player;
	
	public PlayerData(String player, Location loc, ItemStack[] inv, double health, int hunger, int exp){
		this.player = player;
		this.loc = loc;
		this.inv = inv;
		this.health = health;
		this.hunger = hunger;
		this.exp = exp;
	}
	
	public void applyToPlayer(){
		Player p = Bukkit.getPlayer(UUID.fromString(this.player));
		p.teleport(loc);
		p.getInventory().setContents(inv);
		p.setHealth(health);
		p.setFoodLevel(hunger);
		p.setTotalExperience(exp);
		
	}
}
