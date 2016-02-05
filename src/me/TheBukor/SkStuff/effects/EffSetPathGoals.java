package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffSetPathGoals extends Effect {
	private Expression<Integer> goalPriority;
	private Expression<EntityData<?>> entityToAvoid;
	private Expression<Number> avoidRadius;
	private Expression<Number> avoidSpeed1;
	private Expression<Number> avoidSpeed2;
	private Expression<Number> breedSpeed;
	private Expression<Number> fleeSunSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> followOwnerSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> followAdultsSpeed;
	@SuppressWarnings("unused")
	private Expression<EntityData<?>> entitiesToFightBack;
	@SuppressWarnings("unused")
	private Expression<Number> jumpOnBlockSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> leapHeight;
	@SuppressWarnings("unused")
	private Expression<Number> lookRadius;
	@SuppressWarnings("unused")
	private Expression<EntityData<?>> meleeTarget;
	@SuppressWarnings("unused")
	private Expression<Number> meleeSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> moveVillageSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> moveTargetSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> moveTargetRadius;
	@SuppressWarnings("unused")
	private Expression<EntityData<?>> nearTarget;
	@SuppressWarnings("unused")
	private Expression<Number> panicSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> randomWalkSpeed;
	@SuppressWarnings("unused")
	private Expression<Integer> randomWalkInterval;
	private Expression<LivingEntity> entity;

	private int mark;

	private Class<?> goal = ReflectionUtils.getNMSClass("PathfinderGoal", false);
	private Class<?> goalSelector = ReflectionUtils.getNMSClass("PathfinderGoalSelector", false);
	private Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget", false);
	private Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed", false);
	private Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor", false);
	private Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile", false);
	private Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun", false);
	@SuppressWarnings("unused")
	private Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget", false);

	private Class<?> entAnimal = ReflectionUtils.getNMSClass("EntityAnimal", false);
	private Class<?> entCreature = ReflectionUtils.getNMSClass("EntityCreature", false);
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient", false);
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		goalPriority = (Expression<Integer>) expr[0];
		mark = result.mark;
		if (mark == 0) {
			entityToAvoid = (Expression<EntityData<?>>) expr[1];
			avoidRadius = (Expression<Number>) expr[2];
			avoidSpeed1 = (Expression<Number>) expr[3];
			avoidSpeed2 = (Expression<Number>) expr[4];
		} else if (mark == 2) { 
			breedSpeed = (Expression<Number>) expr[5];
		} else if (mark == 4) {
			fleeSunSpeed = (Expression<Number>) expr[6];
		} else if (mark == 6) {
			followOwnerSpeed = (Expression<Number>) expr[7];
		} else if (mark == 7) {
			followAdultsSpeed = (Expression<Number>) expr[8];
		} else if (mark == 8) {
			entitiesToFightBack = (Expression<EntityData<?>>) expr[9];
		} else if (mark == 9) {
			jumpOnBlockSpeed = (Expression<Number>) expr[10];
		} else if (mark == 10) {
			leapHeight = (Expression<Number>) expr[11];
		} else if (mark == 11) {
			lookRadius = (Expression<Number>) expr[12];
		} else if (mark == 12) {
			meleeTarget = (Expression<EntityData<?>>) expr[13];
			meleeSpeed = (Expression<Number>) expr[14];
		} else if (mark == 13) {
			moveVillageSpeed = (Expression<Number>) expr[15];
		} else if (mark == 15) {
			moveTargetSpeed = (Expression<Number>) expr[16];
			moveTargetRadius = (Expression<Number>) expr[17];
		} else if (mark == 16) {
			nearTarget = (Expression<EntityData<?>>) expr[18];
		} else if (mark == 19) {
			panicSpeed = (Expression<Number>) expr[19];
		} else if (mark == 21) {
			randomWalkSpeed = (Expression<Number>) expr[20];
			randomWalkInterval = (Expression<Integer>) expr[21];
		}
		entity = (Expression<LivingEntity>) expr[22];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "add pathfinder goal to ent";
	}

	@Override
	protected void execute(Event e) {
		int priority = 0;
		if (goalPriority != null) {
			priority = goalPriority.getSingle(e).intValue();
		} else {
			priority = 1;
		}
		if (priority < 0) {
			priority = 1;
		} else if (priority > 9) {
			priority = 9;
		}
		LivingEntity ent = entity.getSingle(e);
		if (ent instanceof Player || ent == null)
			return;
		Object obcEnt = craftLivEnt.cast(ent);
		try {
			Object newGoal = null;
			Object nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
			Object goals = ReflectionUtils.getField("goalSelector", entInsent, nmsEnt);
			if (mark == 0) {
				float radius = avoidRadius.getSingle(e).floatValue();
				double spd1 = avoidSpeed1.getSingle(e).doubleValue();
				double spd2 = avoidSpeed2.getSingle(e).doubleValue();
				EntityData<?> entityData;
				String exprInput = entityToAvoid.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className, false);
				if (nmsClass == null)
					return;
				newGoal = goalAvoid.getConstructor(entCreature, Class.class, float.class, double.class, double.class).newInstance(nmsEnt, nmsClass, radius, spd1, spd2);
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(goals, priority, newGoal);
			} else if (mark == 1) {
				newGoal = goalBreakDoor.getConstructor(entInsent).newInstance(nmsEnt);
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(goals, priority, newGoal);
			} else if (mark == 2) {
				double spd = breedSpeed.getSingle(e).doubleValue();
				if (!(nmsEnt.getClass().isAssignableFrom(entAnimal)))
					return;
				newGoal = goalBreed.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(goals, priority, newGoal);
			} else if (mark == 3) {
				newGoal = goalEatGrass.getConstructor(entInsent).newInstance(nmsEnt);
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(goals, priority, newGoal);
			} else if (mark == 4) {
				double spd = fleeSunSpeed.getSingle(e).doubleValue();
				newGoal = goalFleeSun.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(goals, priority, newGoal);
			} else {
				Bukkit.broadcastMessage("Not an Avoid, BreakDoor, Breed or FleeSun goal");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}