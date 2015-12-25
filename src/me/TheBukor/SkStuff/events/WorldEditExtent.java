package me.TheBukor.SkStuff.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.logging.AbstractLoggingExtent;

public class WorldEditExtent extends AbstractLoggingExtent {

    private final Actor actor;
    private final World world;

    public WorldEditExtent(Actor actor, com.sk89q.worldedit.world.World weWorld, Extent extent) {
        super(extent);
        this.actor = actor;
        this.world = ((BukkitWorld) weWorld).getWorld();
    }
 
    @Override
    protected void onBlockChange(final Vector vec, BaseBlock baseBlock) {
        final Block b = BukkitUtil.toLocation(world, vec).getBlock();
       	final Player p = Bukkit.getPlayerExact(actor.getName());
		Bukkit.getPluginManager().callEvent(new EvtWorldEditChange(p, b));
    }
}