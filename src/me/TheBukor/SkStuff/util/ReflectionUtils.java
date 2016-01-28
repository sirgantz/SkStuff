package me.TheBukor.SkStuff.util;

import org.bukkit.Bukkit;
import org.fusesource.jansi.Ansi;

public class ReflectionUtils {

	public static Class<?> getNMSClass(String classString) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
		String name = "net.minecraft.server." + version + classString;
		Class<?> nmsClass = null;
		try {
			nmsClass = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Unable to get NMS class! You are probably running an unsupported version!" + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
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
			Bukkit.getLogger().warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Unable to get OBC class! You are probably running an unsupported version!" + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
			return null;
		}
		return obcClass;
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
	}
}