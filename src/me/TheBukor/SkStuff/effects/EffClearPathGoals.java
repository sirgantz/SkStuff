package me.TheBukor.SkStuff.effects;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffClearPathGoals extends Effect {
	private Expression<LivingEntity> entity;

	private Class<?> goalSelectorClass = ReflectionUtils.getNMSClass("PathfinderGoalSelector");
	private Class<?> insentientEnt = ReflectionUtils.getNMSClass("EntityInsentient");
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		entity = (Expression<LivingEntity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "clear all pathfinder goals from " + entity.toString(e, debug);
	}

	@Override
	protected void execute(Event e) {
		LivingEntity ent = entity.getSingle(e);
		if (ent instanceof Player || ent == null)
			return;
		Object obcEnt = craftLivEnt.cast(ent);
		try {
			Object nmsEnt = insentientEnt.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
			Object goalSelector = ReflectionUtils.getField("goalSelector", insentientEnt, nmsEnt);
			Object targetSelector = ReflectionUtils.getField("targetSelector", insentientEnt, nmsEnt);
			((List<?>) ReflectionUtils.getField("b", goalSelectorClass, goalSelector)).clear();
			((List<?>) ReflectionUtils.getField("c", goalSelectorClass, goalSelector)).clear();
			((List<?>) ReflectionUtils.getField("b", goalSelectorClass, targetSelector)).clear();
			((List<?>) ReflectionUtils.getField("c", goalSelectorClass, targetSelector)).clear();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}