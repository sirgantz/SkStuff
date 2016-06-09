package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprNoGravityState extends SimpleExpression<Boolean> {
	private Expression<Entity> entities;

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entities = (Expression<Entity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "no gravity state of " + entities.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		Entity[] ents = entities.getAll(e);
		if (ents.length == 0)
			return null;
		Boolean[] gravityStates = new Boolean[ents.length];
		int i = 0;
		for (Entity ent : ents) {
			if (ent == null)
				continue;
			net.minecraft.server.v1_10_R1.Entity nmsEnt = ((CraftEntity) ent).getHandle();
			gravityStates[i] = nmsEnt.isNoGravity();
		}
		return gravityStates;
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

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Entity[] ents = entities.getAll(e);
		if (ents.length == 0)
			return;
		if (mode == ChangeMode.SET) {
			boolean newValue = (boolean) delta[0];
			for (Entity ent : ents) {
				if (ent == null)
					return;
				net.minecraft.server.v1_10_R1.Entity nmsEnt = ((CraftEntity) ent).getHandle();
				nmsEnt.setNoGravity(newValue);
			}
		}
	}
}
