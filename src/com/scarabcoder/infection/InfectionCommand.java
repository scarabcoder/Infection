package com.scarabcoder.infection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.scarabcoder.infection.game.GameManager;

import net.md_5.bungee.api.ChatColor;

public class InfectionCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(args.length > 0){
				if(args.length > 1){
					if(args[0].equalsIgnoreCase("join")){
						if(!GameManager.isPlayerIngame(p)){
							if(GameManager.getGame(args[1]) != null){
								GameManager.getGame(args[1]).addPlayer(p);
							}else{
								p.sendMessage(ChatColor.RED + "Game does not exist!");
							}
						}else{
							p.sendMessage(ChatColor.RED + "Already ingame!");
						}
					}
				}
				if(args[0].equalsIgnoreCase("leave")){
					if(GameManager.isPlayerIngame(p)){
						GameManager.getGamePlayerIsIn(p).removePlayer(p);
					}else{
						p.sendMessage(ChatColor.RED + "Not in a game!");
					}
				}
			}
			
		}else{
			sender.sendMessage(ChatColor.RED + "Player-only command!");
		}
		return true;
	}

}