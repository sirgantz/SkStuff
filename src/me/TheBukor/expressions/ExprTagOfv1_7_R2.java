package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.NBTTagList;

public class ExprTagOfv1_7_R2 extends SimpleExpression<Object> {
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
		if (NBT.isEmpty() || NBT == null) return null; //The NBT can be empty/inexistant for items
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
			returned = new NBTTagList[] { NBT.getList(tag, 5) }; //Is the int argument the type ID?
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
}