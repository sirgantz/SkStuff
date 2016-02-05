package me.TheBukor.SkStuff.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fusesource.jansi.Ansi;

import ch.njol.skript.Skript;

public class NBTUtil {
	private static Class<?> nbtBaseClass = ReflectionUtils.getNMSClass("NBTBase", false);
	private static Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound", false);
	private static Class<?> nbtListClass = ReflectionUtils.getNMSClass("NBTTagList", false);

	/**
	 * This is actually a copy of the "a(NBTTagCompound)" method in the NBTTagCompound class.
	 * I needed to add this because the 1.7 and before versions of the NBTTagCompound class didn't have this method,
	 * so there wasn't actually a reliable way to multiple tags at once into a compound.
	 * For the original code for the method, check https://github.com/linouxis9/mc-dev-1.8.7/blob/master/net/minecraft/server/NBTTagCompound.java#L348
	 * 
	 * Please note that I adapted it to work using reflection.
	 */
	@SuppressWarnings("unchecked")
	public static void addCompound(Object NBT, Object toAdd) {
		if (NBT.getClass() == nbtClass && toAdd.getClass() == nbtClass) {
			try {
				HashMap<String, Object> map = (HashMap<String, Object>) ReflectionUtils.getField("map", nbtClass, toAdd);
				Set<String> keySet = (Set<String>) nbtClass.getMethod("c").invoke(toAdd);
				Iterator<String> iterator = keySet.iterator();

				while(iterator.hasNext()) {
					String string = (String) iterator.next();
					Object base = nbtBaseClass.cast(map.get(string));
					if((byte) nbtBaseClass.getMethod("getTypeId").invoke(base) == 10) {
						if((boolean) nbtClass.getMethod("hasKeyOfType", String.class, int.class).invoke(NBT, string, 10)) {
							Object localNBT = null;
							localNBT = nbtClass.getMethod("getCompound", String.class).invoke(localNBT, string);
							NBTUtil.addCompound(localNBT, nbtBaseClass.cast(nbtClass));
						} else {
							nbtClass.getMethod("set", String.class, nbtBaseClass).invoke(NBT, string, base.getClass().getMethod("clone").invoke(base));
						}
					} else {
						nbtClass.getMethod("set", String.class, nbtBaseClass).invoke(NBT, string, base.getClass().getMethod("clone").invoke(base));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Gets the ID of the contents inside a NBT List.
	 * I needed to add this because the 1.7 and before versions of the NBTTagList
	 * class had a different name for the method that got this value.
	 * 1.8 used "f()", while 1.7 used "d()".
	 */
	public static int getContentsId(Object list) {
		if (list.getClass() == nbtListClass) {
			Field type = null;
			int result = 0;
			try {
				type = nbtListClass.getDeclaredField("type");
				type.setAccessible(true);
				result = type.getInt(list);
				type.setAccessible(false);
				return result;
			} catch (Exception ex) {
				type.setAccessible(false);
				ex.printStackTrace();
			}
			return result;
		}
		return 0;
	}

	/**
	 * Used for the "addToList()" and "setIndex()" methods if the typeId of the contents is still not defined.
	 */
	public static void setContentsId(Object list, int newId) {
		if (list.getClass() == nbtListClass) {
			Field type = null;
			try {
				type = nbtListClass.getDeclaredField("type");
				type.setAccessible(true);
				type.set(list, newId);
				type.setAccessible(false);
			} catch (Exception ex) {
				type.setAccessible(false);
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Object> getContents(Object list) {
		if (list.getClass() == nbtListClass) {
			List<Object> result = null;
			try {
				result = (List<Object>) ReflectionUtils.getField("list", nbtListClass, list);
				return result;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return result;
		}
		return null;
	}

	/**
	 * Kind of a copy of the "add()" method from the NBTTagList class.
	 */
	public static void addToList(Object list, Object[] toAdd) {
		if (list.getClass() == nbtListClass && toAdd[0].getClass() == nbtBaseClass) {
			int listTypeId = NBTUtil.getContentsId(list);
			int toAddId = 0;
			try {
				toAddId = (int) toAdd.getClass().getMethod("getTypeId").invoke(toAdd);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (listTypeId == 0) {
				try {
					NBTUtil.setContentsId(list, toAddId);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (listTypeId != toAddId) {
				Skript.warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Adding mismatching tag types to NBT list" + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
				return;
			}

			for (Object tag : toAdd) {
				NBTUtil.getContents(list).add(tag);
			}
		}
	}
	
	public static void removefromList(Object list, int index) {
		if (list.getClass() == nbtListClass) {
			if (index >= 0 && index < NBTUtil.getContents(list).size()) {
				NBTUtil.getContents(list).remove(index);
			}
		}
	}
	
	public static void setIndex(Object list, int index, Object toAdd) {
		if (list.getClass() == nbtListClass && toAdd.getClass() == nbtBaseClass) {
			if (index >= 0 && index < NBTUtil.getContents(list).size()) {
				int listTypeId = NBTUtil.getContentsId(list);
				int toAddId = 0;
				try {
					toAddId = (int) toAdd.getClass().getMethod("getTypeId").invoke(toAdd);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (listTypeId == 0) {
					try {
						NBTUtil.setContentsId(list, toAddId);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (listTypeId != toAddId) {
					Skript.warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Adding mismatching tag types to NBT list" + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
					return;
				}
				
				NBTUtil.getContents(list).set(index, toAdd);
			}
		}
	}
	
	public static Object getIndex(Object list, int index) {
		if (list.getClass() == nbtListClass) {
			if (index >= 0 && index < NBTUtil.getContents(list).size()) {
				NBTUtil.getContents(list).get(index);
			}
		}
		return null;
	}
}