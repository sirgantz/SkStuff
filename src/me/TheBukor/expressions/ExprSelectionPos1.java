package me.TheBukor.expressions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprSelectionPos1 extends SimpleExpression<Location> {
	private Expression<Player> player;
	private WorldEditPlugin we =  (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the WorldEdit point 1 selection of " + player.toString(e, false);
	}

	@Override
	@Nullable
	protected Location[] get(Event e) {
		Player p = player.getSingle(e);
		Region region = null;
		try {
			region = we.getSession(p).getSelection((World) BukkitUtil.getLocalWorld(we.getSelection(p).getWorld()));
		} catch (IncompleteRegionException ex) {
			return null;
		}
		if (!(region instanceof CuboidRegion)) {
			return null; //Who uses polygonal and other selection types anyways?
		}
		CuboidRegion cuboid = (CuboidRegion) region;
		Vector pos = cuboid.getPos1();
		return new Location[] { new Location(we.getSelection(p).getWorld(), pos.getX(), pos.getY(), pos.getZ()) };
	}
}