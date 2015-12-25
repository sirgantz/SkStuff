package me.TheBukor.SkStuff.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EvtWorldEditChange extends Event {
	static Player player;
	static Block block;

	public EvtWorldEditChange(Player player, Block block) {
		EvtWorldEditChange.player = player;
		EvtWorldEditChange.block = block;
	}

	public static Player getPlayer() {
		return player;
	}

	public static Block getBlock() {
		return block;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}