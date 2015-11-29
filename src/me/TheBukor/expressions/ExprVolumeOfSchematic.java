package me.TheBukor.expressions;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@SuppressWarnings("deprecation")
public class ExprVolumeOfSchematic extends SimpleExpression<Integer> {
	private Expression<String> schematic;
	private Expression<String> folder;
	@SuppressWarnings("unused")
	private WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

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
		folder = (Expression<String>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the volume of the schematic " + schematic.toString(e, false) + " in the folder " + (folder.getSingle(e) == null ? "plugins/WorldEdit/schematics" : folder.toString(e, false));
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		String f = (folder.getSingle(e) == null) ? "plugins/WorldEdit/schematics/" : folder.getSingle(e);
		String schem = schematic.getSingle(e);
		File schemFile = new File((f.endsWith("/")) ? f : (f + "/") + (schem.endsWith(".schematic") ? schem : (schem + ".schematic")));
		Integer w = 0;
		Integer h = 0;
		Integer l = 0;
		try {
			w = MCEditSchematicFormat.getFormat(schemFile).load(schemFile).getWidth();
		} catch (DataException | IOException e1) {
			return null;
		}
		try {
			h = MCEditSchematicFormat.getFormat(schemFile).load(schemFile).getHeight();
		} catch (DataException | IOException e2) {
			return null;
		}
		try {
			l = MCEditSchematicFormat.getFormat(schemFile).load(schemFile).getLength();
		} catch (DataException | IOException e1) {
			return null;
		}
		return new Integer[] { w * h * l };
	}
}