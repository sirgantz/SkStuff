package me.TheBukor.SkStuff.expressions;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.event.Event;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprFlagOfWGRegion extends SimpleExpression<String> {
	private Expression<Flag<?>> flag;
	private Expression<ProtectedRegion> region;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] expr, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		flag = (Expression<Flag<?>>) expr[0];
		region = (Expression<ProtectedRegion>) expr[1];
		return true;
	}

	@Override
	protected String[] get(final Event e) {
		ProtectedRegion region = this.region.getSingle(e);
		Flag<?> flag = this.flag.getSingle(e);
		return new String[] { region.getFlag(flag).toString() };
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "worldguard flag " + flag.toString(e, debug) + " of " + region.toString(e, debug);
	}

	@Override
	public void change(Event e, Object[] delta, ChangeMode mode) {
		ProtectedRegion region = this.region.getSingle(e);
		Flag<?> flag = this.flag.getSingle(e);
		if (region == null)
			return;
		if (mode == ChangeMode.SET) {
			if (flag instanceof StateFlag && delta[0] instanceof Boolean) {
				boolean allow = (boolean) delta[0];
				State newState = State.DENY;
				if (allow) {
					newState = State.ALLOW;
				}
				region.setFlag((StateFlag) flag, newState);
			} else if (flag instanceof StringFlag && delta[0] instanceof String) {
				String newValue = (String) delta[0];
				region.setFlag((StringFlag) flag, newValue);
			} else if (flag instanceof BooleanFlag && delta[0] instanceof Boolean) {
				boolean newValue = (boolean) delta[0];
				region.setFlag((BooleanFlag) flag, newValue);
			/*
			} else if (flag instanceof SetFlag) {
				if (delta instanceof EntityData[]) {
					if (((SetFlag) flag).getType() instanceof EntityTypeFlag) {
						Set<EntityType> newSet = new HashSet<EntityType>();
						for (Object entData : delta) {
							EntityType toAdd = null;
							for (EntityType entType : EntityType.values()) { //A weird workaround I've thought to get the entity type from a Skript entity data
								if (((EntityData) entData).getType() == entType.getEntityClass()) {
									toAdd = entType;
								}
							}
							if (toAdd != null) {
								newSet.add(toAdd);
							}
						}
						region.setFlag((SetFlag<EntityType>) flag, newSet);
			*/
			} else {
				Skript.error("Sorry, this flag type isn't supported yet! Flag type: " + flag.getClass().getSimpleName());
			}
		} else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			region.setFlag(flag, null);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
			return CollectionUtils.array(String.class, Boolean.class, EntityData[].class);
		}
		return null;
	}
}
