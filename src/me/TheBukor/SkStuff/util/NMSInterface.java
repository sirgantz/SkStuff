package me.TheBukor.SkStuff.util;

import java.io.File;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface NMSInterface {

	public void addToCompound(Object compound, Object toAdd);

	public void removeFromCompound(Object compound, String ... toRemove);
	
	public Object parseRawNBT(String rawNBT);

	public Object[] getContents(Object nbtList);

	public void addToList(Object nbtList, Object toAdd);

	public void removeFromList(Object nbtList, int index);

	public void setIndex(Object nbtList, int index, Object toSet);

	public Object getIndex(Object nbtList, int index);

	public void clearPathfinderGoals(Entity entity);

	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector);

	public void addPathfinderGoal(Object entity, int priority, Object goal, boolean isTargetSelector);

	public void registerCompoundClassInfo();

	public void registerNBTListClassInfo();
	
	public Object getEntityNBT(Entity entity);
	
	public Object getTileNBT(Block block);

	public Object getItemNBT(ItemStack itemStack);

	public void setEntityNBT(Entity entity, Object newCompound);

	public void setTileNBT(Block block, Object newCompound);

	public ItemStack getItemWithNBT(ItemStack itemStack, Object compound);

	public Object getFileNBT(File file);

	public void setFileNBT(File file, Object newCompound);

	public Object convertToNBT(Number number);

	public Object convertToNBT(String string);

	public String getMCId(ItemStack itemStack);

	public ItemStack getItemFromMcId(String mcId);
}
