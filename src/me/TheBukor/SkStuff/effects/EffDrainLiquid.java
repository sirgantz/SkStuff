package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffDrainLiquid extends Effect {
	private Expression<Location> location;
	private Expression<Double> radius;
	private Expression<EditSession> editSession;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		location = (Expression<Location>) expr[0];
		radius = (Expression<Double>) expr[1];
		editSession = (Expression<EditSession>) expr[2];
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "drain liquids at " + location.toString(e, false) + " in a radius of " + radius.toString(e, false);
	}

	@Override
	protected void execute(Event e) {
		Location loc = location.getSingle(e);
		Double rad = radius.getSingle(e);
		EditSession session = editSession.getSingle(e);
		if (session == null) return;
		try {
			session.drainArea(BukkitUtil.toVector(loc), rad);
			session.flushQueue();
		} catch (WorldEditException ex) {
			if (ex instanceof MaxChangedBlocksException)
				return;
			else
				ex.printStackTrace();
		}
	}
}