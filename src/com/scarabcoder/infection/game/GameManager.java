package com.scarabcoder.infection.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

public class GameManager {
	private static HashMap<String, Game> games = new HashMap<String, Game>();
	
	public static List<Game> getGames(){
		List<Game> gameList = new ArrayList<Game>();
		for(String str: games.keySet()){
			gameList.add(games.get(str));
		}
		return gameList;
	}
	
	public static void registerGame(Game game){
		games.put(game.getID(), game);
	}
	
	public static Game getGame(String id){
		return games.get(id);
	}
	
	public static boolean isPlayerIngame(Player p){
		for(Game game : getGames()){
			if(game.getPlayerUUIDs().contains(p.getUniqueId().toString())){
				return true;
			}
		}
		return false;
	}
	
	public static Game getGamePlayerIsIn(Player p){
		for(Game game : getGames()){
			if(game.getPlayerUUIDs().contains(p.getUniqueId().toString())){
				return game;
			}
		}
		return null;
	}
	
	
	
}
