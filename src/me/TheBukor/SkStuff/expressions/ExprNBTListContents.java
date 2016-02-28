package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

//import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
//import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.SkStuff;
//import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprNBTListContents extends SimpleExpression<Object> {
	private Expression<Object> nbtList;

	//private Class<?> nbtListClass = ReflectionUtils.getNMSClass("NBTTagList", false);
	//private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound", false);

	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		nbtList = (Expression<Object>) expr[0];
		return false;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "contents from NBT list " + nbtList.toString(e, debug);
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		Object list = nbtList.getSingle(e);
		SkStuff.getNMSMethods().getContents(list);
		return null;
	}

	/*
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Object list = nbtList.getSingle(e);
		if (mode == ChangeMode.ADD) {
			if (!(delta[0] instanceof Number || delta[0] instanceof String || nbtListClass.isAssignableFrom(delta[0].getClass()) || nbtClass.isAssignableFrom(delta[0].getClass())))
				return; //NBT can only store numbers, strings, lists or compounds.
			SkStuff.getNMSMethods().addToList(list, delta[0]);
		} else if (mode == ChangeMode.REMOVE) {
			// TODO A method to remove a specific object from a NBT List
		} else if (mode == ChangeMode.REMOVE_ALL) {
			// TODO A method to remove all objects of some type from a NBT List
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}
	*/
}
