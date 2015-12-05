package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprNewEditSession extends SimpleExpression<EditSession> {
	private Expression<World> world;
	private Expression<Integer> blockLimit;

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
		world = (Expression<World>) expr[0];
		blockLimit = (Expression<Integer>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "new edit session in world " + world.toString(e, false) + " with maximum block change limit of " + blockLimit.toString(e, false);
	}

	@Override
	@Nullable
	protected EditSession[] get(Event e) {
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		World w = world.getSingle(e);
		Integer limit = blockLimit.getSingle(e);
		com.sk89q.worldedit.world.World weWorld = BukkitUtil.getLocalWorld(w);
		return new EditSession[] { we.getWorldEdit().getEditSessionFactory().getEditSession(weWorld, limit) };
	}
}