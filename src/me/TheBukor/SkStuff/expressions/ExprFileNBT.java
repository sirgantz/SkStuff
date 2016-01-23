package me.TheBukor.SkStuff.expressions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprFileNBT extends SimpleExpression<Object> {
	private Expression<String> input;
	private boolean using1_7 = false;

	private Class<?> nbtBaseClass = ReflectionUtils.getNMSClass("NBTBase");
	private Class<?> nbtToolsClass = ReflectionUtils.getNMSClass("NBTCompressedStreamTools");
	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound");
	private Class<?> nbtParserClass = ReflectionUtils.getNMSClass("MojangsonParser");

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
		input = (Expression<String>) expr[0];
		String bukkitVersion = ReflectionUtils.getVersion();
		if (bukkitVersion.startsWith("v1_7_R")) {
			using1_7 = true;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the NBT from file " + input.toString(e, false);
	}

	@Override
	@Nullable
	public Object[] get(Event e) {
		Object NBT = null;
		File file = new File(input.getSingle(e));
		InputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			return null; // File doesn't exist
		}
		try {
			NBT = nbtToolsClass.getMethod("a", FileInputStream.class).invoke(NBT, fis);
			fis.close();
		} catch (Exception ex) {
			if (ex instanceof InvocationTargetException) {
				if (ex.getCause().getClass().getName().equals("MojangsonParseException") ) {
					Skript.error("Error when parsing NBT - " + ex.getCause().getMessage());
					return null;
				}
				ex.printStackTrace();
			}
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return new Object[] { NBT };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		File file = new File(input.getSingle(e));
		String tags = (String) delta[0];
		OutputStream os = null;
		InputStream fis = null;
		try {
			os = new FileOutputStream(file);
			fis = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			return; // File doesn't exist.
		}
		if (mode == ChangeMode.ADD) {
			try {
				Object NBT = null;
				NBT = nbtToolsClass.getMethod("a", FileInputStream.class).invoke(NBT, fis);
				Object NBT1 = null;
				NBT1 = nbtParserClass.getMethod("parse", nbtClass).invoke(NBT1, tags);
				if (!using1_7) {
					NBT.getClass().getMethod("a", nbtClass).invoke(NBT, NBT1);
				} else {
					NBT.getClass().getMethod("set", String.class, nbtBaseClass).invoke(NBT, "", NBT1);
				}
				nbtToolsClass.getMethod("a", nbtClass, FileOutputStream.class).invoke(nbtToolsClass.newInstance(), NBT, os);
				fis.close();
				os.close();
			} catch (Exception ex) {
				if (ex instanceof InvocationTargetException) {
					if (ex.getCause().getClass().getName().equals("MojangsonParseException") ) {
						Skript.error("Error when parsing NBT - " + ex.getCause().getMessage());
						return;
					}
					ex.printStackTrace();
				}
				ex.printStackTrace();
			} finally {
				try {
					fis.close();
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if (mode == ChangeMode.REMOVE) {
			try {
				Object NBT = null;
				NBT = nbtToolsClass.getMethod("a", FileInputStream.class).invoke(NBT, fis);
				for (Object s : delta) {
					nbtClass.getMethod("remove", String.class).invoke(NBT, s);
				}
				nbtToolsClass.getMethod("a", nbtClass, FileOutputStream.class).invoke(nbtToolsClass.newInstance(), NBT, os);
				Bukkit.broadcastMessage("\n\nSecond: " + NBT.toString());
				fis.close();
				os.close();
			} catch (Exception ex) {
				if (ex instanceof InvocationTargetException) {
					if (ex.getCause().getClass().getName().equals("MojangsonParseException") ) {
						Skript.error("Error when parsing NBT - " + ex.getCause().getMessage());
						return;
					}
					ex.printStackTrace();
				} else if (ex instanceof EOFException) {
					//No actual error, just end of the file. Ignore it.
				}
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}
}