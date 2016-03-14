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

public class ExprNBTListIndex extends SimpleExpression<Object> {
	private Expression<Object> nbtList;
	private Expression<Number> index;

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
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		nbtList = (Expression<Object>) expr[0];
		index = (Expression<Number>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "NBT list" + nbtList.toString(e, debug) + " index " + index.toString(e, debug);
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		int i = index.getSingle(e).intValue();
		Object list = nbtList.getSingle(e);
		return new Object[] { SkStuff.getNMSMethods().getIndex(list, i) };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		int i = index.getSingle(e).intValue();
		i--;
		Object list = nbtList.getSingle(e);
		if (mode == ChangeMode.SET) {
			if (!(delta[0] instanceof Number || delta[0] instanceof String || nbtBaseClass.isAssignableFrom(delta[0].getClass())))
				//All NBTTags extends NBTBase, so it will check if delta[0] is instance of NBTTagList or NBTTagCompound, because these are the only NBTTagX classes registered in this addon.
				return; //NBT can only store numbers, strings, lists or compounds.
			SkStuff.getNMSMethods().setIndex(list, i, delta[0]);
		} else if (mode == ChangeMode.DELETE) {
			SkStuff.getNMSMethods().removeFromList(list, i);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}
}
