package me.TheBukor.SkStuff.effects;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffRemovePathGoal extends Effect {
	private Expression<LivingEntity> entity;

	private int mark;

	private Class<?> goalSelectorClass = ReflectionUtils.getNMSClass("PathfinderGoalSelector", false);
	private Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget", false);
	private Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed", false);
	private Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor", false);
	private Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile", false);
	private Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun", false);
	private Class<?> goalFloat = ReflectionUtils.getNMSClass("PathfinderGoalFloat", false);
	private Class<?> goalFollowOwner = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner", false);
	private Class<?> goalFollowAdults = ReflectionUtils.getNMSClass("PathfinderGoalFollowParent", false);
	private Class<?> goalReactAttack = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget", false);
	private Class<?> goalJumpOnBlock = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock", false);
	private Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget", false);
	private Class<?> goalLookEntities = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer", false);
	private Class<?> goalMeleeAttack = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack", false);
	private Class<?> goalGotoTarget = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget", false);
	private Class<?> goalNearTarget = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget", false);
	private Class<?> goalOcelotAttack = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack", false);
	private Class<?> goalOpenDoors = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor", false);
	private Class<?> goalPanic = ReflectionUtils.getNMSClass("PathfinderGoalPanic", false);
	private Class<?> goalRandomLook = ReflectionUtils.getNMSClass("PathfinderGoalRandomLookaround", false);
	private Class<?> goalWander = ReflectionUtils.getNMSClass("PathfinderGoalRandomStroll", false);
	private Class<?> goalSit = ReflectionUtils.getNMSClass("PathfinderGoalSit", false);
	private Class<?> goalSwell = ReflectionUtils.getNMSClass("PathfinderGoalSwell", false);

	private Class<?> entBlaze = ReflectionUtils.getNMSClass("EntityBlaze", false);
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient", false);
	private Class<?> entSquid = ReflectionUtils.getNMSClass("EntitySquid", false);
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		mark = result.mark;
		entity = (Expression<LivingEntity>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "remove pathfind goal of " + entity.toString(e, false);
	}

	@SuppressWarnings({ "unused" })
	@Override
	protected void execute(Event e) {
		LivingEntity ent = entity.getSingle(e);
		if (ent instanceof Player || ent == null)
			return;
		Object obcEnt = craftLivEnt.cast(ent);
		try {
			Object nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
			Object goalSelector = ReflectionUtils.getField("goalSelector", entInsent, nmsEnt);
			Object targetSelector = ReflectionUtils.getField("targetSelector", entInsent, nmsEnt);
			Object toRemove = null;
			if (mark == 0) {
				toRemove = goalAvoid;
			} else if (mark == 1) {
				toRemove = goalBreakDoor;
			} else if (mark == 2) {
				toRemove = goalBreed;
			} else if (mark == 3) {
				toRemove = goalEatGrass;
			} else if (mark == 4) {
				toRemove = goalFleeSun;
			} else if (mark == 5) {
				toRemove = goalFloat;
			} else if (mark == 6) {
				toRemove = goalFollowOwner;
			} else if (mark == 7) {
				toRemove = goalFollowAdults;
			} else if (mark == 8) {
				toRemove = goalReactAttack;
			} else if (mark == 9) {
				toRemove = goalJumpOnBlock;
			} else if (mark == 10) {
				toRemove = goalLeapTarget;
			} else if (mark == 11) {
				toRemove = goalLookEntities;
			} else if (mark == 12) {
				toRemove = goalMeleeAttack;
			} else if (mark == 13) {
				toRemove = goalGotoTarget;
			} else if (mark == 14) {
				toRemove = goalNearTarget;
			} else if (mark == 15) {
				toRemove = goalOcelotAttack;
			} else if (mark == 16) {
				toRemove = goalOpenDoors;
			} else if (mark == 17) {
				toRemove = goalPanic;
			} else if (mark == 18) {
				toRemove = goalRandomLook;
			} else if (mark == 19) {
				toRemove = goalWander;
			} else if (mark == 20) {
				toRemove = goalSit;
			} else if (mark == 21) {
				toRemove = goalSwell;
			} else if (mark == 22) {
				Class<?>[] classes = entSquid.getDeclaredClasses();
				for (Class<?> c : classes) {
					Bukkit.broadcastMessage("\u00A79loop-class: \u00A7b" + c);
					if (c.getSimpleName().equals("PathfinderGoalSquid")) {
						toRemove = c;
						break;
					}
				}
			} else if (mark == 23) {
				Class<?>[] classes = entBlaze.getDeclaredClasses();
				for (Class<?> c : classes) {
					Bukkit.broadcastMessage("\u00A79loop-class: \u00A7b" + c);
					if (c.getSimpleName().equals("PathfinderGoalBlazeFireball")) {
						toRemove = c;
						break;
					}
				}
			}
			if (toRemove == null)
				return;
			Iterator<?> goals = ((List<?>) ReflectionUtils.getField("b", goalSelectorClass, goalSelector)).iterator();
			Iterator<?> goalPriorities = ((List<?>) ReflectionUtils.getField("c", goalSelectorClass, goalSelector)).iterator();
			while (goals.hasNext()) {
				Object o = goals.next();
				goalPriorities.next();
				if (ReflectionUtils.getField("a", o.getClass(), o).getClass() == toRemove) {
					goals.remove();
					goalPriorities.remove();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}