package me.TheBukor.SkStuff.util;

import org.bukkit.Bukkit;

public class ReflectionUtils {

	public static Class<?> getNMSClass(String classString) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
		String name = "net.minecraft.server." + version + classString;
		Class<?> nmsClass = null;
		try {
			nmsClass = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().warning("Unable to get NMS class! You are probably running an unsupported version");
			return null;
		}
		return nmsClass;
	}

	public static Class<?> getOBCClass(String classString) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
		String name = "org.bukkit.craftbukkit." + version + classString;
		Class<?> obcClass = null;
		try {
			obcClass = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().warning("Unable to get OBC class! You are probably running an unsupported version");
			return null;
		}
		return obcClass;
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
	}
}