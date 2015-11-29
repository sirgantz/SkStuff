package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprEditSessionLimit extends SimpleExpression<Integer> {
	private Expression<EditSession> editSession;
	private WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");


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
		editSession = (Expression<EditSession>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the block change limit of an edit session";
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		EditSession session = editSession.getSingle(e);
		if (session == null) return null;
		return new Integer[] { session.getBlockChangeLimit() };
	}
	
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		EditSession session = editSession.getSingle(e);
		if (session == null) return;
		if (mode == ChangeMode.SET) {
			Integer newLimit = (Integer) delta[0];
			session.setBlockChangeLimit(Integer.valueOf(newLimit));
		} else if (mode == ChangeMode.RESET) {
			session.setBlockChangeLimit(Integer.valueOf(we.getLocalConfiguration().defaultChangeLimit));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			return CollectionUtils.array(Integer.class);
		}
		return null;
	}
}
