package com.scarabcoder.infection;

import org.bukkit.Location;

public class ScarabUtil {
	public static boolean isLocationBetweenLocs(Location in, Location loc1, Location loc2){
		return in.toVector().isInAABB(loc1.toVector(), loc2.toVector());
	}
}
