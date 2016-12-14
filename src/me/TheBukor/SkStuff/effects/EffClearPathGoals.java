package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;

public class EffClearPathGoals extends Effect {
	private Expression<LivingEntity> entities;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		entities = (Expression<LivingEntity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "clear all pathfinder goals from " + entities.toString(e, debug);
	}

	@Override
	protected void execute(Event e) {
		LivingEntity[] ents = entities.getAll(e);
		for (LivingEntity ent : ents) {
			if (!(ent instanceof Player || ent instanceof ArmorStand || ent == null)) {
				SkStuff.getNMSMethods().clearPathfinderGoals(ent);
			}
		}
	}
}