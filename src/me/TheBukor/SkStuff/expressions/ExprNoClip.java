package me.TheBukor.SkStuff.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class ExprNoClip extends SimpleExpression<Boolean> {
	private Expression<Entity> entities;

	private Class<?> craftEntClass = ReflectionUtils.getOBCClass("entity.CraftEntity");

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		entities = (Expression<Entity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the no clip state of " + entities.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		Entity[] ents = entities.getAll(e); 
		if (ents == null)
			return null;
		List<Boolean> noClipStates = new ArrayList<Boolean>();
		for (Entity ent : ents) {
			if (ent == null)
				continue;
			Object nmsEnt = null;
			try {
				nmsEnt = craftEntClass.cast(ent).getClass().getMethod("getHandle").invoke(ent); //nmsEnt = ((CraftEntity) ent).getHandle();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			noClipStates.add((Boolean) ReflectionUtils.getField("noclip", nmsEnt.getClass(), nmsEnt));
		}
		return Arrays.copyOf(noClipStates.toArray(), noClipStates.size(), Boolean[].class);
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Entity[] ents = entities.getAll(e);
		if (ents == null) 
			return;
		if (mode == ChangeMode.SET) {
			Boolean newValue = (Boolean) delta[0];
			for (Entity ent : ents) {
				if (ent == null)
					continue;
				Object nmsEnt = null;
				try {
					nmsEnt = craftEntClass.cast(ent).getClass().getMethod("getHandle").invoke(ent); //nmsEnt = ((CraftEntity) ent).getHandle();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				ReflectionUtils.setField("noclip", nmsEnt.getClass(), nmsEnt, newValue);
			}
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