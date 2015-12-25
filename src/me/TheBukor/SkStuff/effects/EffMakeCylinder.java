package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Patterns;
import com.sk89q.worldedit.function.pattern.RandomPattern;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffMakeCylinder extends Effect {
	private Expression<Location> location;
	private Expression<Double> radius1;
	private Expression<Double> radius2;
	private Expression<Integer> height;
	private Expression<EditSession> editSession;
	private Expression<ItemStack> blockList;
	private boolean filled = true;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		location = (Expression<Location>) expr[0];
		radius1 = (Expression<Double>) expr[1];
		height = (Expression<Integer>) expr[2];
		radius2 = (Expression<Double>) expr[3];
		editSession = (Expression<EditSession>) expr[4];
		blockList = (Expression<ItemStack>) expr[5];
		if (result.mark == 1) filled = false;
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "create a cylinder at " + location.toString(e, false) + " with a radius of " + radius1.toString(e, false) + " " + height.toString(e, false) + " " + radius2.toString(e, false) + " using an edit session with " + blockList.toString(e, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		Location loc = location.getSingle(e);
		Double rad1 = radius1.getSingle(e);
		Integer h = height.getSingle(e);
		Double rad2 = radius2.getSingle(e);
		EditSession session = editSession.getSingle(e);
		ItemStack[] blocks = blockList.getAll(e);
		RandomPattern random = new RandomPattern();
		if (session == null) return;
		for (ItemStack b : blocks) {
			if (b.getType().isBlock()) {
				random.add(new BlockPattern(new BaseBlock(b.getTypeId(), b.getDurability())), 50);
			}
		}
		try {
			session.makeCylinder(BukkitUtil.toVector(loc), Patterns.wrap(random), rad1, rad2, h, filled);
			session.flushQueue();
		} catch (WorldEditException ex) {
			if (ex instanceof MaxChangedBlocksException)
				return;
			else
				ex.printStackTrace();
		}
	}
}