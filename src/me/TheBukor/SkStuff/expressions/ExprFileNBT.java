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

import org.bukkit.event.Event;
import org.fusesource.jansi.Ansi;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.util.NBTUtil;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprFileNBT extends SimpleExpression<Object> {
	private Expression<String> input;

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound", false);
	private Class<?> nbtParserClass = ReflectionUtils.getNMSClass("MojangsonParser", false);
	private Class<?> nbtCompressedClass = ReflectionUtils.getNMSClass("NBTCompressedStreamTools", false);

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
			NBT = nbtCompressedClass.getMethod("a", FileInputStream.class).invoke(NBT, fis);
			fis.close();
		} catch (Exception ex) {
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
				NBT = nbtCompressedClass.getMethod("a", FileInputStream.class).invoke(NBT, fis);
				Object NBT1 = null;
				NBT1 = nbtParserClass.getMethod("parse", String.class).invoke(NBT1, tags);
				NBTUtil.addCompound(NBT, NBT1);
				nbtCompressedClass.getMethod("a", nbtClass, FileOutputStream.class).invoke(nbtCompressedClass.newInstance(), NBT, os);
				fis.close();
				os.close();
			} catch (Exception ex) {
				if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
					Skript.warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Error when parsing NBT - " + ex.getCause().getMessage() + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
					return;
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
				NBT = nbtCompressedClass.getMethod("a", FileInputStream.class).invoke(NBT, fis);
				for (Object s : delta) {
					nbtClass.getMethod("remove", String.class).invoke(NBT, s);
				}
				nbtCompressedClass.getMethod("a", nbtClass, FileOutputStream.class).invoke(nbtCompressedClass.newInstance(), NBT, os);
				fis.close();
				os.close();
			} catch (Exception ex) {
				if (ex instanceof EOFException) {
					// No actual error, just end of the file. Ignore it.
				} else {
					ex.printStackTrace();
				}
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