package me.TheBukor.effects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@SuppressWarnings("deprecation")
public class EffDrawLineWE extends Effect {
	private Expression<Location> location1;
	private Expression<Location> location2;
	private Expression<EditSession> editSession;
	private Expression<ItemStack> block;
	private Expression<Double> thickness;
	private boolean filled = true;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		location1 = (Expression<Location>) expr[0];
		location2 = (Expression<Location>) expr[1];
		editSession = (Expression<EditSession>) expr[2];
		block = (Expression<ItemStack>) expr[3];
		thickness = (Expression<Double>) expr[4];
		if (matchedPattern == 1) filled = false;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "draw a line from " + location1.toString(e, false) + " to " + location2.toString(e, false) + " using an edit session with " + block.toString(e, false) + " and thickness " + thickness.toString(e, false);
	}

	@Override
	protected void execute(Event e) {
		Location pos1 = location1.getSingle(e);
		Location pos2 = location2.getSingle(e);
		EditSession session = editSession.getSingle(e);
		ItemStack b = block.getSingle(e);
		Double thick = thickness.getSingle(e);
		if (b.getType().isBlock()) {
			try {
				session.drawLine(new SingleBlockPattern(new BaseBlock(b.getTypeId(), b.getDurability())), BukkitUtil.toVector(pos1), BukkitUtil.toVector(pos2), thick, filled);
				session.flushQueue();
			} catch (MaxChangedBlocksException ex) {
				return;
			}
		}
	}
}
