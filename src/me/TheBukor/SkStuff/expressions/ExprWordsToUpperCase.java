package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.apache.commons.lang.WordUtils;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprWordsToUpperCase extends SimpleExpression<String> {
	private Expression<String> text;
	private Boolean fullyCapitalize = false;
	private String toStringEnd;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		text = (Expression<String>) expr[0];
		if (result.mark == 0) {
			toStringEnd = " to uppercase";
		} else {
			fullyCapitalize = true;
			toStringEnd = " to uppercase ignoring other uppercase characters";
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "convert first character of each word in " + text.toString(e, false) + toStringEnd;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		String s = text.getSingle(e);
		if (fullyCapitalize) {
			return new String[] { WordUtils.capitalizeFully(s) };
		} else {
			return new String[] { WordUtils.capitalize(s) };
		}
	}
}