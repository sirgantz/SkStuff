package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffSetPathGoals extends Effect {
	private Expression<Integer> goalPriority;
	private Expression<EntityData<?>> typeToAvoid;
	private Expression<Number> avoidRadius;
	private Expression<Number> avoidSpeed1;
	private Expression<Number> avoidSpeed2;
	private Expression<Number> breedSpeed;
	private Expression<Number> fleeSunSpeed;
	private Expression<Number> followOwnerSpeed;
	private Expression<Number> followAdultsSpeed;
	private Expression<EntityData<?>> typesToFightBack;
	private Expression<Number> jumpOnBlockSpeed;
	private Expression<Number> leapHeight;
	private Expression<EntityData<?>> lookType;
	private Expression<Number> lookRadius;
	private Expression<EntityData<?>> meleeTarget;
	private Expression<Number> meleeSpeed;
	private Expression<Number> moveTargetSpeed;
	private Expression<Number> moveTargetRadius;
	private Expression<EntityData<?>> nearTarget;
	@SuppressWarnings("unused")
	private Expression<Number> panicSpeed;
	@SuppressWarnings("unused")
	private Expression<Number> randomWalkSpeed;
	@SuppressWarnings("unused")
	private Expression<Timespan> randomWalkInterval;
	private Expression<LivingEntity> entity;

	private int mark;

	private Class<?> goal = ReflectionUtils.getNMSClass("PathfinderGoal", false);
	private Class<?> goalSelector = ReflectionUtils.getNMSClass("PathfinderGoalSelector", false);
	private Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget", false);
	private Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed", false);
	private Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor", false);
	private Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile", false);
	private Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun", false);
	private Class<?> goalFloat = ReflectionUtils.getNMSClass("PathfinderGoalFloat", false);
	private Class<?> goalFollowOwner = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner", false);
	private Class<?> goalFollowAdults = ReflectionUtils.getNMSClass("PathfinderGoalFollowParents", false);
	private Class<?> goalReactAttack = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget", false);
	private Class<?> goalJumpOnBlock = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock", false);
	private Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget", false);
	private Class<?> goalLookEntities = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer", false);
	private Class<?> goalMeleeAttack = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack", false);
	private Class<?> goalGotoTarget = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget", false);
	private Class<?> goalNearTarget = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget", false);
	private Class<?> goalOcelotAttack = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack", false);
	private Class<?> goalOpenDoors = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor", false);

	private Class<?> entAnimal = ReflectionUtils.getNMSClass("EntityAnimal", false);
	private Class<?> entCreature = ReflectionUtils.getNMSClass("EntityCreature", false);
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient", false);
	private Class<?> entOcelot = ReflectionUtils.getNMSClass("EntityOcelot", false);
	private Class<?> entTameable = ReflectionUtils.getNMSClass("EntityTameableAnimal", false);
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		goalPriority = (Expression<Integer>) expr[0];
		mark = result.mark;
		if (mark == 0) {
			typeToAvoid = (Expression<EntityData<?>>) expr[1];
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
			typesToFightBack = (Expression<EntityData<?>>) expr[9];
		} else if (mark == 9) {
			jumpOnBlockSpeed = (Expression<Number>) expr[10];
		} else if (mark == 10) {
			leapHeight = (Expression<Number>) expr[11];
		} else if (mark == 11) {
			lookType = (Expression<EntityData<?>>) expr[12];
			lookRadius = (Expression<Number>) expr[13];
		} else if (mark == 12) {
			meleeTarget = (Expression<EntityData<?>>) expr[14];
			meleeSpeed = (Expression<Number>) expr[15];
		} else if (mark == 13) {
			moveTargetSpeed = (Expression<Number>) expr[16];
			moveTargetRadius = (Expression<Number>) expr[17];
		} else if (mark == 14) {
			nearTarget = (Expression<EntityData<?>>) expr[18];
		} else if (mark == 17) {
			panicSpeed = (Expression<Number>) expr[19];
		} else if (mark == 19) {
			randomWalkSpeed = (Expression<Number>) expr[20];
			randomWalkInterval = (Expression<Timespan>) expr[21];
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
			boolean target = false;
			Object newGoal = null;
			Object nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
			Object goals = ReflectionUtils.getField("goalSelector", entInsent, nmsEnt);
			Object targets = ReflectionUtils.getField("targetSelector", entInsent, nmsEnt);
			if (mark == 0) {
				float radius = avoidRadius.getSingle(e).floatValue();
				double spd1 = avoidSpeed1.getSingle(e).doubleValue();
				double spd2 = avoidSpeed2.getSingle(e).doubleValue();
				EntityData<?> entityData;
				String exprInput = typeToAvoid.toString(e, false);
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
			} else if (mark == 1) {
				newGoal = goalBreakDoor.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 2) {
				double spd = breedSpeed.getSingle(e).doubleValue();
				if (!(nmsEnt.getClass().isAssignableFrom(entAnimal)))
					return;
				newGoal = goalBreed.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 3) {
				newGoal = goalEatGrass.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 4) {
				double spd = fleeSunSpeed.getSingle(e).doubleValue();
				newGoal = goalFleeSun.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 5) {
				newGoal = goalFloat.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 6) {
				double spd = followOwnerSpeed.getSingle(e).doubleValue();
				if (!(nmsEnt.getClass().isAssignableFrom(entTameable)))
					return;
				newGoal = goalFollowOwner.getConstructor(entTameable, double.class, float.class, float.class).newInstance(nmsEnt, spd, 20.0F, 5.0F);
			} else if (mark == 7) {
				double spd = followAdultsSpeed.getSingle(e).doubleValue();
				if (!(nmsEnt.getClass().isAssignableFrom(entAnimal)))
					return;
				newGoal = goalFollowAdults.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 8) {
				target = true;
				EntityData<?> entityData;
				String exprInput = typesToFightBack.toString(e, false);
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
				newGoal = goalReactAttack.getConstructor(entCreature, boolean.class, Class[].class).newInstance(nmsEnt, false, nmsClass);
			} else if (mark == 9) {
				double spd = jumpOnBlockSpeed.getSingle(e).doubleValue();
				if (nmsEnt.getClass() != entOcelot)
					return;
				newGoal = goalJumpOnBlock.getConstructor(entOcelot, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 10) {
				float height = leapHeight.getSingle(e).floatValue();
				newGoal = goalLeapTarget.getConstructor(entInsent, float.class).newInstance(nmsEnt, height);
			} else if (mark == 11) {
				EntityData<?> entityData;
				String exprInput = lookType.toString(e, false);
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
				float radius = lookRadius.getSingle(e).floatValue();
				newGoal = goalLookEntities.getConstructor(entInsent, Class.class, float.class).newInstance(nmsEnt, nmsClass, radius);
			} else if (mark == 12) {
				EntityData<?> entityData;
				String exprInput = meleeTarget.toString(e, false);
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
				double spd = meleeSpeed.getSingle(e).doubleValue();
				newGoal = goalMeleeAttack.getConstructor(entCreature, Class.class, double.class).newInstance(nmsEnt, nmsClass, spd);
			} else if (mark == 13) {
				double spd = moveTargetSpeed.getSingle(e).doubleValue();
				float radius = moveTargetRadius.getSingle(e).floatValue();
				newGoal = goalGotoTarget.getConstructor(entCreature, double.class, float.class).newInstance(nmsEnt, spd, radius);
			} else if (mark == 14) {
				target = true;
				EntityData<?> entityData;
				String exprInput = nearTarget.toString(e, false);
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
				newGoal = goalNearTarget.getConstructor(entCreature, Class.class, boolean.class).newInstance(nmsEnt, nmsClass, false);
			} else if (mark == 15) {
				newGoal = goalOcelotAttack.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 16) {
				newGoal = goalOpenDoors.getConstructor(entInsent, boolean.class).newInstance(nmsEnt);
			}
			if (target) {
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(targets, priority, newGoal);
			} else {
				newGoal = goalSelector.getMethod("a", int.class, goal).invoke(goals, priority, newGoal);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}