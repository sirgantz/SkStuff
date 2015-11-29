package me.TheBukor.expressions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprEditSession extends SimpleExpression<EditSession> {
	private Expression<Player> player;

	@Override
	public Class<? extends EditSession> getReturnType() {
		return EditSession.class;
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
		return "the edit session of " + player.toString(e, false);
	}

	@Override
	@Nullable
	protected EditSession[] get(Event e) {
		Player p = player.getSingle(e);
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (we.getWorldEdit().getEditSessionFactory().getEditSession((World) we.wrapPlayer(p).getWorld(), we.getSession(p).getBlockChangeLimit(), we.wrapPlayer(p)) == null) {
			return new EditSession[] { we.createEditSession(p) };
		}
		return new EditSession[] { we.getWorldEdit().getEditSessionFactory().getEditSession((World) we.wrapPlayer(p).getWorld(), we.getSession(p).getBlockChangeLimit(), we.wrapPlayer(p)) };
	}

}
