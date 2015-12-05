package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minecraft.server.v1_7_R2.MojangsonParser;
import net.minecraft.server.v1_7_R2.NBTTagCompound;

public class ExprItemNBTv1_7_R2 extends SimpleExpression<ItemStack> {
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
	public String toString(@Nullable Event e, boolean arg1) {
		return itemStack.toString(e, false) + " with custom NBT";
	}

	@Override
	@Nullable
	public ItemStack[] get(Event e) {
		ItemStack item = itemStack.getSingle(e);
		String newTags = string.getSingle(e);
		if (item.getType() == Material.AIR || item == null) {
			return null;
		}
		net.minecraft.server.v1_7_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound NBT = (NBTTagCompound) MojangsonParser.parse(newTags);
		if (NBT == null || NBT.isEmpty()) {
			return new ItemStack[] { item };
		}
		nmsItem.setTag(NBT);
		ItemStack newItem = CraftItemStack.asCraftMirror(nmsItem);
		return new ItemStack[] { newItem };
	}
}