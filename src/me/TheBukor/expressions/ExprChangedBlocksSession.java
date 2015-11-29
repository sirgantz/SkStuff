package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprChangedBlocksSession extends SimpleExpression<Integer> {
	private Expression<EditSession> editSession;
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
		return "the number of changed blocks in an edit session";
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		EditSession session = editSession.getSingle(e);
		if (session == null) return null;
		return new Integer[] { session.getBlockChangeCount() };
	}
}
