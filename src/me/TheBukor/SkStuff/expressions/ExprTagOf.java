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

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound");
	private Class<?> nbtBaseClass = ReflectionUtils.getNMSClass("NBTBase");

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
	public String toString(@Nullable Event e, boolean arg1) {
		return "the tag " + string.toString(e, false) + " of compound";
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		Object NBT = compound.getSingle(e);
		if (NBT == null || NBT.toString().equals("{}")) return null; //The NBT can be empty/inexistant for items ("{}" is an empty compound).
		String stringTag = string.getSingle(e);
		Object tag = null;
		try {
			tag = NBT.getClass().getMethod("get", String.class).invoke(NBT, stringTag);
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
				return new Byte[] { Byte.valueOf(NBT.getClass().getMethod("getByte", String.class).invoke(NBT, stringTag).toString()) };
			case 2:
				return new Short[] { Short.valueOf(NBT.getClass().getMethod("getShort", String.class).invoke(NBT, stringTag).toString()) };
			case 3:
				return new Integer[] { Integer.valueOf(NBT.getClass().getMethod("getInt", String.class).invoke(NBT, stringTag).toString()) };
			case 4:
				return new Long[] { Long.valueOf(NBT.getClass().getMethod("getLong", String.class).invoke(NBT, stringTag).toString()) };
			case 5:
				return new Float[] { Float.valueOf(NBT.getClass().getMethod("getFloat", String.class).invoke(NBT, stringTag).toString()) };
			case 6:
				return new Double[] { Double.valueOf(NBT.getClass().getMethod("getDouble", String.class).invoke(NBT, stringTag).toString()) };
			case 7: //Byte array, only used in chunk files. Also doesn't have support for the MojangsonParser.
				break;
			case 8:
				return new String[] { NBT.getClass().getMethod("getString", String.class).invoke(NBT, stringTag).toString() };
				//Lists will be probably an ASS to implement when I get to them
			case 9:
				int i;
				Object list = null;
				for (i = 1; i <= 11; i++) { //To get a list I need to know the type of the tags it contains inside,
					//since I can't predict what type the list will have, I just loop all of the IDs until I find a non-empty list.
					list = NBT.getClass().getMethod("getList", String.class, int.class).invoke(NBT, stringTag, i);
					if (!list.toString().equals("[]")) { //If list is not empty.
						break; //Stop loop.
					}
				}
				String methodName = null;
				switch (((int) list.getClass().getMethod("f").invoke(list))) { //list.f() gets the type of the tags in the list.
					case 5: //Float
						methodName = "e";
						break;
					case 6: //Double
						methodName = "d";
						break;
					case 8: //String
						methodName = "getString";
						break;
					case 10: //Compound
						methodName = "get";
						break;
					case 11: //Integer array
						methodName = "c";
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
			case 10:
				return new Object[] { NBT.getClass().getMethod("getCompound", String.class).invoke(NBT, stringTag) };
			case 11: //Integer array, this one is only used on the chunk files.
				return new Object[] { NBT.getClass().getMethod("getIntArray", String.class).invoke(NBT, stringTag).toString() };
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
					NBT.getClass().getMethod("setByte", String.class, byte.class).invoke(NBT, stringTag, (byte) newValue);
				} else if (newValue instanceof Short) {
					NBT.getClass().getMethod("setShort", String.class, short.class).invoke(NBT, stringTag, (short) newValue);
				} else if (newValue instanceof Integer) {
					NBT.getClass().getMethod("setInt", String.class, int.class).invoke(NBT, stringTag, (int) newValue);
				} else if (newValue instanceof Long) {
					NBT.getClass().getMethod("setLong", String.class, long.class).invoke(NBT, stringTag, (long) newValue);
				} else if (newValue instanceof Float) {
					NBT.getClass().getMethod("setFloat", String.class, float.class).invoke(NBT, stringTag, (float) newValue);
				} else if (newValue instanceof Double) {
					NBT.getClass().getMethod("setDouble", String.class, double.class).invoke(NBT, stringTag, (double) newValue);
				} else if (newValue instanceof String) {
					NBT.getClass().getMethod("setString", String.class, String.class).invoke(NBT, stringTag, (String) newValue);
				} else {
					return; //Something else like a list or entire compound.
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			try {
				NBT.getClass().getMethod("set", String.class, nbtBaseClass).invoke(NBT, stringTag, nbtBaseClass.newInstance());
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