package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprTagOf extends SimpleExpression<Object> {
	private Expression<String> string;
	private Expression<Object> compound;

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound", false);
	private Class<?> nbtBaseClass = ReflectionUtils.getNMSClass("NBTBase", false);

	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}
	@Override
	public boolean isSingle() {
		return true;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2, ParseResult arg3) {
		string = (Expression<String>) expr[0];
		compound = (Expression<Object>) expr[1];
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the tag " + string.toString(e, debug) + " of compound";
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		Object NBT = compound.getSingle(e);
		if (NBT == null || NBT.toString().equals("{}")) return null; //The NBT can be empty/inexistant for items ("{}" is an empty compound).
		String stringTag = string.getSingle(e);
		Object tag = null;
		try {
			tag = nbtClass.getMethod("get", String.class).invoke(NBT, stringTag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (tag == null)
			return null; //The tag doesn't exist? Return <none>.
		Byte id = null;
		try {
			id = (Byte) tag.getClass().getMethod("getTypeId").invoke(tag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			switch (id) {
			case 1:
				return new Byte[] { Byte.valueOf(nbtClass.getMethod("getByte", String.class).invoke(NBT, stringTag).toString()) };
			case 2:
				return new Short[] { Short.valueOf(nbtClass.getMethod("getShort", String.class).invoke(NBT, stringTag).toString()) };
			case 3:
				return new Integer[] { Integer.valueOf(nbtClass.getMethod("getInt", String.class).invoke(NBT, stringTag).toString()) };
			case 4:
				return new Long[] { Long.valueOf(nbtClass.getMethod("getLong", String.class).invoke(NBT, stringTag).toString()) };
			case 5:
				return new Float[] { Float.valueOf(nbtClass.getMethod("getFloat", String.class).invoke(NBT, stringTag).toString()) };
			case 6:
				return new Double[] { Double.valueOf(nbtClass.getMethod("getDouble", String.class).invoke(NBT, stringTag).toString()) };
			case 7: //Byte array, only used in chunk files. Also doesn't have support for the MojangsonParser.
				break;
			case 8:
				return new String[] { nbtClass.getMethod("getString", String.class).invoke(NBT, stringTag).toString() };
			case 9:
				int i;
				Object[] list = new Object[] { new Object() };
				for (i = 1; i <= 11; i++) { //To get a list I need to know the type of the tags it contains inside,
					//since I can't predict what type the list will have, I just loop all of the IDs until I find a non-empty list.
					list[0] = nbtClass.getMethod("getList", String.class, int.class).invoke(NBT, stringTag, i); //Try to get the list with the ID "loop-number".
					if (!list[0].toString().equals("[]")) { //If list is not empty.
						break; //Stop loop.
					}
				}
				return list;
				/*
				REMOVED TEMPORARILY, HOPEFULLY THE NEW IMPLEMENTATION SHOULD WORK BETTER
				int i;
				Object list = null;
				for (i = 1; i <= 11; i++) { //To get a list I need to know the type of the tags it contains inside,
					//since I can't predict what type the list will have, I just loop all of the IDs until I find a non-empty list.
					list = nbtClass.getMethod("getList", String.class, int.class).invoke(NBT, stringTag, i); //Try to get the list with the ID "loop-number".
					if (!list.toString().equals("[]")) { //If list is not empty.
						break; //Stop loop.
					}
				}
				String methodName = null;
				switch (NBTUtil.getContentsId(list)) {
					case 5: //Float
						methodName = "e"; //list.e(int) = get float from the specified index.
						break;
					case 6: //Double
						methodName = "d"; //list.d(int) = get double from the specified index.
						break;
					case 8: //String
						methodName = "getString"; //Self-explanatory, I guess.
						break;
					case 10: //Compound
						methodName = "get"; //list.get(int) = get compound at the specified index.
						break;
					case 11: //Integer array
						methodName = "c"; //Not sure if ever used, but meh.
						break;
					default:
						break;
				}
				int listSize = (int) list.getClass().getMethod("size").invoke(list);
				Object[] tags = new Object[listSize];
				for (i = 0; i < listSize; i++) {
					Object gottenTag = list.getClass().getMethod(methodName, int.class).invoke(list, i);
					tags[i] = gottenTag;
				}
				return tags;
				*/
			case 10:
				return new Object[] { nbtClass.getMethod("getCompound", String.class).invoke(NBT, stringTag) };
			case 11: //Integer array, this one is only used on the chunk files (and maybe schematic files?).
				return new Object[] { nbtClass.getMethod("getIntArray", String.class).invoke(NBT, stringTag).toString() };
			default: //This shouldn't happen, but it's better to have this just in case it spills errors everywhere.
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Object NBT = compound.getSingle(e);
		if (NBT == null)
			try {
				NBT = nbtClass.newInstance(); //If the NBT isn't set, create an empty one
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		String stringTag = string.getSingle(e);
		if (mode == ChangeMode.SET) {
			Object newValue = delta[0];
			try {
				if (newValue instanceof Byte) {
					nbtClass.getMethod("setByte", String.class, byte.class).invoke(NBT, stringTag, ((Byte) newValue).byteValue());
				} else if (newValue instanceof Short) {
					nbtClass.getMethod("setShort", String.class, short.class).invoke(NBT, stringTag, ((Short) newValue).shortValue());
				} else if (newValue instanceof Integer) {
					nbtClass.getMethod("setInt", String.class, int.class).invoke(NBT, stringTag, ((Integer) newValue).intValue());
				} else if (newValue instanceof Long) {
					nbtClass.getMethod("setLong", String.class, long.class).invoke(NBT, stringTag, ((Long) newValue).longValue());
				} else if (newValue instanceof Float) {
					nbtClass.getMethod("setFloat", String.class, float.class).invoke(NBT, stringTag, ((Float) newValue).floatValue());
				} else if (newValue instanceof Double) {
					nbtClass.getMethod("setDouble", String.class, double.class).invoke(NBT, stringTag, ((Double) newValue).doubleValue());
				} else if (newValue instanceof String) {
					nbtClass.getMethod("setString", String.class, String.class).invoke(NBT, stringTag, String.valueOf(newValue));
				} else {
					return; //Something else like a list or entire compound.
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			try {
				nbtClass.getMethod("set", String.class, nbtBaseClass).invoke(NBT, stringTag, nbtBaseClass.newInstance());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}
}