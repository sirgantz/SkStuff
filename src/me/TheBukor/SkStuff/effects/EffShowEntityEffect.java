package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffShowEntityEffect extends Effect {
	private Expression<Entity> entity;
	
	private int mark;
	private String toStringMark;
	
	private EntityEffect effect;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		entity = (Expression<Entity>) expr[0];
		mark = result.mark;
		if (mark == 0) {
			effect = EntityEffect.FIREWORK_EXPLODE;
			toStringMark = "fireworks explosion";
		} else if (mark == 1) {
			effect = EntityEffect.HURT;
			toStringMark = "hurt";
		} else if (mark == 2) {
			effect = EntityEffect.IRON_GOLEM_ROSE;
			toStringMark = "iron golem offer rose";
		} else if (mark == 3) {
			effect = EntityEffect.SHEEP_EAT;
			toStringMark = "sheep eat grass";
		} else if (mark == 4) {
			effect = EntityEffect.WOLF_SHAKE;
			toStringMark = "wolf shake";
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "play entity effect " + toStringMark + "at" + entity.toString(e, debug);
	}

	@Override
	protected void execute(Event e) {
		Entity ent = entity.getSingle(e);
		if (ent != null)
			ent.playEffect(effect);
	}
}