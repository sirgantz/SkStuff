package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprFireProof extends SimpleExpression<Boolean> {
	private Expression<Entity> entity;

	private Class<?> craftEntClass = ReflectionUtils.getOBCClass("entity.CraftEntity");
	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		entity = (Expression<Entity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "fireproof state of " + entity.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		Entity ent = entity.getSingle(e); 
		if (ent == null)
			return null;
		Object nmsEnt;
		Boolean fireProof = null;
		try {
			nmsEnt = craftEntClass.getMethod("getHandle").invoke(ent);
			fireProof = (Boolean) nmsEnt.getClass().getMethod("isFireProof").invoke(nmsEnt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Boolean[] { fireProof };
	}
	
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Entity ent = entity.getSingle(e);
		if (ent == null) 
			return;
		Object nmsEnt = null;
		try {
			nmsEnt = craftEntClass.getMethod("getHandle").invoke(ent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (mode == ChangeMode.SET) {
			Boolean newValue = (Boolean) delta[0];
			ReflectionUtils.setField("fireProof", nmsEnt.getClass(), nmsEnt, newValue);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Boolean.class);
		}
		return null;
	}
}