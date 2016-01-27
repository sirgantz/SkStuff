package me.TheBukor.SkStuff.expressions;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprItemNBT extends SimpleExpression<ItemStack> {
	private Expression<ItemStack> itemStack;
	private Expression<String> string;

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound");
	private Class<?> nbtParseClass = ReflectionUtils.getNMSClass("MojangsonParser");
	private Class<?> nmsItemClass = ReflectionUtils.getNMSClass("ItemStack");

	private Class<?> craftItemClass = ReflectionUtils.getOBCClass("inventory.CraftItemStack");

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
		return itemStack.toString(e, false) + " with custom NBT " + string.toString(e, false);
	}

	@Override
	@Nullable
	public ItemStack[] get(Event e) {
		ItemStack item = itemStack.getSingle(e);
		String newTags = string.getSingle(e);
		if (item.getType() == Material.AIR || item == null) {
			return null;
		}
		Object nmsItem = null;
		try {
			nmsItem = craftItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(item, item);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Object NBT = null;
			NBT = nbtParseClass.getMethod("parse", String.class).invoke(NBT, newTags);
			if (NBT == null || NBT.toString().equals("{}")) { //"{}" is an empty compound.
				return new ItemStack[] { item }; //There's no NBT involved, so just give a normal item.
			}
			nmsItem.getClass().getMethod("setTag", nbtClass).invoke(nmsItem, NBT);
		} catch (Exception ex) {
			if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
				Bukkit.getConsoleSender().sendMessage("[SkStuff] " + ChatColor.RED + "Error when parsing NBT - " + ex.getCause().getMessage());
				return null;
			}
			ex.printStackTrace();
		}
		Object newItem = null;
		try {
			newItem = craftItemClass.getMethod("asCraftMirror", nmsItemClass).invoke(newItem, nmsItem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ItemStack[] { (ItemStack) newItem };
	}
}