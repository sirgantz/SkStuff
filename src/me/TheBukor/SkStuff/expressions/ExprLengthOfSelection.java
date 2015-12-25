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

public class ExprLengthOfSelection extends SimpleExpression<Integer> {
	private Expression<Player> player;

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
		player = (Expression<Player>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the length of the WorldEdit selection of " + player.toString(e, false);
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		Selection sel = we.getSelection(player.getSingle(e));
		if (sel == null)
			return null;
		return new Integer[] { sel.getLength() };
	}
}