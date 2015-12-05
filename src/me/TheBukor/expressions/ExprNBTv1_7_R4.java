package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minecraft.server.v1_7_R4.MojangsonParser;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.TileEntity;

public class ExprNBTv1_7_R4 extends SimpleExpression<NBTTagCompound> {
	private Expression<?> target;
	private NBTTagCompound[] returned;

	@Override
	public Class<? extends NBTTagCompound> getReturnType() {
		return NBTTagCompound.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		target = expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the NBT of " + target.toString(e, false);
	}

	@Override
	@Nullable
	public NBTTagCompound[] get(Event e) {
		Object tar = target.getSingle(e);
		if (tar instanceof Entity) {
			CraftEntity ent = (CraftEntity) tar;
			NBTTagCompound NBT = new NBTTagCompound();
			ent.getHandle().e(NBT);
			returned = new NBTTagCompound[] { NBT };

		} else if (tar instanceof Block) {
			Block block = (Block) tar;
			NBTTagCompound NBT = new NBTTagCompound();
			TileEntity tileEntity = ((CraftWorld) block.getWorld()).getHandle().getTileEntity(block.getX(), block.getY(), block.getZ());
			if (tileEntity == null) {
				return null;
			}
			tileEntity.b(NBT);
			returned = new NBTTagCompound[] { NBT };

		} else if (tar instanceof ItemStack) {
			ItemStack item = (ItemStack) tar;
			if (item.getType() == Material.AIR) {
				return null;
			}
			NBTTagCompound NBT = CraftItemStack.asNMSCopy(item).getTag();
			if (NBT == null || NBT.isEmpty()) {
				return null;
			}
			returned = new NBTTagCompound[] { NBT };
		}
		return returned;
	}

	@Override
	public void change(Event e, Object[] delta, ChangeMode mode) {
		Object tar = target.getSingle(e);
		if (tar instanceof Entity) {
			CraftEntity ent = (CraftEntity) tar;
			NBTTagCompound NBT = new NBTTagCompound();
			ent.getHandle().e(NBT);
			if (mode == ChangeMode.ADD) {
				String newTags = (String) (delta[0]);
				NBTTagCompound NBT1 = (NBTTagCompound) MojangsonParser.parse(newTags);
				NBT1.remove("UUIDMost");
				NBT1.remove("UUIDLeast");
				NBT1.remove("WorldUUIDLeast");
				NBT1.remove("WorldUUIDMost");
				NBT1.remove("Bukkit.updateLevel");
				NBT.set("", NBT1);
				ent.getHandle().f(NBT);
			} else if (mode == ChangeMode.REMOVE) {
				ent.getHandle().e(NBT);
				for (Object s : delta) {
					if (s != "UUIDMost" || s != "UUIDLeast" || s != "WorldUUIDMost" || s != "WorldUUIDLeast"
							|| s != "Bukkit.updateLevel") {
						NBT.remove((String) s);
						ent.getHandle().f(NBT);
					}
				}
			}
		} else if (tar instanceof Block) {
			Block block = (Block) tar;
			NBTTagCompound NBT = new NBTTagCompound();
			TileEntity tileEntity = ((CraftWorld) block.getWorld()).getHandle().getTileEntity(block.getX(), block.getY(), block.getZ());
			if (tileEntity == null) {
				return;
			}
			if (mode == ChangeMode.ADD) {
				String newTags = (String) (delta[0]);
				tileEntity.b(NBT);
				NBTTagCompound NBT1 = (NBTTagCompound) MojangsonParser.parse(newTags);
				NBT.set("", NBT1);;
				NBT.setInt("x", block.getX());
				NBT.setInt("y", block.getY());
				NBT.setInt("z", block.getZ());
				tileEntity.a(NBT);
				tileEntity.update();
				((CraftWorld) block.getWorld()).getHandle().notify(tileEntity.x, tileEntity.y, tileEntity.z);;
			} else if (mode == ChangeMode.REMOVE) {
				tileEntity.b(NBT);
				for (Object s : delta) {
					if (s != "x" || s != "y" || s != "z" || s != "id") {
						NBT.remove((String) s);
						tileEntity.a(NBT);
						tileEntity.update();
						((CraftWorld) block.getWorld()).getHandle().notify(tileEntity.x, tileEntity.y, tileEntity.z);
					}
				}
			}
		} else if (tar instanceof ItemStack) {
			ItemStack item = (ItemStack) tar;
			if (item.getType() == Material.AIR) {
				return;
			}
			net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound NBT = nmsItem.getTag();
			if (NBT == null) {
				NBT = new NBTTagCompound();
			}
			if (mode == ChangeMode.ADD) {
				String newTags = (String) (delta[0]);
				NBTTagCompound NBT1 = (NBTTagCompound) MojangsonParser.parse(newTags);
				NBT.set("", NBT1);
				nmsItem.setTag(NBT);
				ItemStack newItem = CraftItemStack.asCraftMirror(nmsItem);
				Object[] slot = target.getSource().getAll(e);
				if (!(slot[0] instanceof Slot)) {
					return;
				}
				((Slot) slot[0]).setItem(newItem);
			} else if (mode == ChangeMode.REMOVE) {
				NBT = nmsItem.getTag();
				if (NBT == null || NBT.isEmpty()) {
					return;
				}
				for (Object s : delta) {
					NBT.remove((String) s);
				}
				nmsItem.setTag(NBT);
				ItemStack newItem = CraftItemStack.asCraftMirror(nmsItem);
				Object[] slot = target.getSource().getAll(e);
				((Slot) slot[0]).setItem(newItem);
			} else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
				nmsItem.setTag(new NBTTagCompound());
				ItemStack newItem = CraftItemStack.asCraftMirror(nmsItem);
				Object[] slot = target.getSource().getAll(e);
				if (!(slot[0] instanceof Slot)) {
					return;
				}
				((Slot) slot[0]).setItem(newItem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.DELETE
				|| mode == ChangeMode.RESET) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}
}