package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprVanishState extends SimpleExpression<Boolean> {
	private Expression<Player> player;
	private Plugin vanishPlugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
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
	public String toString(@Nullable Event e, boolean debug) {
		return "vanish state of " + player.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		Player p = player.getSingle(e);
		return new Boolean[] { ((VanishPlugin) vanishPlugin).getManager().isVanished(p) };
	}
}