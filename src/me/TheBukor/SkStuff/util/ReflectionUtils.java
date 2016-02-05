package me.TheBukor.SkStuff.util;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.fusesource.jansi.Ansi;

public class ReflectionUtils {

	public static Class<?> getNMSClass(String classString, boolean isArray) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
		String name = "net.minecraft.server." + version + classString;
		if (isArray)
			name = "[L" + name;
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
	
	public static Object getField(String field, Class<?> clazz, Object object) {
		Field f = null;
		Object obj = null;
		try {
			f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			obj = f.get(object);
			f.setAccessible(false);
		} catch (Exception ex) {
			if (f != null)
				f.setAccessible(false);
			ex.printStackTrace();
		}
		return obj;
	}
	
	public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
		Field f = null;
		try {
			f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			f.set(object, toSet);
			f.setAccessible(false);
		} catch (Exception ex) {
			if (f != null)
				f.setAccessible(false);
			ex.printStackTrace();
		}
	}

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
	}
}