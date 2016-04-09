package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprSuperPickaxe extends SimpleExpression<Boolean> {
	private Expression<Player> players;

	@Override
	public boolean isSingle() {
		return players.isSingle();
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		players = (Expression<Player>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "world edit super pickaxe state of " + players.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		Player[] ps = players.getAll(e);
		Boolean[] states = new Boolean[ps.length];
		int i = 0;
		for (Player p : ps) {
			states[i] = we.getSession(p).hasSuperPickAxe();
			i++;
		}
		return states;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Boolean.class);
		}
		return null;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
			Player[] ps = players.getAll(e);
			boolean enablePick = (boolean) delta[0];
			for (Player p : ps) {
				if (enablePick) {
					we.getSession(p).enableSuperPickAxe();
				} else {
					we.getSession(p).disableSuperPickAxe();
				}
			}
		}
	}
}