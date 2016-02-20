package me.TheBukor.SkStuff.expressions;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.fusesource.jansi.Ansi;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.util.NBTUtil;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprNBTOf extends SimpleExpression<Object> {
	private Expression<Object> target;

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound", false);
	private Class<?> nbtParserClass = ReflectionUtils.getNMSClass("MojangsonParser", false);
	private Class<?> nmsPosClass = ReflectionUtils.getNMSClass("BlockPosition", false);
	private Class<?> nmsItemClass = ReflectionUtils.getNMSClass("ItemStack", false);

	private Class<?> craftEntClass = ReflectionUtils.getOBCClass("entity.CraftEntity");
	private Class<?> craftItemClass = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
	private Class<?> craftWorldClass = ReflectionUtils.getOBCClass("CraftWorld");

	@Override
	public Class<? extends Object> getReturnType() {
		return nbtClass;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		target = (Expression<Object>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the NBT of " + target.toString(e, debug);
	}

	@Override
	@Nullable
	public Object[] get(Event e) {
		Object tar = target.getSingle(e);
		if (tar instanceof Entity) {
			Object NBT = null;
			try {
				Object nmsEnt = craftEntClass.getMethod("getHandle").invoke(tar);
				NBT = nbtClass.newInstance();
				nmsEnt.getClass().getMethod("e", nbtClass).invoke(nmsEnt, NBT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return new Object[] { NBT };

		} else if (tar instanceof Block) {
			Block block = (Block) tar;
			Object NBT = null;
			Object tileEntity = null;
			try {
				NBT = nbtClass.newInstance();
				Object craftWorld = craftWorldClass.cast(block.getWorld());
				Object nmsWorld = craftWorld.getClass().getMethod("getHandle").invoke(craftWorld);
				tileEntity = nmsWorld.getClass().getMethod("getTileEntity", nmsPosClass).invoke(nmsWorld, nmsPosClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (tileEntity == null) {
				return null;
			}
			try {
				tileEntity.getClass().getMethod("b", nbtClass).invoke(tileEntity, NBT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return new Object[] { NBT };

		} else if (tar instanceof ItemStack) {
			ItemStack item = (ItemStack) tar;
			if (item.getType() == Material.AIR) {
				return null;
			}
			Object NBT = null;
			try {
				Object nmsItem = craftItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(item, item);
				NBT = nmsItem.getClass().getMethod("getTag").invoke(nmsItem);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (NBT == null || NBT.toString().equals("{}")) { //Null or empty.
				return null;
			}
			return new Object[] { NBT };
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, ChangeMode mode) {
		Object tar = target.getSingle(e);
		if (!(delta[0] instanceof String))
			return;
		if (tar instanceof Entity) {
			Object NBT = null;
			Object nmsEnt = null;
			try {
				nmsEnt = craftEntClass.getMethod("getHandle").invoke(tar);
				NBT = nbtClass.newInstance();
				nmsEnt.getClass().getMethod("e", nbtClass).invoke(nmsEnt, NBT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (mode == ChangeMode.ADD) {
				String newTags = (String) (delta[0]);
				try {
					Object NBT1 = null;
					NBT1 = nbtParserClass.getMethod("parse", String.class).invoke(NBT1, newTags);
					NBT1.getClass().getMethod("remove", String.class).invoke(NBT1, "UUIDMost"); // Prevent crucial data from being modified
					NBT1.getClass().getMethod("remove", String.class).invoke(NBT1, "UUIDLeast"); // Prevent crucial data from being modified
					NBT1.getClass().getMethod("remove", String.class).invoke(NBT1, "WorldUUIDMost"); // Prevent crucial data from being modified
					NBT1.getClass().getMethod("remove", String.class).invoke(NBT1, "WorldUUIDLeast"); // Prevent crucial data from being modified
					NBT1.getClass().getMethod("remove", String.class).invoke(NBT1, "Bukkit.updateLevel"); // Prevent crucial data from being modified
					NBTUtil.addCompound(NBT, NBT1);
					nmsEnt.getClass().getMethod("f", nbtClass).invoke(nmsEnt, NBT);
				} catch (Exception ex) {
					if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
						Skript.warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Error when parsing NBT - " + ex.getCause().getMessage() + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
						return;
					}
					ex.printStackTrace();
				}
			} else if (mode == ChangeMode.REMOVE) {
				for (Object s : delta) {
					if (s != "UUIDMost" || s != "UUIDLeast" || s != "WorldUUIDMost" || s != "WorldUUIDLeast" || s != "Bukkit.updateLevel") { // Prevent crucial data from being modified
						try {
							NBT.getClass().getMethod("remove", String.class).invoke(NBT, (String) s);
							nmsEnt.getClass().getMethod("f", nbtClass).invoke(nmsEnt, NBT);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		} else if (tar instanceof Block) {
			Block block = (Block) tar;
			Object NBT = null;
			Object tileEntity = null;
			Object nmsWorld = null;
			try {
				NBT = nbtClass.newInstance();
				Object craftWorld = craftWorldClass.cast(block.getWorld());
				nmsWorld = craftWorld.getClass().getMethod("getHandle").invoke(craftWorld);
				tileEntity = nmsWorld.getClass().getMethod("getTileEntity", nmsPosClass).invoke(nmsWorld, nmsPosClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (tileEntity == null) {
				return;
			}
			if (mode == ChangeMode.ADD) {
				String newTags = (String) (delta[0]);
				try {
					tileEntity.getClass().getMethod("b", nbtClass).invoke(tileEntity, NBT);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					Object NBT1 = null;
					NBT1 = nbtParserClass.getMethod("parse", String.class).invoke(NBT1, newTags);
					NBTUtil.addCompound(NBT, NBT1);
					NBT1.getClass().getMethod("setInt", String.class, int.class).invoke(NBT1, "x", block.getX());
					NBT1.getClass().getMethod("setInt", String.class, int.class).invoke(NBT1, "y", block.getY());
					NBT1.getClass().getMethod("setInt", String.class, int.class).invoke(NBT1, "z", block.getZ());
					tileEntity.getClass().getMethod("a", nbtClass).invoke(tileEntity, NBT);
					tileEntity.getClass().getMethod("update").invoke(tileEntity);
					nmsWorld.getClass().getMethod("notify", nmsPosClass).invoke(nmsWorld, tileEntity.getClass().getMethod("getPosition").invoke(tileEntity));
				} catch (Exception ex) {
					if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
						Skript.warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Error when parsing NBT - " + ex.getCause().getMessage() + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
						return;
					}
					ex.printStackTrace();
				}
			} else if (mode == ChangeMode.REMOVE) {
				try {
					tileEntity.getClass().getMethod("b", nbtClass).invoke(tileEntity, NBT);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				for (Object s : delta) {
					if (s != "x" || s != "y" || s != "z" || s != "id") {
						try {
							NBT.getClass().getMethod("remove", String.class).invoke(NBT, ((String) s));
							tileEntity.getClass().getMethod("a", nbtClass).invoke(tileEntity, NBT);
							tileEntity.getClass().getMethod("update").invoke(tileEntity);
							nmsWorld.getClass().getMethod("notify", nmsPosClass).invoke(nmsWorld, tileEntity.getClass().getMethod("getPosition").invoke(tileEntity));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		} else if (tar instanceof ItemStack) {
			ItemStack item = (ItemStack) tar;
			if (item.getType() == Material.AIR) {
				return;
			}
			Object nmsItem = null;
			Object NBT = null;
			try {
				nmsItem = craftItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(item, item);
				NBT = nmsItem.getClass().getMethod("getTag").invoke(nmsItem);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (NBT == null) { //No need to check for "{}" (empty) NBT because a new instance of NBT is actually "{}".
				try {
					NBT = nbtClass.newInstance();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (mode == ChangeMode.ADD) {
				String newTags = (String) (delta[0]);
				try {
					Object NBT1 = null;
					NBT1 = nbtParserClass.getMethod("parse", String.class).invoke(NBT1, newTags);
					NBTUtil.addCompound(NBT, NBT1);
					nmsItem.getClass().getMethod("setTag", nbtClass).invoke(nmsItem, NBT);
					Object newItem = null;
					newItem = craftItemClass.getMethod("asCraftMirror", nmsItemClass).invoke(newItem, nmsItem);
					Object[] slot = target.getSource().getAll(e);
					if (!(slot[0] instanceof Slot)) {
						return;
					}
					((Slot) slot[0]).setItem((ItemStack) newItem);
				} catch (Exception ex) {
					if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
						Skript.warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Error when parsing NBT - " + ex.getCause().getMessage() + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
						return;
					}
					ex.printStackTrace();
				}
			} else if (mode == ChangeMode.REMOVE) {
				if (NBT == null || NBT.toString().equals("{}")) { //Check for "{}" (empty) NBT because executing the remove is just useless.
					return;
				}
				for (Object s : delta) {
					try {
						NBT.getClass().getMethod("remove", String.class).invoke(NBT ,((String) s));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				Object newItem = null;
				try {
					nmsItem.getClass().getMethod("setTag", nbtClass).invoke(nmsItem, NBT);
					newItem = craftItemClass.getMethod("asCraftMirror", nmsItemClass).invoke(newItem, nmsItem);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				Object[] slot = target.getSource().getAll(e);
				((Slot) slot[0]).setItem((ItemStack) newItem);
			} else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
				Object newItem = null;
				try {
					nmsItem.getClass().getMethod("setTag", nbtClass).invoke(nmsItem, nbtClass.newInstance());
					newItem = craftItemClass.getMethod("asCraftMirror", nmsItemClass).invoke(newItem, nmsItem);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				Object[] slot = target.getSource().getAll(e);
				if (!(slot[0] instanceof Slot)) {
					return;
				}
				((Slot) slot[0]).setItem((ItemStack) newItem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			return CollectionUtils.array(String[].class);
		}
		return null;
	}
}