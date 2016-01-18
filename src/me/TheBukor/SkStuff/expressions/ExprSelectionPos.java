package me.TheBukor.SkStuff.expressions;

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
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprSelectionPos extends SimpleExpression<Location> {
	private Expression<Player> player;
	private boolean usePos2 = false;
	private WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

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
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		player = (Expression<Player>) expr[0];
		if (result.mark == 1) {
			usePos2 = true;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the WorldEdit point " + (usePos2 ? "2" : "1") + " selection of " + player.toString(e, false);
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
		if (!(region instanceof CuboidRegion))
			return null; //Who uses polygonal and other selection types anyways?
		CuboidRegion cuboid = (CuboidRegion) region;
		Vector pos = null;
		if (usePos2 == true) {
			pos = cuboid.getPos2();
		} else {
			pos = cuboid.getPos1();
		}
		return new Location[] { BukkitUtil.toLocation(we.getSelection(p).getWorld(), pos) };
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Player p = player.getSingle(e);
		Location newLoc = (Location) delta[0];
		if (mode == ChangeMode.SET) {
			Region region = null;
			try {
				region = we.getSession(p).getSelection((World) BukkitUtil.getLocalWorld(we.getSelection(p).getWorld()));
			} catch (IncompleteRegionException | NullPointerException ex) {
				CuboidRegionSelector cuboidregion = new CuboidRegionSelector(BukkitUtil.getLocalWorld((org.bukkit.World) newLoc.getWorld()), BukkitUtil.toVector(newLoc), BukkitUtil.toVector(newLoc));
				we.getSession(p).setRegionSelector((World) BukkitUtil.getLocalWorld(p.getWorld()), cuboidregion);
			}
			if (!(region instanceof CuboidRegion))
				return; //Who uses polygonal and other selection types anyways?
			CuboidRegion cuboid = (CuboidRegion) region;
			if (usePos2 == true) {
				cuboid.setPos2(BukkitUtil.toVector(newLoc));
			} else {
				cuboid.setPos1(BukkitUtil.toVector(newLoc));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Location.class);
		}
		return null;
	}
}