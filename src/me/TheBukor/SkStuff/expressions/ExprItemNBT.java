package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;

public class ExprItemNBT extends SimpleExpression<ItemStack> {
	private Expression<ItemStack> itemStack;
	private Expression<String> string;


	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		itemStack = (Expression<ItemStack>) expr[0];
		string = (Expression<String>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return itemStack.toString(e, debug) + " with custom NBT " + string.toString(e, debug);
	}

	@Override
	@Nullable
	public ItemStack[] get(Event e) {
		ItemStack item = itemStack.getSingle(e);
		String newTags = string.getSingle(e);
		if (item.getType() == Material.AIR || item == null) {
			return null;
		}
		Object parsedNBT = SkStuff.getNMSMethods().parseRawNBT(newTags);
		ItemStack newItem = SkStuff.getNMSMethods().getItemWithNBT(item, parsedNBT);
		return new ItemStack[] { newItem };
	}
}