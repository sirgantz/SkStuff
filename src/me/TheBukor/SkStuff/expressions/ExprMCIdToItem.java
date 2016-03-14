package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;

public class ExprMCIdToItem extends SimpleExpression<ItemStack> {
	private Expression<String> mcId;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		mcId = (Expression<String>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "item from minecraft id " + mcId.toString(e, debug);
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		String id = mcId.getSingle(e);
		return new ItemStack[] { SkStuff.getNMSMethods().getItemFromMcId(id) };
	}

}
