package me.TheBukor.expressions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipException;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minecraft.server.v1_8_R2.MojangsonParseException;
import net.minecraft.server.v1_8_R2.MojangsonParser;
import net.minecraft.server.v1_8_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R2.NBTTagCompound;

public class ExprFileNBTv1_8_R2 extends SimpleExpression<NBTTagCompound> {
	private Expression<String> input;

	@Override
	public Class<? extends NBTTagCompound> getReturnType() {
		return NBTTagCompound.class;
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
		return "the NBT of file from file " + input.toString(e, false);
	}

	@Override
	@Nullable
	protected NBTTagCompound[] get(Event e) {
		NBTTagCompound NBT = null;
		File file = new File(input.getSingle(e));
		if (file.exists()) {
			try {
				InputStream fis = new FileInputStream(file);
				NBT = NBTCompressedStreamTools.a(fis);
				fis.close();
			} catch (EOFException ex) {
				//End of file, no error.
			} catch (ZipException ex) {
				return null; //Not a valid file (not compressed in GZIP format)
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			return null; // Specified file doesn't exist
		}
		return new NBTTagCompound[] { NBT };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		File file = new File(input.getSingle(e));
		String tags = (String) delta[0];
		if (mode == ChangeMode.ADD) {
			try {
				InputStream fis = new FileInputStream(file);
				NBTTagCompound NBT = NBTCompressedStreamTools.a(fis);
				OutputStream os = new FileOutputStream(file);
				NBTTagCompound NBT1 = MojangsonParser.parse(tags);
				NBT.a(NBT1);
				NBTCompressedStreamTools.a(NBT, os);
				fis.close();
				os.close();
			} catch (EOFException ex) {
				//End of file, no error.
			} catch (ZipException ex) {
				return; //Not a valid file (not compressed in GZIP format)
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (MojangsonParseException ex) {
				Skript.warning("Error when parsing NBT - " + ex.getMessage());
			}
		} else if (mode == ChangeMode.REMOVE) {
			try {
				InputStream fis = new FileInputStream(file);
				NBTTagCompound NBT = NBTCompressedStreamTools.a(fis);
				OutputStream os = new FileOutputStream(file);
				NBT.remove(tags);
				NBTCompressedStreamTools.a(NBT, os);
				fis.close();
				os.close();
			} catch (EOFException ex) {
				//End of file, no error.
			} catch (ZipException ex) {
				return; //Not a valid file (not compressed in GZIP format)
			} catch (IOException ex) {
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