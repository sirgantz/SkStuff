package me.TheBukor.SkStuff.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;

public class CondSelectionContains extends Condition {
	private Expression<Player> player;
	private Expression<Location> loc;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) expr[0];
		loc = (Expression<Location>) expr[1];
		setNegated(matchedPattern == 2 || matchedPattern == 3);
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "if WorldEdit selection of " + player.toString(e, false) + (isNegated() ? " doesn't contain " : " contains ") + loc.toString(e, false);
	}

	@Override
	public boolean check(final Event e) {
		final WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (we.getSelection(player.getSingle(e)) == null) {
			return false;
		}
		return player.check(e, new Checker<Player>() {
			@Override
			public boolean check(Player p) {
				return we.getSelection(p).contains(loc.getSingle(e));
			}
		}, isNegated());
	}
}
