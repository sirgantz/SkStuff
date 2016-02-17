package me.TheBukor.SkStuff.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprEndermanBlocks extends SimpleExpression<ItemStack> {
	private Expression<Entity> entity;

	private Class<?> nmsBlockClass = ReflectionUtils.getNMSClass("Block", false);
	private Class<?> endermanClass = ReflectionUtils.getNMSClass("EntityEnderman", false);
	private Class<?> nmsIBlockData = ReflectionUtils.getNMSClass("IBlockData", false);
	private Class<?> nmsItemClass = ReflectionUtils.getNMSClass("ItemStack", false);
	private Class<?> craftEntClass = ReflectionUtils.getOBCClass("entity.CraftEntity");
	private Class<?> craftItemClass = ReflectionUtils.getOBCClass("inventory.CraftItemStack");

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		entity = (Expression<Entity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "blocks that " + entity.toString(e, debug) + " can carry";
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		Entity ent = entity.getSingle(e);
		if (ent == null || !(ent instanceof Enderman))
			return null;
		Object nmsEnt = null;
		try {
			nmsEnt = craftEntClass.getMethod("getHandle").invoke(ent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Set<Object> nmsBlocks = (Set<Object>) ReflectionUtils.getField("c", endermanClass, nmsEnt);
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (Object nmsBlock : nmsBlocks) {
			Object nmsBlockData;
			try {
				nmsBlockData = nmsBlockClass.getMethod("getBlockData").invoke(nmsBlock);
				int dataValue = (int) nmsBlockClass.getMethod("toLegacyData", nmsIBlockData).invoke(nmsBlock, nmsBlockData);
				Object nmsItem = nmsItemClass.getConstructor(nmsBlockClass, int.class, int.class).newInstance(nmsBlock, 1, dataValue);
				ItemStack bukkitItem = (ItemStack) craftItemClass.getMethod("asCraftMirror", nmsItemClass).invoke(null, nmsItem);
				items.add(bukkitItem);
			} catch (Exception ex) {
				ex.printStackTrace();;
			}
		}
		return (ItemStack[]) items.toArray();
	}

	@SuppressWarnings("unused")
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			ItemStack[] toSet = (ItemStack[]) delta;
			// TODO Convert bukkit items to NMS items (blocks). Then clear the list and add the delta items.
		} else if (mode == ChangeMode.REMOVE) {
			ItemStack[] toRemove = (ItemStack[]) delta;
			// TODO The code.
		} else if (mode == ChangeMode.ADD) {
			ItemStack[] toAdd = (ItemStack[]) delta;
			// TODO The code.
		} else if (mode == ChangeMode.RESET) {
			// TODO The code.
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.REMOVE || mode == ChangeMode.ADD || mode == ChangeMode.RESET) {
			return CollectionUtils.array(ItemStack[].class);
		}
		return null;
	}
}
