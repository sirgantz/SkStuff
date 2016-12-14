package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;

public class ExprLastLocation extends SimpleExpression<Location> {
	private Expression<Entity> entity;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entity = (Expression<Entity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "last location of " + entity.toString(e, debug);
	}

	@Override
	@Nullable
	protected Location[] get(Event e) {
		Entity ent = entity.getSingle(e);
		if (ent == null) {
			return null;
		}
			return new Location[] { SkStuff.getNMSMethods().getLastLocation(ent) };
	}
}