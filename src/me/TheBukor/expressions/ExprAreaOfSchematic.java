package me.TheBukor.expressions;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprAreaOfSchematic extends SimpleExpression<Integer> {
	private Expression<String> schematic;

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		schematic = (Expression<String>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the area of the schematic from " + schematic.toString(e, false);
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		String schem = schematic.getSingle(e);
		File schemFile = new File((schem.endsWith(".schematic") ? schem : (schem + ".schematic")));
		Region region = null;
		try {
			region = ((Clipboard) MCEditSchematicFormat.getFormat(schemFile).load(schemFile)).getRegion();
		} catch (DataException | IOException ex) {
			return null;
		}
		return new Integer[] { region.getWidth() * region.getLength() };
	}
}