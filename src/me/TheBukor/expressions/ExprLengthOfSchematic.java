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
public class ExprLengthOfSchematic extends SimpleExpression<Integer> {
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
		return "the length of the schematic " + schematic.toString(e, false) + " in the folder " + new String(folder.getSingle(e) != null ? folder.toString(e, false) : "plugins/WorldEdit/schematics");
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		String f = (folder.getSingle(e) != null) ? folder.getSingle(e) : "plugins/WorldEdit/schematics/";
		String schem = schematic.getSingle(e);
		File schemFile = new File((f.endsWith("/")) ? f : f + "/" + new String(schem.endsWith(".schematic") ? schem : schem + ".schematic"));
		Integer l = null;
		try {
			l = MCEditSchematicFormat.getFormat(schemFile).load(schemFile).getLength();
		} catch (DataException | IOException e1) {
			return null;
		}
		return new Integer[] { l };
	}
}