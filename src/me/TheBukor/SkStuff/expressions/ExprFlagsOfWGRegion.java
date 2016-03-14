package me.TheBukor.SkStuff.expressions;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.event.Event;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@SuppressWarnings("rawtypes")
public class ExprFlagsOfWGRegion extends SimpleExpression<Flag> {
	private Expression<ProtectedRegion> region;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] expr, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		region = (Expression<ProtectedRegion>) expr[0];
		return true;
	}

	@Override
	protected Flag[] get(final Event e) {
		ProtectedRegion region = this.region.getSingle(e);
		if (region != null) {
			return region.getFlags().keySet().toArray(new Flag[region.getFlags().size()]);
		}
		return null;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Flag> getReturnType() {
		return Flag.class;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "all worldguard flags of " + region.toString(e, debug);
	}

	@Override
	public void change(Event e, Object[] delta, ChangeMode mode) {
		ProtectedRegion region = this.region.getSingle(e);
		if (region == null)
			return;
		if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			region.getFlags().clear();
		}
	}

	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			return new Class[0];
		}
		return null;
	}
}