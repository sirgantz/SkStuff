package me.TheBukor.SkStuff.expressions;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprWGMemberOwner extends SimpleExpression<OfflinePlayer> {
	private Expression<ProtectedRegion> region;

	private int mark;

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult result) {
		region = (Expression<ProtectedRegion>) expr[0];
		mark = result.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		String markString = mark == 0 ? "members" : "owners";
		return "the " + markString + " of the worldguard region " + region.toString(e, debug);
	}

	@Override
	@Nullable
	protected OfflinePlayer[] get(Event e) {
		ProtectedRegion reg = region.getSingle(e);
		Set<UUID> uuids;
		if (mark == 0) { 
			uuids = reg.getMembers().getUniqueIds();
		} else {
			uuids = reg.getOwners().getUniqueIds();
		}
		if (uuids.isEmpty()) {
			return null;
		}
		OfflinePlayer[] offPlayers = new OfflinePlayer[uuids.size()];
		int i = 0;
		for (UUID uuid : uuids) {
			offPlayers[i] = Bukkit.getOfflinePlayer(uuid);
			i++;
		}
		return offPlayers;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(OfflinePlayer[].class);
		}
		return null;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		ProtectedRegion reg = region.getSingle(e);
		if (mode == ChangeMode.ADD) {
			OfflinePlayer[] toAdd = Arrays.copyOf(delta, delta.length, OfflinePlayer[].class);
			for (OfflinePlayer offPlayer : toAdd) {
				DefaultDomain domain;
				if (mark == 0) {
					domain = reg.getMembers();
				} else {
					domain = reg.getOwners();
				}
				domain.addPlayer(offPlayer.getUniqueId());
			}
		} else if (mode == ChangeMode.REMOVE) {
			OfflinePlayer[] toRemove = Arrays.copyOf(delta, delta.length, OfflinePlayer[].class);
			for (OfflinePlayer offPlayer : toRemove) {
				DefaultDomain domain;
				if (mark == 0) {
					domain = reg.getMembers();
				} else {
					domain = reg.getOwners();
				}
				domain.removePlayer(offPlayer.getUniqueId());
			}
		}
	}
}
