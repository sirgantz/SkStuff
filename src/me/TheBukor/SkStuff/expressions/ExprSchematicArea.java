package me.TheBukor.SkStuff.expressions;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprSchematicArea extends SimpleExpression<Integer> {
	private Expression<String> schematic;
	
	private int parseMark;
	private String toStringMark;

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
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		schematic = (Expression<String>) expr[0];
		parseMark = result.mark;
		if (parseMark == 0) {
			toStringMark = "volume";
		} else if (parseMark == 1) {
			toStringMark = "width (x-size)";
		} else if (parseMark == 2) {
			toStringMark = "height (y-size)";
		} else if (parseMark == 3) {
			toStringMark = "length (z-size)";
		} else if (parseMark == 4) {
			toStringMark = "area";
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the " + toStringMark + " of the schematic from " + schematic.toString(e, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	protected Integer[] get(Event e) {
		String schem = schematic.getSingle(e);
		File schemFile = new File((schem.endsWith(".schematic") ? schem : (schem + ".schematic")));
		Vector size = null;
		try {
			size = MCEditSchematicFormat.getFormat(schemFile).load(schemFile).getSize();
		} catch (DataException | IOException ex) {
			return null;
		}
		Number result = null;
		if (parseMark == 0) {
			result = (size.getX() * size.getY() * size.getZ());
		} else if (parseMark == 1) {
			result = size.getX();
		} else if (parseMark == 2) {
			result = size.getY();
		} else if (parseMark == 3) {
			result = size.getZ();
		} else if (parseMark == 4) {
			result = (size.getX() * size.getZ());
		}
		return new Integer[] { result.intValue() };
	}
}