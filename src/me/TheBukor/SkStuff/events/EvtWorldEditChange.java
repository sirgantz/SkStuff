package me.TheBukor.SkStuff.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EvtWorldEditChange extends Event implements Cancellable {
	private static Player player;
	private static Block block;
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();

	public EvtWorldEditChange(Player player, Block block) {
		EvtWorldEditChange.player = player;
		EvtWorldEditChange.block = block;
		this.cancelled = false;
	}

	public static Player getPlayer() {
		return player;
	}

	public static Block getBlock() {
		return block;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean bool) {
		this.cancelled = bool;
	}
}