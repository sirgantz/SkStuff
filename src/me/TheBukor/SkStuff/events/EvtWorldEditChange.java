package me.TheBukor.SkStuff.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EvtWorldEditChange extends Event {
	static Player player;
	static Block block;
	static Block futureBlock;

	public EvtWorldEditChange(Player player, Block block, Block futureBlock) {
		EvtWorldEditChange.player = player;
		EvtWorldEditChange.block = block;
		EvtWorldEditChange.futureBlock = futureBlock;
	}

	public static Player getPlayer() {
		return player;
	}

	public static Block getBlock() {
		return block;
	}
	
	public static Block getFutureBlock() {
		return futureBlock;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}