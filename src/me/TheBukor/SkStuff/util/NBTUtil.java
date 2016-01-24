package me.TheBukor.SkStuff.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class NBTUtil {
	private static Class<?> nbtBaseClass = ReflectionUtils.getNMSClass("NBTBase");
	private static Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound");

	/** 
	 * This is actually a copy of the "a(NBTTagCompound)" method in the NBTTagCompound class using reflection
	 * I needed to add this because the 1.7 and before versions of the NBTTagCompound class didn't have this method,
	 * so there wasn't actually a reliable way to multiple tags at once into a compound.
	 * For the original code for the method, check https://github.com/linouxis9/mc-dev-1.8.7/blob/master/net/minecraft/server/NBTTagCompound.java#L348
	 */
	@SuppressWarnings("unchecked")
	public static void addCompound(Object source, Object toAdd) {
		if (source.getClass().getName().contains("NBTTagCompound") && toAdd.getClass().getName().contains("NBTTagCompound")) {
			try {
				Field map = nbtClass.getDeclaredField("map");
				map.setAccessible(true);
				Set<String> keySet = (Set<String>) nbtClass.getMethod("c").invoke(toAdd);
				Iterator<String> iterator = keySet.iterator();

				while(iterator.hasNext()) {
					String string = (String) iterator.next();
					Object base = nbtBaseClass.cast((((HashMap<String, Object>) map.get(toAdd)).get(string)));
					if((byte) nbtBaseClass.getMethod("getTypeId").invoke(base) == 10) {
						if((boolean) nbtClass.getMethod("hasKeyOfType", String.class, int.class).invoke(source, string, 10)) {
							Object NBT = null;
							NBT = nbtClass.getMethod("getCompound", String.class).invoke(NBT, string);
							NBTUtil.addCompound(NBT, base.getClass().cast(nbtClass));
						} else {
							nbtClass.getMethod("set", String.class, nbtBaseClass).invoke(source, string, base.getClass().getMethod("clone").invoke(base));
						}
					} else {
						nbtClass.getMethod("set", String.class, nbtBaseClass).invoke(source, string, base.getClass().getMethod("clone").invoke(base));
					}
				}
				map.setAccessible(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
