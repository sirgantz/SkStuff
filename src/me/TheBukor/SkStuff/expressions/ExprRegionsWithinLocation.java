package me.TheBukor.SkStuff.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprRegionsWithinLocation extends SimpleExpression<ProtectedRegion> {
	private Expression<Location> location;

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends ProtectedRegion> getReturnType() {
		return ProtectedRegion.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		location = (Expression<Location>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "all the regions containing " + location.toString(e, debug);
	}

	@Override
	@Nullable
	protected ProtectedRegion[] get(Event e) {
		WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		Location loc = location.getSingle(e);
		Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
		Map<String, ProtectedRegion> regions = wg.getRegionManager(loc.getWorld()).getRegions();
		List<ProtectedRegion> regionsInside = new ArrayList<ProtectedRegion>();
		for (ProtectedRegion region : regions.values()) {
			if (region.contains(vec)) {
				regionsInside.add(region);
			}
		}
		return regionsInside.toArray(new ProtectedRegion[0]);
	}
}
