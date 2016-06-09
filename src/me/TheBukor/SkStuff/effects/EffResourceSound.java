package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffResourceSound extends Effect {
	private Expression<String> sound;
	private Expression<Player> players;
	private Expression<Location> location;
	private Expression<Number> volume;
	private Expression<Number> pitch;
	
	private int pattern;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult result) {
		sound = (Expression<String>) expr[0];
		players = (Expression<Player>) expr[1];
		location = (Expression<Location>) expr[2];
		pattern = matchedPattern;
		if (pattern == 0) {
			volume = (Expression<Number>) expr[3];
			pitch = (Expression<Number>) expr[4];
		} else {
			pitch = (Expression<Number>) expr[3];
			volume = (Expression<Number>) expr[4];
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "play raw sound for " + players.toString(e, debug) + " at " + location.toString(e, debug) + " with " + (pattern == 0 ? "volume " + volume.toString(e, debug) : "pitch " + pitch.toString(e, debug)) + " and " + (pattern == 0 ? "pitch " + pitch.toString(e, debug) : "volume " + volume.toString(e, debug));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		String s = sound.getSingle(e);
		Player[] ps = players.getAll(e);
		Location loc = location.getSingle(e);
		float vol = (volume == null ? 1.0F : volume.getSingle(e).floatValue());
		float pitch = (this.pitch == null ? 1.0F : this.pitch.getSingle(e).floatValue());
		for (Player p : ps) {
			p.playSound(loc, s, vol, pitch);
		}
	}
}