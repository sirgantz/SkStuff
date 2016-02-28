package me.TheBukor.SkStuff.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagEnd;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagLong;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.World;

public class NMS_v1_8_R3 implements NMSInterface {

	@Override
	public void addToCompound(Object compound, Object toAdd) {
		if (compound instanceof NBTTagCompound && toAdd instanceof NBTTagCompound) {
			((NBTTagCompound) compound).a((NBTTagCompound) toAdd);
		}
	}

	@Override
	public void removeFromCompound(Object compound, String ... toRemove) {
		if (compound instanceof NBTTagCompound) {
			for (String s : toRemove) {
				((NBTTagCompound) compound).remove(s);
			}
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
		if (nbtList instanceof NBTTagList) {
			if (toSet instanceof NBTBase) {
				((NBTTagList) nbtList).a(index, (NBTBase) toSet);
			} else {
				if (toSet instanceof Byte) {
					((NBTTagList) nbtList).a(index, new NBTTagByte((byte) toSet));
				} else if (toSet instanceof Short) {
					((NBTTagList) nbtList).a(index, new NBTTagShort((short) toSet));
				} else if (toSet instanceof Integer) {
					((NBTTagList) nbtList).a(index, new NBTTagInt((int) toSet));
				} else if (toSet instanceof Long) {
					((NBTTagList) nbtList).a(index, new NBTTagLong((long) toSet));
				} else if (toSet instanceof Float) {
					((NBTTagList) nbtList).a(index, new NBTTagFloat((float) toSet));
				} else if (toSet instanceof Double) {
					((NBTTagList) nbtList).a(index, new NBTTagDouble((double) toSet));
				} else if (toSet instanceof String) {
					((NBTTagList) nbtList).a(index, new NBTTagString((String) toSet));
				} //No need to check for NBTTagList and NBTTagCompound, these extend NBTBase.
			}
		}
	}

	@Override
	public Object getIndex(Object nbtList, int index) {
		if (nbtList instanceof NBTTagList) {
			NBTBase value = ((NBTTagList) nbtList).g(index);
			if (value instanceof NBTTagEnd) {
				return null;
			} else if (value instanceof NBTTagByte) {
				return ((NBTTagByte) value).f(); //Byte stored inside a NBTNumber
			} else if (value instanceof NBTTagShort) {
				return ((NBTTagShort) value).e(); //Short inside a NBTNumber
			} else if (value instanceof NBTTagInt) {
				return ((NBTTagInt) value).d(); //Integer inside a NBTNumber
			} else if (value instanceof NBTTagLong) {
				return ((NBTTagLong) value).c(); //Long inside a NBTNumber
			} else if (value instanceof NBTTagFloat) {
				return ((NBTTagFloat) value).h(); //Float inside a NBTNumber
			} else if (value instanceof NBTTagDouble) {
				return ((NBTTagDouble) value).g(); //Double inside a NBTNumber
			} else if (value instanceof NBTTagString) {
				return ((NBTTagString) value).a_(); //String inside the NBTTagString
			} else if (value instanceof NBTTagList || value instanceof NBTTagCompound) {
				return value; //No need to convert anything, these are registered by the addon.
			}
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
					Bukkit.broadcastMessage("rawNBT: \u00A72" + rawNBT.substring(4));
					NBTTagCompound NBT = (NBTTagCompound) parseRawNBT(rawNBT.substring(4));
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
		}).serializer(new Serializer<NBTTagCompound>() {

			@Override
			public Fields serialize(NBTTagCompound compound) throws NotSerializableException {
				Fields f = new Fields();
				f.putObject("asString", compound.toString());
				return f;
			}

			@Override
			public void deserialize(NBTTagCompound compound, Fields f) throws StreamCorruptedException, NotSerializableException {
				assert false;
			}

			@Override
			protected boolean canBeInstantiated() {
				return false;
			}

			@Override
			protected NBTTagCompound deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
				String s = fields.getObject("asString", String.class);
				NBTTagCompound compound = (NBTTagCompound) parseRawNBT(s);
				return compound;
			}

			@Override
			@Nullable
			public NBTTagCompound deserialize(String s) {
				NBTTagCompound compound = (NBTTagCompound) parseRawNBT(s);
				return compound;
			}

			@Override
			public boolean mustSyncDeserialization() {
				return true;
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
				if (delta instanceof Byte[]) {
					typeId = 1;
				} else if (delta instanceof Short[]) {
					typeId = 2;
				} else if (delta instanceof Integer[]) {
					typeId = 3;
				} else if (delta instanceof Long[]) {
					typeId = 4;
				} else if (delta instanceof Float[]) {
					typeId = 5;
				} else if (delta instanceof Double[]) {
					typeId = 6;
				} else if (delta instanceof String[]) {
					typeId = 8;
				} else if (delta instanceof NBTTagList[]) {
					typeId = 9;
				} else if (delta instanceof NBTTagCompound[]) {
					typeId = 10;
				} else {
					return;
				}
				if (mode == ChangeMode.SET) {
					if (typeId == 9)
						nbtList[0] = (NBTTagList) delta[0];
				} else if (mode == ChangeMode.ADD) {
					if (getContentsId(nbtList[0]) == typeId) {
						if (typeId == 1) {
							NBTTagByte byteTag = new NBTTagByte((byte) delta[0]);
							addToList(nbtList[0], byteTag);
						} else if (typeId == 2) {
							NBTTagShort shortTag = new NBTTagShort((short) delta[0]);
							addToList(nbtList[0], shortTag);
						} else if (typeId == 3) {
							NBTTagInt intTag = new NBTTagInt((int) delta[0]);
							addToList(nbtList[0], intTag);
						} else if (typeId == 4) {
							NBTTagLong longTag = new NBTTagLong((long) delta[0]);
							addToList(nbtList[0], longTag);
						} else if (typeId == 5) {
							NBTTagFloat floatTag = new NBTTagFloat((float) delta[0]);
							addToList(nbtList[0], floatTag);
						} else if (typeId == 6) {
							NBTTagDouble doubleTag = new NBTTagDouble((double) delta[0]);
							addToList(nbtList[0], doubleTag);
						} else if (typeId == 8) {
							NBTTagString stringTag = new NBTTagString((String) delta [0]);
							addToList(nbtList[0], stringTag);
						} else if (typeId == 9) {
							addToList(nbtList[0], delta[0]);
						} else if (typeId == 10) {
							addToList(nbtList[0], delta[0]);
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
			public NBTTagList parse(String listString, ParseContext context) {
				if (listString.startsWith("[") && listString.endsWith("]")) {
					NBTTagCompound NBT = (NBTTagCompound) parseRawNBT("{SkStuffIsCool:[0:" + listString.substring(1) + "}");
					NBTTagList parsedList = (NBTTagList) NBT.get("SkStuffIsCool");
					return parsedList;
				}
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
		}).serializer(new Serializer<NBTTagList>() {

			@Override
			public Fields serialize(NBTTagList nbtList) throws NotSerializableException {
				Fields f = new Fields();
				f.putObject("asString", nbtList.toString());
				return f;
			}

			@Override
			public void deserialize(NBTTagList nbtList, Fields f) throws StreamCorruptedException, NotSerializableException {
				assert false;
			}

			@Override
			protected boolean canBeInstantiated() {
				return false;
			}

			@Override
			protected NBTTagList deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
				String s = fields.getObject("asString", String.class);
				NBTTagCompound tempNBT = (NBTTagCompound) parseRawNBT("{SkStuffIsCool:" + s + "}");
				NBTTagList nbtList = (NBTTagList) tempNBT.get("SkStuffIsCool");
				return nbtList;
			}

			@Override
			@Nullable
			public NBTTagList deserialize(String s) {
				NBTTagCompound tempNBT = (NBTTagCompound) parseRawNBT("{SkStuffIsCool:" + s + "}");
				NBTTagList nbtList = (NBTTagList) tempNBT.get("SkStuffIsCool");
				return nbtList;
			}

			@Override
			public boolean mustSyncDeserialization() {
				return true;
			}
		}));
	}

	@Override
	public Object getEntityNBT(Entity entity) {
		net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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
		NBTTagCompound itemNBT = CraftItemStack.asNMSCopy(itemStack).getTag();
		Bukkit.broadcastMessage(String.valueOf(itemNBT));
		if (itemNBT.isEmpty())
			itemNBT = null;
		return itemNBT;
	}

	@Override
	public void setEntityNBT(Entity entity, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		if (compound instanceof NBTTagCompound && itemStack != null) {
			if (itemStack.getType() == Material.AIR)
				return null;
			if (((NBTTagCompound) compound).isEmpty())
				return itemStack;
			nmsItem.setTag((NBTTagCompound) compound);
			ItemStack newItem = CraftItemStack.asBukkitCopy(nmsItem);
			return newItem;
		} else if (compound == null) {
			nmsItem.setTag(null);
			ItemStack newItem = CraftItemStack.asBukkitCopy(nmsItem);
			return newItem;
		}
		return itemStack;
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
			if (ex instanceof EOFException) {
				; //Nothing.
			} else {
				ex.printStackTrace();
			}
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				if (ex instanceof EOFException) {
					; //Ignore it!
				} else {
					ex.printStackTrace();
				}
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
		} catch (IOException ex) {
			if (ex instanceof EOFException) {
				; //Ignore, just end of the file
			} else {
				ex.printStackTrace();
			}
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				if (ex instanceof EOFException) {
					; //Ignore.
				} else {
					ex.printStackTrace();
				}
			}
		}
	}
}