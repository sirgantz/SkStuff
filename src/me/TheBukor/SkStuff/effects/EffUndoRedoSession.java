package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.sk89q.worldedit.EditSession;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffUndoRedoSession extends Effect {
	private Expression<EditSession> editSession;
	private boolean redo = false;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		editSession = (Expression<EditSession>) expr[0];
		if (result.mark == 1) redo = true;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return (redo == true ? "redo " : "undo ") + "changes from edit session";
	}

	@Override
	protected void execute(Event e) {
		EditSession session = editSession.getSingle(e);
		if (session == null) return;
		if (redo == false) { 
			session.undo(session);
		} else {
			session.redo(session);
		}
		session.flushQueue();
	}
}