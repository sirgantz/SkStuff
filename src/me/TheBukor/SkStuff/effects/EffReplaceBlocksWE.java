package me.TheBukor.SkStuff.effects;

import java.util.HashSet;
import java.util.Set;

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
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffReplaceBlocksWE extends Effect {
	private Expression<ItemStack> blockList1;
	private Expression<Location> location1;
	private Expression<Location> location2;
	private Expression<ItemStack> blockList2;
	private Expression<EditSession> editSession;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		blockList1 = (Expression<ItemStack>) expr[0];
		location1 = (Expression<Location>) expr[1];
		location2 = (Expression<Location>) expr[2];
		blockList2 = (Expression<ItemStack>) expr[3];
		editSession = (Expression<EditSession>) expr[4];
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "replace all " + blockList1.toString(e, debug) + " from " + location1.toString(e, debug) + " to " + location2.toString(e, debug) + " with " + blockList1.toString(e, debug) + " using an edit session";
	}

	@SuppressWarnings({ "deprecation" })
	@Override
	protected void execute(Event e) {
		Location pos1 = location1.getSingle(e);
		Location pos2 = location2.getSingle(e);
		EditSession session = editSession.getSingle(e);
		ItemStack[] blocks = blockList1.getAll(e);
		ItemStack[] blocksToPlace = blockList2.getAll(e);
		RandomPattern random = new RandomPattern();
		Set<BaseBlock> blocksToReplace = new HashSet<BaseBlock>();
		if (session == null) return;
		for (ItemStack b : blocks) {
			if (b.getType().isBlock()) {
				blocksToReplace.add(new BaseBlock(b.getTypeId(), b.getDurability()));
			}
		}
		for (ItemStack b : blocksToPlace) {
			if (b.getType().isBlock()) {
				random.add(new BlockPattern(new BaseBlock(b.getTypeId(), b.getDurability())), 50);
			}
		}
		try {
			session.replaceBlocks(new CuboidRegion((World) BukkitUtil.getLocalWorld(pos1.getWorld()), BukkitUtil.toVector(pos1), BukkitUtil.toVector(pos2)), blocksToReplace, Patterns.wrap(random));
			session.flushQueue();
		} catch (WorldEditException ex) {
			if (ex instanceof MaxChangedBlocksException)
				return;
			else
				ex.printStackTrace();
		}
	}
}