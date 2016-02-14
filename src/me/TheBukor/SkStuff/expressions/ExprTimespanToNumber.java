package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

public class ExprTimespanToNumber extends SimpleExpression<Number> {
	private Expression<Timespan> time;
	private String toStringMark;
	private int mark;

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		time = (Expression<Timespan>) expr[0];
		mark = result.mark;
		if (result.mark == 0) {
			toStringMark = "ticks";
		} else if (result.mark == 1) {
			toStringMark = "seconds";
		} else if (result.mark == 2) {
			toStringMark = "minutes";
		} else if (result.mark == 3) {
			toStringMark = "hours";
		} else if (result.mark == 4) {
			toStringMark = "days";
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return time.toString(e, debug) + "converted to " + toStringMark;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	protected Number[] get(Event e) {
		Timespan t = time.getSingle(e);
		Number ticks = null;
		if (Skript.methodExists(Timespan.class, "getTicks_i")) { //Compatibility with Mirreducki's Skript patch 24+ days timespans.
			ticks = t.getTicks_i();
		} else { //Standard Skript timespans, limited to roughly 24 days.
			ticks = t.getTicks();
		}
		if (mark == 0) {
			return new Number[] { ticks };
		} else if (mark == 1) {
			return new Number[] { ticks.longValue() / 20 };
		} else if (mark == 2) {
			return new Number[] { ticks.longValue() / 20 / 60 };
		} else if (mark == 3) {
			return new Number[] { ticks.longValue() / 20 / 60 / 60 };
		} else if (mark == 4) {
			return new Number[] { ticks.longValue() / 20 / 60 / 60 / 24 }; 
		}
		return null;
	}
}