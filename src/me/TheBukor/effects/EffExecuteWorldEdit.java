package me.TheBukor.effects;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@SuppressWarnings("deprecation")
public class EffExecuteWorldEdit extends Effect {
	private Expression<Player> player;
	private Expression<ItemStack> block;
	private Expression<Integer> blockLimit;
	private WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) expr[0];
		block = (Expression<ItemStack>) expr[1];
		blockLimit = (Expression<Integer>) expr[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "make " + player.toString(e, false) + " execute WorldEdit set using " + block.toString(e, false) + new String(blockLimit.getSingle(e) != null ? " with limit of " + blockLimit.toString(e, false) + " blocks" : "");
	}

	@Override
	protected void execute(Event e) {
		Player p = player.getSingle(e);
		ItemStack b = block.getSingle(e);
		Integer limit = blockLimit.getSingle(e);
		if (limit == null) limit = we.getWorldEdit().getConfiguration().defaultChangeLimit;
		if (we.getSelection(p) != null) {
			if (b.getType().isBlock()) {
				try {
					EditSession session = we.createEditSession(p);
					p.sendMessage(Boolean.toString(session.isQueueEnabled()));
					session.enableQueue();
					session.setBlockChangeLimit(limit);
					session.setBlocks(we.getSession(p).getSelection((World) we.wrapPlayer(p).getWorld()), new BaseBlock(b.getTypeId(), b.getDurability()));
					we.getSession(p).remember(session);
				} catch (MaxChangedBlocksException | IncompleteRegionException ex) {
					return;
				}
			}
		}
	}
}
