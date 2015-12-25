package me.TheBukor.SkStuff.events;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;

public class WorldEditChangeHandler {
	
	public WorldEditChangeHandler() {
		WorldEdit.getInstance().getEventBus().register(this);
	}
	
    @Subscribe
    public void wrapForLogging(EditSessionEvent event) {
        Actor actor = event.getActor();
        World world = event.getWorld();
        if (world instanceof BukkitWorld) {
            event.setExtent(new WorldEditExtent(actor, world, event.getExtent()));
        }
    }
}