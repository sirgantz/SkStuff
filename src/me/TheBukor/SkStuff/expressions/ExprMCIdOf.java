package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;

public class ExprMCIdOf extends SimpleExpression<String> {
	private Expression<ItemType> itemType;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		itemType = (Expression<ItemType>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "minecraft id of " + itemType.toString(e, debug);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	protected String[] get(Event e) {
		ItemType type = itemType.getSingle(e);
		ItemStack item = new ItemStack(type.getTypes().get(0).getId());
		return new String[] { SkStuff.getNMSMethods().getMCId(item) };
	}

}
