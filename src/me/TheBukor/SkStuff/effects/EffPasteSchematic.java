package me.TheBukor.SkStuff.effects;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@SuppressWarnings("deprecation")
public class EffPasteSchematic extends Effect {
	private Expression<String> schematic;
	private Expression<Location> location;
	private Expression<EditSession> editSession;

	private int mark;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		mark = result.mark;
		schematic = (Expression<String>) expr[0];
		location = (Expression<Location>) expr[1];
		editSession = (Expression<EditSession>) expr[2];
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "paste schematic " + schematic.toString(e, debug) + " at " + location.toString(e, debug) + " using edit session" + (mark == 1 ? " ignoring air" : "");
	}

	@Override
	protected void execute(Event e) {
		String schem = schematic.getSingle(e);
		Location loc = location.getSingle(e);
		EditSession session = editSession.getSingle(e);
		Vector origin = BukkitUtil.toVector(loc);
		boolean noAir = false;
		if (mark == 1)
			noAir = true;
		File schemFile = new File((schem.endsWith(".schematic") ? schem : (schem + ".schematic")));
		if (!schemFile.exists() || session == null)
			return;
		try {
			SchematicFormat.getFormat(schemFile).load(schemFile).paste(session, origin, noAir);
		} catch (MaxChangedBlocksException | DataException | IOException ex) {
			if (ex instanceof MaxChangedBlocksException)
				return;
			ex.printStackTrace();
		}
	}
}