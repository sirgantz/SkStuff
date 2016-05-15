package me.TheBukor.SkStuff.expressions;

import javax.annotation.Nullable;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprInventoryOwner extends SimpleExpression<Object> {
	private Expression<Inventory> inventory;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		inventory = (Expression<Inventory>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "owner of " + inventory.toString(e, debug);
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		if (inventory.getSingle(e) == null)
			return null;
		InventoryHolder holder = inventory.getSingle(e).getHolder();
		if (holder instanceof Entity) {
			return new Entity[] { (Entity) holder };
		} else if (holder instanceof BlockState) {
			return new Block[] { ((BlockState) holder).getBlock() };
		} else if (holder instanceof DoubleChest) {
			return new Block[] { ((DoubleChest) holder).getLocation().getBlock() };
		} else {
			Skript.error("Something went wrong when trying to get the owner of the specified inventory!");
			Skript.error("Post the below info on the SkStuff thread in SkUnity:");
			Skript.error("Class -> " + holder.getClass().getCanonicalName());
		}
		return null;
	}
}