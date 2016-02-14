package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffToggleVanish extends Effect {
	private Expression<Player> player;
	private Plugin vanishPlugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
	private boolean silent = false;
	private String toStringMark = "";

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		player = (Expression<Player>) expr[0];
		if (result.mark == 1) {
			toStringMark = " silently";
			silent = true;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "toggle vanished state of " + player.toString(e, debug) + toStringMark;
	}

	@Override
	protected void execute(Event e) {
		Player p = player.getSingle(e);
		if (silent) {
			((VanishPlugin) vanishPlugin).getManager().toggleVanishQuiet(p, false);
		} else {
			((VanishPlugin) vanishPlugin).getManager().toggleVanish(p);
		}
	}
}