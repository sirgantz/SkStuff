package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.SkStuff;

public class ExprGlideState extends SimpleExpression<Boolean> {
	private Expression<LivingEntity> entity;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entity = (Expression<LivingEntity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "elytra gliding state of " + entity.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		LivingEntity ent = entity.getSingle(e);
		return new Boolean[] { SkStuff.getNMSMethods().getElytraGlideState(ent) };
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
		LivingEntity ent = entity.getSingle(e);
		if (mode == ChangeMode.SET) {
			boolean newValue = (boolean) delta[0];
			SkStuff.getNMSMethods().setElytraGlideState(ent, newValue);
		}
	}
}
