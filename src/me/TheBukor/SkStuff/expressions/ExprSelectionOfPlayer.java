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
import com.sk89q.worldedit.world.World;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprSelectionOfPlayer extends SimpleExpression<Location> {
	private Expression<Player> player;
	private WorldEditPlugin we =  (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");;

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "the WorldEdit selection of " + player.toString(e, false);
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
			return null;
		}
		CuboidRegion cuboid = (CuboidRegion) region;
		Vector pos1Vec = cuboid.getPos1();
		Vector pos2Vec = cuboid.getPos2();
		Location pos1 = new Location(we.getSelection(p).getWorld(), pos1Vec.getX(), pos1Vec.getY(), pos1Vec.getZ());
		Location pos2 = new Location(we.getSelection(p).getWorld(), pos2Vec.getX(), pos2Vec.getY(), pos2Vec.getZ());
		return new Location[] { pos1, pos2 };
	}
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Player p = player.getSingle(e);
		if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			if (we.getSelection(p) == null)
				return;
			we.getSession(p).getRegionSelector((World) BukkitUtil.getLocalWorld(we.getSelection(p).getWorld())).clear();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			return CollectionUtils.array(Location.class);
		}
		return null;
	}
}