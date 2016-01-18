package me.TheBukor.SkStuff.expressions;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
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
		return "the volume of the schematic from " + schematic.toString(e, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	protected Integer[] get(Event e) {
		String schem = schematic.getSingle(e);
		File schemFile = new File((schem.endsWith(".schematic") ? schem : (schem + ".schematic")));
		Vector v = null;
		try {
			v = MCEditSchematicFormat.getFormat(schemFile).load(schemFile).getSize();
		} catch (DataException | IOException ex) {
			return null;
		}
		Bukkit.broadcastMessage("vector: " + v.getX() + v.getY() + v.getZ());
		return new Integer[] { 1 };
	}
}