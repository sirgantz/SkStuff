package me.TheBukor.SkStuff.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.EntityInsentient;
import net.minecraft.server.v1_8_R2.MojangsonParseException;
import net.minecraft.server.v1_8_R2.MojangsonParser;
import net.minecraft.server.v1_8_R2.NBTBase;
import net.minecraft.server.v1_8_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.NBTTagDouble;
import net.minecraft.server.v1_8_R2.NBTTagEnd;
import net.minecraft.server.v1_8_R2.NBTTagFloat;
import net.minecraft.server.v1_8_R2.NBTTagInt;
import net.minecraft.server.v1_8_R2.NBTTagList;
import net.minecraft.server.v1_8_R2.NBTTagString;
import net.minecraft.server.v1_8_R2.PathfinderGoal;
import net.minecraft.server.v1_8_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R2.TileEntity;
import net.minecraft.server.v1_8_R2.World;

public class NMS_v1_8_R2 implements NMSInterface {

	@Override
	public void addToCompound(Object compound, Object toAdd) {
		if (compound instanceof NBTTagCompound && toAdd instanceof NBTTagCompound) {
			((NBTTagCompound) compound).a((NBTTagCompound) toAdd);
		}
	}
	
	@Override
	public void removeFromCompound(Object compound, String ... toRemove) {
		if (compound instanceof NBTTagCompound) {
			((NBTTagCompound) compound).remove(toRemove.toString()); //FIXME
		}
	}

	@Override
	public Object parseRawNBT(String rawNBT) {
		NBTTagCompound parsedNBT = null;
		try {
			parsedNBT = MojangsonParser.parse(rawNBT);
		} catch (MojangsonParseException ex) {
			Skript.warning("Error when parsing NBT - " + ex.getMessage());
		}
		return parsedNBT;
	}

	@Override
	public int getContentsId(Object nbtList) {
		if (nbtList instanceof NBTTagList) {
			return ((NBTTagList) nbtList).f();
		}
		return 0;
	}

	@Override
	public Object[] getContents(Object nbtList) {
		if (nbtList instanceof NBTTagList) {
			List<Object> contents = new ArrayList<Object>();
			for (int i = 0; i < ((NBTTagList) nbtList).size(); i++) {
				if (getIndex(nbtList, i) != null) {
					contents.add(getIndex(nbtList, i));
				}
			}
			return contents.toArray();
		}
		return null;
	}

	@Override
	public void addToList(Object nbtList, Object toAdd) {
		if (nbtList instanceof NBTTagList && toAdd instanceof NBTBase) {
			((NBTTagList) nbtList).add((NBTBase) toAdd);
		}
	}

	@Override
	public void removeFromList(Object nbtList, int index) {
		if (nbtList instanceof NBTTagList) {
			((NBTTagList) nbtList).a(index);
		}
	}

	@Override
	public void setIndex(Object nbtList, int index, Object toSet) {
		if (nbtList instanceof NBTTagList && toSet instanceof NBTBase) {
			((NBTTagList) nbtList).a(index, (NBTBase) toSet);
		}
	}

	@Override
	public Object getIndex(Object nbtList, int index) {
		if (nbtList instanceof NBTTagList) {
			NBTBase value = ((NBTTagList) nbtList).g(index);
			if (value instanceof NBTTagEnd)
				return null;
			else
				return value;
		}
		return null;
	}

	@Override
	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient) {
			((EntityInsentient) entity).setGoalTarget(null);
			if (isTargetSelector) {
				Iterator<?> goals = ((List<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).targetSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
			} else {
				Iterator<?> goals = ((List<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).goalSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
			}
		}
	}

	@Override
	public void addPathfinderGoal(Object entity, int priority, Object goal, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient && goal instanceof PathfinderGoal) {
			if (isTargetSelector)
				((EntityInsentient) entity).targetSelector.a(priority, (PathfinderGoal) goal);
			else
				((EntityInsentient) entity).goalSelector.a(priority, (PathfinderGoal) goal);
		}
	}

	@Override
	public void registerCompoundClassInfo() {
		Classes.registerClass(new ClassInfo<NBTTagCompound>(NBTTagCompound.class, "compound").user("((nbt)?( ?tag)?) ?compounds?").name("NBT Compound").changer(new Changer<NBTTagCompound>() {

			@SuppressWarnings("unchecked")
			@Override
			@Nullable
			public Class<?>[] acceptChange(ChangeMode mode) {
				if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET) {
					return CollectionUtils.array(String[].class, NBTTagCompound[].class);
				}
				return null;
			}

			@Override
			public void change(NBTTagCompound[] NBT, @Nullable Object[] delta, ChangeMode mode) {
				if (mode == ChangeMode.SET) {
					if (delta[0] instanceof NBTTagCompound) {
						NBT[0] = (NBTTagCompound) delta[0];
					} else {
						NBTTagCompound parsedNBT = (NBTTagCompound) parseRawNBT((String) delta[0]);
						NBT[0] = parsedNBT;
					}
				} else if (mode == ChangeMode.ADD) {
					if (delta[0] instanceof String) {
						NBTTagCompound parsedNBT = (NBTTagCompound) parseRawNBT((String) delta[0]);
						addToCompound(NBT[0], parsedNBT);
					} else {
						addToCompound(NBT[0], delta[0]);
					}
				} else if (mode == ChangeMode.REMOVE) {
					if (delta[0] instanceof NBTTagCompound)
						return;
					for (Object s : delta) {
						NBT[0].remove((String) s);
					}
				}
			}
		}).parser(new Parser<NBTTagCompound>() {

			@Override
			public String getVariableNamePattern() {
				return ".+";
			}

			@Override
			@Nullable
			public NBTTagCompound parse(String rawNBT, ParseContext context) {
				if (rawNBT.startsWith("nbt:{") && rawNBT.endsWith("}")) {
					rawNBT.substring(4);
					NBTTagCompound NBT = (NBTTagCompound) parseRawNBT(rawNBT);
					if (NBT.toString().equals("{}")) {
						return null;
					}
					return NBT;
				}
				return null;
			}

			@Override
			public String toString(NBTTagCompound compound, int arg1) {
				return compound.toString();
			}

			@Override
			public String toVariableNameString(NBTTagCompound compound) {
				return "nbt:" + compound.toString();
			}
		}));

	}

	@Override
	public void registerNBTListClassInfo() {
		Classes.registerClass(new ClassInfo<NBTTagList>(NBTTagList.class, "nbtlist").user("nbt ?list ?(tag)?").name("NBT List").changer(new Changer<NBTTagList>() {

			@SuppressWarnings("unchecked")
			@Override
			@Nullable
			public Class<?>[] acceptChange(ChangeMode mode) {
				if (mode == ChangeMode.ADD || mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
					return CollectionUtils.array(Float[].class, Double[].class, String[].class, NBTTagCompound[].class, Integer[].class, NBTTagList[].class);
				}
				return null;
			}

			@Override
			public void change(NBTTagList[] nbtList, @Nullable Object[] delta, ChangeMode mode) {
				int typeId = 0;
				if (delta instanceof Float[]) {
					typeId = 5;
				} else if (delta instanceof Double[]) {
					typeId = 6;
				} else if (delta instanceof String[]) {
					typeId = 8;
				} else if (delta instanceof NBTTagList[]) {
					typeId = 9;
				} else if (delta instanceof NBTTagCompound[]) {
					typeId = 10;
				} else if (delta instanceof Integer[]) {
					typeId = 11;
				} else {
					return;
				}
				if (mode == ChangeMode.SET) {
					if (typeId == 9)
						nbtList[0] = (NBTTagList) delta[0];
				} else if (mode == ChangeMode.ADD) {
					if (getContentsId(nbtList[0]) == typeId) {
						if (typeId == 5) {
							NBTTagFloat floatTag = new NBTTagFloat((float) delta[0]);
							addToList(nbtList[0], floatTag);
						} else if (typeId == 6) {
							NBTTagDouble doubleTag = new NBTTagDouble((double) delta[0]);
							addToList(nbtList[0], doubleTag);
						} else if (typeId == 8) {
							NBTTagString stringTag = new NBTTagString((String) delta [0]);
							addToList(nbtList[0], stringTag);
						} else if (typeId == 10) {
							addToList(nbtList[0], delta[0]);
						} else if (typeId == 11) {
							NBTTagInt intTag = new NBTTagInt((int) delta[0]);
							addToList(nbtList[0], intTag);
						}
					}
				} else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
					nbtList[0] = new NBTTagList();
				}
			}
		}).parser(new Parser<NBTTagList>() {

			@Override
			public String getVariableNamePattern() {
				return ".+";
			}

			@Override
			@Nullable
			public NBTTagList parse(String ignored, ParseContext context) {
				return null;
			}

			@Override
			public String toString(NBTTagList nbtList, int arg1) {
				return nbtList.toString();
			}

			@Override
			public String toVariableNameString(NBTTagList nbtList) {
				return nbtList.toString();
			}
		}));
	}

	@Override
	public Object getEntityNBT(Entity entity) {
		net.minecraft.server.v1_8_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		NBTTagCompound NBT = new NBTTagCompound();
		nmsEntity.e(NBT);
		return NBT;
	}

	@Override
	public Object getTileNBT(Block block) {
		NBTTagCompound NBT = new NBTTagCompound();
		World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
		TileEntity tileEntity = nmsWorld.getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		if (tileEntity == null)
			return null;
		tileEntity.b(NBT);
		return NBT;
	}

	@Override
	public Object getItemNBT(ItemStack itemStack) {
		if (itemStack.getType() == Material.AIR)
			return null;
		NBTTagCompound NBT = new NBTTagCompound();
		net.minecraft.server.v1_8_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		NBT = nmsItem.getTag();
		if (NBT == null || NBT.toString().equals("{}")) //Null or empty.
			return null;
		return new Object[] { NBT };
	}

	@Override
	public void setEntityNBT(Entity entity, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			net.minecraft.server.v1_8_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
			nmsEntity.f((NBTTagCompound) newCompound);
		}
	}

	@Override
	public void setTileNBT(Block block, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
			TileEntity tileEntity = nmsWorld.getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
			if (tileEntity == null)
				return;
			tileEntity.a((NBTTagCompound) newCompound);
			tileEntity.update();
			nmsWorld.notify(tileEntity.getPosition());
		}
	}

	@Override
	public ItemStack getItemWithNBT(ItemStack itemStack, Object compound) {
		if (compound instanceof NBTTagCompound) {
			if (itemStack.getType() == Material.AIR || itemStack == null)
				return null;
			if (compound == null || compound.toString().equals("{}"))
				return itemStack;
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
			nmsItem.setTag((NBTTagCompound) compound);
			ItemStack newItem = CraftItemStack.asCraftMirror(nmsItem);
			return newItem;
		}
		return null;
	}

	@Override
	public Object getFileNBT(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			return null; //File doesn't exist.
		}
		NBTTagCompound fileNBT = null;
		try {
			fileNBT = NBTCompressedStreamTools.a(fis);
			fis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return fileNBT;
	}

	@Override
	public void setFileNBT(File file, Object newCompound) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		try {
			NBTCompressedStreamTools.a((NBTTagCompound) newCompound, os);
			os.close();
		} catch (Exception ex) {
			if (ex instanceof EOFException) {
				; //Ignore, just end of the file
			} else {
				ex.printStackTrace();
			}
		} finally {
			try {
				os.close();
			} catch (Exception ex) {
				if (ex instanceof EOFException) {
					; //Ignore.
				} else {
					ex.printStackTrace();
				}
			}
		}
	}
}