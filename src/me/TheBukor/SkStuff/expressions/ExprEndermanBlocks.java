package me.TheBukor.SkStuff.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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

	private Class<?> nmsBlockClass = ReflectionUtils.getNMSClass("Block");
	private Class<?> endermanClass = ReflectionUtils.getNMSClass("EntityEnderman");
	private Class<?> nmsIBlockData = ReflectionUtils.getNMSClass("IBlockData");
	private Class<?> nmsItemClass = ReflectionUtils.getNMSClass("ItemStack");
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
			nmsEnt = craftEntClass.cast(ent).getClass().getMethod("getHandle").invoke(ent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Set<Object> nmsBlocks = (Set<Object>) ReflectionUtils.getField("c", endermanClass, nmsEnt);
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (Object nmsBlock : nmsBlocks) {
			Object nmsBlockData;
			try {
				nmsBlockData = nmsBlockClass.getMethod("getBlockData").invoke(nmsBlock);
				int dataValue = (int) nmsBlockClass.getMethod("toLegacyData", nmsIBlockData).invoke(nmsBlock,
						nmsBlockData);
				Object nmsItem = nmsItemClass.getConstructor(nmsBlockClass, int.class, int.class).newInstance(nmsBlock,
						1, dataValue);
				ItemStack bukkitItem = (ItemStack) craftItemClass.getMethod("asCraftMirror", nmsItemClass).invoke(null,
						nmsItem);
				items.add(bukkitItem);
			} catch (Exception ex) {
				ex.printStackTrace();
				;
			}
		}
		return Arrays.copyOf(items.toArray(), items.size(), ItemStack[].class);
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Entity ent = entity.getSingle(e);
		if (ent == null || !(ent instanceof Enderman))
			return;
		Object nmsEnt = null;
		try {
			nmsEnt = craftEntClass.cast(ent).getClass().getMethod("getHandle").invoke(ent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Set<Object> enderBlocks = (Set<Object>) ReflectionUtils.getField("c", endermanClass, nmsEnt);
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET) {
			ItemStack[] deltaItems = Arrays.copyOf(delta, delta.length, ItemStack[].class);
			if (mode == ChangeMode.SET) {
				enderBlocks.clear();
			}
			for (ItemStack itemStack : deltaItems) {
				if (itemStack.getType() == Material.AIR || itemStack == null)
					continue;
				Object nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
				Object nmsItem = null;
				try {
					nmsItem = nmsItemStack.getClass().getMethod("getItem").invoke(nmsItemStack);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (mode == ChangeMode.ADD || mode == ChangeMode.SET)
					enderBlocks.add(nmsItem);
				else //ChangeMode.REMOVE
					enderBlocks.remove(nmsItem);
			}
		} else if (mode == ChangeMode.RESET) {
			ItemStack grass = new ItemStack(Material.GRASS);
			ItemStack dirt = new ItemStack(Material.DIRT);
			ItemStack sand = new ItemStack(Material.SAND);
			ItemStack gravel = new ItemStack(Material.GRAVEL);
			ItemStack dandelion = new ItemStack(Material.YELLOW_FLOWER);
			ItemStack poppy = new ItemStack(Material.RED_ROSE);
			ItemStack brownShroom = new ItemStack(Material.BROWN_MUSHROOM);
			ItemStack redShroom = new ItemStack(Material.RED_MUSHROOM);
			ItemStack tnt = new ItemStack(Material.TNT);
			ItemStack cactus = new ItemStack(Material.CACTUS);
			ItemStack clay = new ItemStack(Material.CLAY);
			ItemStack pumpkin = new ItemStack(Material.PUMPKIN);
			ItemStack melon = new ItemStack(Material.MELON_BLOCK);
			ItemStack mycellium = new ItemStack(Material.MYCEL);
			ItemStack[] defaultItems = new ItemStack[] { grass, dirt, gravel, dandelion, poppy, brownShroom, redShroom, tnt, cactus, clay, pumpkin, melon, mycellium };
			enderBlocks.clear();
			for (ItemStack itemStack : defaultItems) {
				Object nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
				Object nmsItem = null;
				try {
					nmsItem = nmsItemStack.getClass().getMethod("getItem").invoke(nmsItemStack);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				enderBlocks.add(nmsItem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.RESET|| mode == ChangeMode.SET) {
			return CollectionUtils.array(ItemStack[].class);
		}
		return null;
	}
}
