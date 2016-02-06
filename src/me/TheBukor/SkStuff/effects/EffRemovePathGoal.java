package me.TheBukor.SkStuff.effects;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
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

public class EffRemovePathGoal extends Effect {
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
	private Expression<Number> panicSpeed;
	private Expression<Number> randomWalkSpeed;
	private Expression<Timespan> randomWalkInterval;
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

	private Class<?> entAnimal = ReflectionUtils.getNMSClass("EntityAnimal", false);
	private Class<?> entCreature = ReflectionUtils.getNMSClass("EntityCreature", false);
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient", false);
	private Class<?> entOcelot = ReflectionUtils.getNMSClass("EntityOcelot", false);
	private Class<?> entTameable = ReflectionUtils.getNMSClass("EntityTameableAnimal", false);
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		mark = result.mark;
		if (mark == 0) {
			typeToAvoid = (Expression<EntityData<?>>) expr[0];
			avoidRadius = (Expression<Number>) expr[1];
			avoidSpeed1 = (Expression<Number>) expr[2];
			avoidSpeed2 = (Expression<Number>) expr[3];
		} else if (mark == 2) {
			breedSpeed = (Expression<Number>) expr[4];
		} else if (mark == 4) {
			fleeSunSpeed = (Expression<Number>) expr[5];
		} else if (mark == 6) {
			followOwnerSpeed = (Expression<Number>) expr[6];
		} else if (mark == 7) {
			followAdultsSpeed = (Expression<Number>) expr[7];
		} else if (mark == 8) {
			typesToFightBack = (Expression<EntityData<?>>) expr[8];
		} else if (mark == 9) {
			jumpOnBlockSpeed = (Expression<Number>) expr[9];
		} else if (mark == 10) {
			leapHeight = (Expression<Number>) expr[10];
		} else if (mark == 11) {
			lookType = (Expression<EntityData<?>>) expr[11];
			lookRadius = (Expression<Number>) expr[12];
		} else if (mark == 12) {
			meleeTarget = (Expression<EntityData<?>>) expr[13];
			meleeSpeed = (Expression<Number>) expr[14];
		} else if (mark == 13) {
			moveTargetSpeed = (Expression<Number>) expr[15];
			moveTargetRadius = (Expression<Number>) expr[16];
		} else if (mark == 14) {
			nearTarget = (Expression<EntityData<?>>) expr[17];
		} else if (mark == 17) {
			panicSpeed = (Expression<Number>) expr[18];
		} else if (mark == 19) {
			randomWalkSpeed = (Expression<Number>) expr[19];
			randomWalkInterval = (Expression<Timespan>) expr[20];
		}
		entity = (Expression<LivingEntity>) expr[21];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "remove pathfind goal of " + entity.toString(e, false);
	}

	@SuppressWarnings({ "unused", "deprecation" })
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
			Object newGoal = null;
			Object toRemove = null;
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
				Class<?>[] nmsClass = new Class<?>[] { ReflectionUtils.getNMSClass(className, false) };
				if (nmsClass[0] == null)
					return;
				newGoal = goalReactAttack.getConstructor(entCreature, boolean.class, Class[].class).newInstance(nmsEnt, false, nmsClass[0]);
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
				newGoal = goalMeleeAttack.getConstructor(entCreature, Class.class, double.class, boolean.class).newInstance(nmsEnt, nmsClass, spd, false);
			} else if (mark == 13) {
				double spd = moveTargetSpeed.getSingle(e).doubleValue();
				float radius = moveTargetRadius.getSingle(e).floatValue();
				newGoal = goalGotoTarget.getConstructor(entCreature, double.class, float.class).newInstance(nmsEnt, spd, radius);
			} else if (mark == 14) {
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
				newGoal = goalOpenDoors.getConstructor(entInsent, boolean.class).newInstance(nmsEnt, false);
			} else if (mark == 17) {
				double spd = panicSpeed.getSingle(e).doubleValue();
				newGoal = goalPanic.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 18) {
				newGoal = goalRandomLook.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 19) {
				double spd = randomWalkSpeed.getSingle(e).doubleValue();
				int interval = randomWalkInterval.getSingle(e).getTicks();
				newGoal = goalWander.getConstructor(entCreature, double.class, int.class).newInstance(nmsEnt, spd, interval);
			} else if (mark == 20) {
				if (!(nmsEnt.getClass().isAssignableFrom(entTameable))) {
					Bukkit.broadcastMessage("\u00A7c" + ent.getType().toString() + " is not a tameable animal - \u00A7e[DEBUG MESSAGE]");
					return;
				}
				newGoal = goalSit.getConstructor(entTameable).newInstance(nmsEnt);
			} else if (mark == 21) {
				if (!(ent instanceof Creeper))  {
					Bukkit.broadcastMessage("\u00A7c" + ent.getType().toString() + " is not a creeper - \u00A7e[DEBUG MESSAGE]");
					return;
				}
				newGoal = goalSwell.getConstructor(nmsEnt.getClass()).newInstance(nmsEnt);
			}
			Iterator<?> goals = ((List<?>) ReflectionUtils.getField("b", goalSelectorClass, goalSelector)).iterator();
			while (goals.hasNext()) {
				Object o = goals.next();
				Bukkit.broadcastMessage("\u00A7eClass from \'a\' field of iterated goal: \u00A76" + ReflectionUtils.getField("a", o.getClass(), o).getClass());
				Bukkit.broadcastMessage("\u00A7eClass from \'newGoal\': \u00A76" + newGoal.getClass());
				Bukkit.broadcastMessage("\n\u00A79String form of \'a\' field of iterated goal: \u00A7b" + ReflectionUtils.getField("a", o.getClass(), o));
				Bukkit.broadcastMessage("\u00A79String form of \'newGoal\': \u00A7b" + newGoal);
				if (ReflectionUtils.getField("a", o.getClass(), o).getClass() == newGoal.getClass()) {
					goals.remove();
					Bukkit.broadcastMessage("\u00A7aClasses were the same. Just need to know how to check values");
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}