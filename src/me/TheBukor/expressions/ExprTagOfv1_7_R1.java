package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minecraft.server.v1_7_R1.NBTTagCompound;

public class ExprTagOfv1_7_R1 extends SimpleExpression<Object> {
	private Expression<String> string;
	private Expression<NBTTagCompound> compound;
	private Object[] returned;
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
		compound = (Expression<NBTTagCompound>) expr[1];
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the tag " + string.toString(e, false) + " of compound";
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		NBTTagCompound NBT = compound.getSingle(e);
		if (NBT == null || NBT.isEmpty()) return null; //The NBT can be empty/inexistant for items
		String tag = string.getSingle(e);
		if (NBT.get(tag) == null) return null; //The tag doesn't exist? Return <none>.
		Byte id = NBT.get(tag).getTypeId();
		switch (id) {
		case 1:
			returned = new Byte[] { NBT.getByte(tag) };
			break;
		case 2:
			returned = new Short[] { NBT.getShort(tag) };
			break;
		case 3:
			returned = new Integer[] { NBT.getInt(tag) };
			break;
		case 4:
			returned = new Long[] { NBT.getLong(tag) };
			break;
		case 5:
			returned = new Float[] { NBT.getFloat(tag) };
			break;
		case 6:
			returned = new Double[] { NBT.getDouble(tag) };
			break;
		case 7: //Byte array, never seen this kind of tag (where is it used?)
			break;
		case 8:
			returned = new String[] { NBT.getString(tag) };
			break;
		case 9: //List, will need to make a new type if getCompound() doesn't work here
			Bukkit.broadcastMessage("LIST!");
			returned = new Object[] { NBT.getList(tag, 0).toString() }; //Is the int argument the type ID?
			break;
		case 10:
			returned = new NBTTagCompound[] { NBT.getCompound(tag) };
			break;
		case 11: //Integer array, this one is only used on the chunk files I believe
			break;
		default:
			break;
		}
		return returned;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		NBTTagCompound NBT = compound.getSingle(e);
		if (NBT == null) NBT = new NBTTagCompound(); //If the NBT isn't set, create an empty one
		String tag = string.getSingle(e);
		Object newValue = delta[0];
		if (mode == ChangeMode.SET) {
			if (newValue instanceof Byte) {
				NBT.setByte(tag, (byte) newValue);
			} else if (newValue instanceof Short) {
				NBT.setShort(tag, (short) newValue);
			} else if (newValue instanceof Integer) {
				NBT.setInt(tag, (int) newValue);;
			} else if (newValue instanceof Long) {
				NBT.setLong(tag, (long) newValue);
			} else if (newValue instanceof Float) {
				NBT.setFloat(tag, (float) newValue);
			} else if (newValue instanceof Double) {
				NBT.setDouble(tag, (double) newValue);
			} else if (newValue instanceof String) {
				NBT.setString(tag, (String) newValue);
			} else {
				return; //Non-supported type or maybe an error occured?
			}
		} else if (mode == ChangeMode.ADD) {
			if (newValue instanceof Byte) {
				newValue = NBT.getByte(tag) + (byte) newValue; 
				NBT.setByte(tag, (byte) newValue);
			} else if (newValue instanceof Short) {
				newValue = NBT.getShort(tag) + (short) newValue;
				NBT.setShort(tag, (short) newValue);
			} else if (newValue instanceof Integer) {
				newValue = NBT.getInt(tag) + (int) newValue;
				NBT.setInt(tag, (int) newValue);
			} else if (newValue instanceof Long) {
				newValue = NBT.getLong(tag) + (long) newValue;
				NBT.setLong(tag, (long) newValue);
			} else if (newValue instanceof Float) {
				newValue = NBT.getFloat(tag) + (float) newValue;
				NBT.setFloat(tag, (float) newValue);
			} else if (newValue instanceof Double) {
				newValue = NBT.getDouble(tag) + (double) newValue;
				NBT.setDouble(tag, (double) newValue);
			} else {
				return; //Non-supported type or maybe an error occured?
			}
		} else if (mode == ChangeMode.REMOVE) {
			if (newValue instanceof Byte) {
				newValue = NBT.getByte(tag) - (byte) newValue; 
				NBT.setByte(tag, (byte) newValue);
			} else if (newValue instanceof Short) {
				newValue = NBT.getShort(tag) - (short) newValue;
				NBT.setShort(tag, (short) newValue);
			} else if (newValue instanceof Integer) {
				newValue = NBT.getInt(tag) - (int) newValue;
				NBT.setInt(tag, (int) newValue);
			} else if (newValue instanceof Long) {
				newValue = NBT.getLong(tag) - (long) newValue;
				NBT.setLong(tag, (long) newValue);
			} else if (newValue instanceof Float) {
				newValue = NBT.getFloat(tag) - (float) newValue;
				NBT.setFloat(tag, (float) newValue);
			} else if (newValue instanceof Double) {
				newValue = NBT.getDouble(tag) - (double) newValue;
				NBT.setDouble(tag, (double) newValue);
			} else {
				return; //Non-supported type or maybe an error occured?
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}
}