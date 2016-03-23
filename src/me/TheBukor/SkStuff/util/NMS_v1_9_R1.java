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
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.Item;
import net.minecraft.server.v1_9_R1.MinecraftKey;
import net.minecraft.server.v1_9_R1.MojangsonParseException;
import net.minecraft.server.v1_9_R1.MojangsonParser;
import net.minecraft.server.v1_9_R1.NBTBase;
import net.minecraft.server.v1_9_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_9_R1.NBTTagByte;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagDouble;
import net.minecraft.server.v1_9_R1.NBTTagFloat;
import net.minecraft.server.v1_9_R1.NBTTagInt;
import net.minecraft.server.v1_9_R1.NBTTagList;
import net.minecraft.server.v1_9_R1.NBTTagLong;
import net.minecraft.server.v1_9_R1.NBTTagShort;
import net.minecraft.server.v1_9_R1.NBTTagString;
import net.minecraft.server.v1_9_R1.PacketPlayInChat;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.TileEntity;
import net.minecraft.server.v1_9_R1.World;

public class NMS_v1_9_R1 implements NMSInterface {

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
	public NBTTagCompound parseRawNBT(String rawNBT) {
		NBTTagCompound parsedNBT = null;
		try {
			parsedNBT = MojangsonParser.parse(rawNBT);
		} catch (MojangsonParseException ex) {
			Skript.warning("Error when parsing NBT - " + ex.getMessage());
			return null;
		}
		return parsedNBT;
	}

	@Override
	public Object[] getContents(Object nbtList) {
		if (nbtList instanceof NBTTagList) {
			Object[] contents = new Object[((NBTTagList) nbtList).size()];
			for (int i = 0; i < ((NBTTagList) nbtList).size(); i++) {
				if (getIndex(nbtList, i) != null) {
					contents[i] = getIndex(nbtList, i);
				}
			}
			return contents;
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
		if (nbtList instanceof NBTTagList && index >= 0 && index < ((NBTTagList) nbtList).size()) {
			((NBTTagList) nbtList).remove(index);
		}
	}

	@Override
	public void setIndex(Object nbtList, int index, Object toSet) {
		if (nbtList instanceof NBTTagList && index >= 0 && index < ((NBTTagList) nbtList).size()) {
			if (toSet instanceof NBTBase) {
				((NBTTagList) nbtList).a(index, (NBTBase) toSet);
			} else if (toSet instanceof Number) {
				((NBTTagList) nbtList).a(index, convertToNBT((Number) toSet));
			} else if (toSet instanceof String) {
				((NBTTagList) nbtList).a(index, convertToNBT((String) toSet));
			}
		}
	}

	@Override
	public Object getIndex(Object nbtList, int index) {
		if (nbtList instanceof NBTTagList && index >= 0 && index < ((NBTTagList) nbtList).size()) {
			NBTBase value = ((NBTTagList) nbtList).h(index);
			if (value instanceof NBTTagByte) {
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
			} else if (value instanceof NBTBase) {
				return value; //No need to convert this
			}
		}
		return null;
	}

	@Override
	public void clearPathfinderGoals(Entity entity) {
		EntityInsentient nmsEnt = (EntityInsentient) ((CraftEntity) entity).getHandle();
		((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, nmsEnt.goalSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("c", PathfinderGoalSelector.class, nmsEnt.goalSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, nmsEnt.targetSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("c", PathfinderGoalSelector.class, nmsEnt.targetSelector)).clear();
	}

	@Override
	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient) {
			((EntityInsentient) entity).setGoalTarget(null);
			if (isTargetSelector) {
				Iterator<?> goals = ((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).targetSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
			} else {
				Iterator<?> goals = ((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).goalSelector)).iterator();
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
				if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
					return CollectionUtils.array(String.class, NBTTagCompound.class);
				}
				return null;
			}

			@Override
			public void change(NBTTagCompound[] NBT, @Nullable Object[] delta, ChangeMode mode) {
				if (mode == ChangeMode.ADD) {
					if (delta[0] instanceof String) {
						NBTTagCompound parsedNBT = parseRawNBT((String) delta[0]);
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
					NBTTagCompound NBT = parseRawNBT(rawNBT.substring(4));
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
				return compound.toString();
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
				NBTTagCompound compound =  parseRawNBT(s);
				return compound;
			}

			@Override
			@Nullable
			public NBTTagCompound deserialize(String s) {
				NBTTagCompound compound =  parseRawNBT(s);
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
				if (mode == ChangeMode.ADD) {
					return CollectionUtils.array(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, String.class, NBTTagCompound.class, NBTTagList.class);
				}
				return null;
			}

			@Override
			public void change(NBTTagList[] nbtList, @Nullable Object[] delta, ChangeMode mode) {
				if (delta.length == 0)
					return;
				if (delta[0] instanceof Number) {
					addToList(nbtList[0], convertToNBT((Number) delta[0]));
				} else if (delta[0] instanceof String) {
					addToList(nbtList[0], convertToNBT((String) delta[0]));
				} else if (delta[0] instanceof NBTBase) {
					addToList(nbtList[0], delta[0]);
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
					NBTTagCompound tempNBT =  parseRawNBT("{SkStuffIsCool:[0:" + listString.substring(1) + "}");
					NBTTagList parsedList = (NBTTagList) tempNBT.get("SkStuffIsCool");
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
				NBTTagCompound tempNBT =  parseRawNBT("{SkStuffIsCool:" + s + "}");
				NBTTagList nbtList = (NBTTagList) tempNBT.get("SkStuffIsCool");
				return nbtList;
			}

			@Override
			@Nullable
			public NBTTagList deserialize(String s) {
				NBTTagCompound tempNBT =  parseRawNBT("{SkStuffIsCool:" + s + "}");
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
	public NBTTagCompound getEntityNBT(Entity entity) {
		net.minecraft.server.v1_9_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		NBTTagCompound NBT = new NBTTagCompound();
		nmsEntity.e(NBT);
		return NBT;
	}

	@Override
	public NBTTagCompound getTileNBT(Block block) {
		NBTTagCompound NBT = new NBTTagCompound();
		World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
		TileEntity tileEntity = nmsWorld.getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		if (tileEntity == null)
			return null;
		tileEntity.save(NBT);
		return NBT;
	}

	@Override
	public NBTTagCompound getItemNBT(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR)
			return null;
		NBTTagCompound itemNBT = CraftItemStack.asNMSCopy(itemStack).getTag();
		if (itemNBT == null || itemNBT.isEmpty())
			itemNBT = null;
		return itemNBT;
	}

	@Override
	public void setEntityNBT(Entity entity, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			net.minecraft.server.v1_9_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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
			IBlockData tileEntType = nmsWorld.getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
			nmsWorld.notify(tileEntity.getPosition(), tileEntType, tileEntType, 3);
		}
	}

	@Override
	public ItemStack getItemWithNBT(ItemStack itemStack, Object compound) {
		net.minecraft.server.v1_9_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
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
	public NBTTagCompound getFileNBT(File file) {
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
		}
		return fileNBT;
	}

	@Override
	public void setFileNBT(File file, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
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
			}
		}
	}

	@Override
	public NBTBase convertToNBT(Number number) {
		if (number instanceof Byte) {
			return new NBTTagByte((byte) number);
		} else if (number instanceof Short) {
			return new NBTTagShort((short) number);
		} else if (number instanceof Integer) {
			return new NBTTagInt((int) number);
		} else if (number instanceof Long) {
			return new NBTTagLong((long) number);
		} else if (number instanceof Float) {
			return new NBTTagFloat((float) number);
		} else if (number instanceof Double) {
			return new NBTTagDouble((double) number);
		}
		return null;
	}

	@Override
	public NBTTagString convertToNBT(String string) {
		return new NBTTagString(string);
	}

	@Override
	public String getMCId(ItemStack itemStack) {
		net.minecraft.server.v1_9_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		MinecraftKey mcKey = Item.REGISTRY.b(nmsItem.getItem());
		return mcKey.toString();
	}

	@Override
	public ItemStack getItemFromMcId(String mcId) {
		MinecraftKey mcKey = new MinecraftKey(mcId);
		Item nmsItem = (Item) Item.REGISTRY.get(mcKey);
		return CraftItemStack.asNewCraftStack(nmsItem);
	}

	@Override
	public void makeClientSay(String msg, Player p) {
		PacketPlayInChat chatPacket = new PacketPlayInChat(msg);
		((CraftPlayer) p).getHandle().playerConnection.a(chatPacket);
	}
}