package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minecraft.server.v1_9_R1.EntityPlayer;

public class ExprGlideState extends SimpleExpression<Boolean> {
	private Expression<Player> player;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		player = (Expression<Player>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "elytra gliding state of " + player.toString(e, debug);
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		Player p = player.getSingle(e);
		EntityPlayer nmsPlayer = ((CraftPlayer) p).getHandle();
		return new Boolean[] { nmsPlayer.cB() };
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
		Player p = player.getSingle(e);
		EntityPlayer nmsPlayer = ((CraftPlayer) p).getHandle();
		if (mode == ChangeMode.SET) {
			boolean newValue = (boolean) delta[0];
			nmsPlayer.setFlag(7, newValue);
		}
	}
}
