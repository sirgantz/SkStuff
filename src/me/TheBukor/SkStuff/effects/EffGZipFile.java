package me.TheBukor.SkStuff.effects;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffGZipFile extends Effect {
	private Expression<String> filePath;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		filePath = (Expression<String>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "create GZipped file at path " + filePath.toString(e, debug);
	}

	@Override
	protected void execute(Event e) {
		File newFile = new File(filePath.getSingle(e));
		if (!newFile.exists()) {
			try {
				new GZIPOutputStream(new FileOutputStream(newFile)).close();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				if (!(ex instanceof EOFException)) {
					ex.printStackTrace();
				}
			}
		}
	}
}
