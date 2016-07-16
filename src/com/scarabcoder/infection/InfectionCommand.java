package com.scarabcoder.infection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.scarabcoder.infection.game.Game;
import com.scarabcoder.infection.game.GameManager;

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
				}else if(args[0].equalsIgnoreCase("start")){
					if(GameManager.isPlayerIngame(p)){
						Game game = GameManager.getGamePlayerIsIn(p);
						if(p.hasPermission("infection.start")){
							game.sendMessage(ChatColor.GREEN + p.getName() + " started the game!");
							game.startGame();
						}
					}
				}
			}
			
		}else{
			sender.sendMessage(ChatColor.RED + "Player-only command!");
		}
		return true;
	}

}
