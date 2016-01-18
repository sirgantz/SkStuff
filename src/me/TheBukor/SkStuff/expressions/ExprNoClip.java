package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprNoClip extends SimpleExpression<Boolean> {
	private Expression<Entity> entity;

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
	public String toString(@Nullable Event e, boolean arg1) {
		return "no clip state of " + entity.toString(e, false);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		Entity ent = entity.getSingle(e); 
		if (ent == null)
			return null;
		return new Boolean[] { ((CraftEntity) ent).getHandle().noclip };
	}
	
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Entity ent = entity.getSingle(e);
		if (ent == null) 
			return;
		if (mode == ChangeMode.SET) {
			Boolean newValue = (Boolean) delta[0];
			((CraftEntity) ent).getHandle().noclip = newValue;
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