package me.TheBukor.effects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffNaturalize extends Effect {
	private Expression<Location> location1;
	private Expression<Location> location2;
	private Expression<EditSession> editSession;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		location1 = (Expression<Location>) expr[0];
		location2 = (Expression<Location>) expr[1];
		editSession = (Expression<EditSession>) expr[2];
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "naturalize all blocks from " + location1.toString(e, false) + " to " + location2.toString(e, false);
	}

	@Override
	protected void execute(Event e) {
		Location pos1 = location1.getSingle(e);
		Location pos2 = location2.getSingle(e);
		EditSession session = editSession.getSingle(e);
		if (session == null) return;
		CuboidRegion region = new CuboidRegion(BukkitUtil.toVector(pos1), BukkitUtil.toVector(pos2));
		try {
			session.naturalizeCuboidBlocks(region);
			session.flushQueue();
		} catch (WorldEditException ex) {
			if (ex instanceof MaxChangedBlocksException)
				return;
			else
				ex.printStackTrace();
		}
	}
}