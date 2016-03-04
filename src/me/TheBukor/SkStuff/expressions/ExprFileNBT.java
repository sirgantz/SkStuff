package me.TheBukor.SkStuff.expressions;

import java.io.File;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.SkStuff;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class ExprFileNBT extends SimpleExpression<Object> {
	private Expression<String> input;

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound");

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
	public String toString(@Nullable Event e, boolean debug) {
		return "the NBT from file " + input.toString(e, debug);
	}

	@Override
	@Nullable
	public Object[] get(Event e) {
		String fileName = input.getSingle(e);
		fileName = !fileName.endsWith(".dat") ? fileName + ".dat" : fileName;
		File file = new File(fileName);
		if (!file.exists())
			return null;
		return new Object[] { SkStuff.getNMSMethods().getFileNBT(file) };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		String fileName = input.getSingle(e);
		fileName = !fileName.endsWith(".dat") ? fileName + ".dat" : fileName;
		File file = new File(fileName);
		if (!file.exists())
			return;
		Object fileNBT = SkStuff.getNMSMethods().getFileNBT(file);
		if (mode == ChangeMode.ADD) {
			Object parsedNBT = null;
			parsedNBT = SkStuff.getNMSMethods().parseRawNBT((String) delta[0]);
			SkStuff.getNMSMethods().addToCompound(fileNBT, parsedNBT);
			SkStuff.getNMSMethods().setFileNBT(file, fileNBT);
		} else if (mode == ChangeMode.REMOVE) {
			for (Object s : delta) {
				SkStuff.getNMSMethods().removeFromCompound(fileNBT, (String) s);
			}
			SkStuff.getNMSMethods().setFileNBT(file, fileNBT);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(String[].class);
		}
		return null;
	}
}