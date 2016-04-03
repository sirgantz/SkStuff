package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.SkStuff;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprTagOf extends SimpleExpression<Object> {
	private Expression<String> string;
	private Expression<Object> compound;

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
	public String toString(@Nullable Event e, boolean debug) {
		return "the tag " + string.toString(e, debug) + " of " + compound.toString(e, debug);
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		Object NBT = compound.getSingle(e);
		if (NBT == null || NBT.toString().equals("{}")) { // "{}" is an empty compound.
			return null; //The NBT can be empty/inexistant for items
		}
		String stringTag = string.getSingle(e);
		Object tag = SkStuff.getNMSMethods().getNBTTag(NBT, stringTag);
		if (tag == null) {
			return null; //The tag doesn't exist? Return <none>.
		}
		byte id = SkStuff.getNMSMethods().getTypeId(tag);
		return new Object[] { SkStuff.getNMSMethods().getNBTTagValue(NBT, stringTag, id) };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Object NBT = compound.getSingle(e);
		if (NBT == null) {
			return;
		}
		String stringTag = string.getSingle(e);
		if (mode == ChangeMode.SET) {
			Object newValue = delta[0];
			SkStuff.getNMSMethods().setNBTTag(NBT, stringTag, newValue);
		} else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			SkStuff.getNMSMethods().removeNBTTag(NBT, stringTag);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			return CollectionUtils.array(Number.class, String.class, nbtBaseClass);
		}
		return null;
	}
}