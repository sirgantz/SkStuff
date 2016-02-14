package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffRememberChanges extends Effect {
	private Expression<Player> player;
	private Expression<EditSession> editSession;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) expr[0];
		editSession = (Expression<EditSession>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "make " + player.toString(e, debug) + " remember changes from edit session";
	}

	@Override
	protected void execute(Event e) {
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		Player p = player.getSingle(e);
		EditSession session = editSession.getSingle(e);
		if (we.getSession(p) == null) return;
		we.getSession(p).remember(session);
	}
}