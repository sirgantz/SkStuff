package me.TheBukor.SkStuff.expressions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprSelectionArea extends SimpleExpression<Integer> {
	private Expression<Player> player;
	private Integer parseMark;
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
		player = (Expression<Player>) expr[0];
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
	public String toString(@Nullable Event e, boolean debug) {
		return "the " + toStringMark + " of the WorldEdit selection of " + player.toString(e, debug);
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		Selection sel = we.getSelection(player.getSingle(e));
		if (sel == null)
			return null;
		Integer result = null;
		if (parseMark == 0) {
			result = sel.getArea();
		} else if (parseMark == 1) {
			result = sel.getWidth();
		} else if (parseMark == 2) {
			result = sel.getHeight();
		} else if (parseMark == 3) {
			result = sel.getLength();
		} else if (parseMark == 4) {
			result = (sel.getWidth() * sel.getLength());
		}
		return new Integer[] { result };
	}
}