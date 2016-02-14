package me.TheBukor.SkStuff.effects;

import java.lang.reflect.Constructor;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;

import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffSetPathGoal extends Effect {
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
	private Expression<Number> panicSpeed;
	private Expression<Number> randomWalkSpeed;
	private Expression<Timespan> randomWalkInterval;
	private Expression<LivingEntity> entity;

	private int mark;

	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");
	private Class<?> entAnimal = ReflectionUtils.getNMSClass("EntityAnimal", false);
	private Class<?> entCreature = ReflectionUtils.getNMSClass("EntityCreature", false);
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient", false);
	private Class<?> entTameable = ReflectionUtils.getNMSClass("EntityTameableAnimal", false);

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
	public String toString(@Nullable Event e, boolean debug) {
		return "add pathfinder goal to entity";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		int priority = 0;
		if (goalPriority != null) {
			priority = goalPriority.getSingle(e).intValue();
		} else {
			priority = 4;
		}
		if (priority < 1) {
			priority = 1;
		} else if (priority > 9) {
			priority = 9;
		}
		LivingEntity ent = entity.getSingle(e);
		if (ent == null ||ent instanceof Player)
			return;
		Object obcEnt = craftLivEnt.cast(ent);
		try {
			Object nmsEnt = null;
			boolean target = false;
			Object newGoal = null;
			nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
			Object goals = ReflectionUtils.getField("goalSelector", entInsent, nmsEnt);
			Object targets = ReflectionUtils.getField("targetSelector", entInsent, nmsEnt);
			if (mark == 0) {
				Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget", false);
				float radius = avoidRadius.getSingle(e).floatValue();
				double spd1 = avoidSpeed1.getSingle(e).doubleValue();
				double spd2 = avoidSpeed2.getSingle(e).doubleValue();
				EntityData<?> entityData;
				String exprInput = typeToAvoid.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				if (!LivingEntity.class.isAssignableFrom(entityData.getType())) {
					return;
				}
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className, false);
				if (nmsClass == null)
					return;
				newGoal = goalAvoid.getConstructor(entCreature, Class.class, float.class, double.class, double.class).newInstance(nmsEnt, nmsClass, radius, spd1, spd2);
			} else if (mark == 1) {
				Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor", false);
				newGoal = goalBreakDoor.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 2) {
				Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed", false);
				double spd = breedSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Animals))
					return;
				newGoal = goalBreed.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 3) {
				Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile", false);
				newGoal = goalEatGrass.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 4) {
				Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun", false);
				double spd = fleeSunSpeed.getSingle(e).doubleValue();
				newGoal = goalFleeSun.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 5) {
				Class<?> goalFloat = ReflectionUtils.getNMSClass("PathfinderGoalFloat", false);
				newGoal = goalFloat.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 6) {
				Class<?> goalFollowOwner = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner", false);
				double spd = followOwnerSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Tameable))
					return;
				newGoal = goalFollowOwner.getConstructor(entTameable, double.class, float.class, float.class).newInstance(nmsEnt, spd, 20.0F, 5.0F);
			} else if (mark == 7) {
				Class<?> goalFollowAdults = ReflectionUtils.getNMSClass("PathfinderGoalFollowParent", false);
				double spd = followAdultsSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Animals))
					return;
				newGoal = goalFollowAdults.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 8) {
				target = true;
				Class<?> goalReactAttack = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget", false);
				EntityData<?> entityData;
				String exprInput = typesToFightBack.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				className = "Entity" + className;
				Class<?>[] nmsClass = new Class<?>[] { ReflectionUtils.getNMSClass(className, false) };
				if (nmsClass[0] == null)
					return;
				newGoal = goalReactAttack.getConstructor(entCreature, boolean.class, Class[].class).newInstance(nmsEnt, false, nmsClass[0]);
			} else if (mark == 9) {
				Class<?> goalJumpOnBlock = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock", false);
				double spd = jumpOnBlockSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Ocelot))
					return;
				newGoal = goalJumpOnBlock.getConstructor(nmsEnt.getClass(), double.class).newInstance(nmsEnt, spd);
			} else if (mark == 10) {
				Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget", false);
				float height = leapHeight.getSingle(e).floatValue();
				newGoal = goalLeapTarget.getConstructor(entInsent, float.class).newInstance(nmsEnt, height);
			} else if (mark == 11) {
				Class<?> goalLookEntities = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer", false);
				EntityData<?> entityData;
				String exprInput = lookType.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
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
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				if (ent instanceof Spider) {
					Class<?>[] classes = nmsEnt.getClass().getDeclaredClasses();
					Class<?> clazz = null;
					for (Class<?> c : classes) {
						Bukkit.broadcastMessage("Class name: \u00A7e" + c.getName());
						if (c.getName().equals("PathfinderGoalSpiderMeleeAttack"))
							clazz = c;
					}
					Constructor<?>[] constructors = clazz.getDeclaredConstructors();
					Constructor<?> constr = null;
					for (Constructor<?> constructor : constructors) {
						Bukkit.broadcastMessage("Constructor name: \u00A7a" + constructor.getName());
						if (constructor.getName().equals("PathfinderGoalSpiderMeleeAttack"))
							constr = constructor;
					}
					if (constr == null)
						return;
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt, entityData);
				} else {
					Class<?> goalMeleeAttack = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack", false);
					String className = entityData.getType().getSimpleName();
					if (className.equals("HumanEntity"))
						className = "Human";
					className = "Entity" + className;
					Class<?> nmsClass = ReflectionUtils.getNMSClass(className, false);
					if (nmsClass == null)
						return;
					double spd = meleeSpeed.getSingle(e).doubleValue();
					newGoal = goalMeleeAttack.getConstructor(entCreature, Class.class, double.class, boolean.class).newInstance(nmsEnt, nmsClass, spd, false);
				}
			} else if (mark == 13) {
				Class<?> goalGotoTarget = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget", false);
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
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				if (ent instanceof Spider) {
					Class<?> goalSpiderNearTarget = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderNearestAttackableTarget", false);
					Constructor<?> constr = goalSpiderNearTarget.getDeclaredConstructor(nmsEnt.getClass(), Class.class);
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt, entityData);
				} else {
					Class<?> goalNearTarget = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget", false);
					String className = entityData.getType().getSimpleName();
					if (className.equals("HumanEntity"))
						className = "Human";
					className = "Entity" + className;
					Class<?> nmsClass = ReflectionUtils.getNMSClass(className, false);
					if (nmsClass == null)
						return;
					newGoal = goalNearTarget.getConstructor(entCreature, Class.class, boolean.class).newInstance(nmsEnt, nmsClass, false);
				}
			} else if (mark == 15) {
				Class<?> goalOcelotAttack = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack", false);
				newGoal = goalOcelotAttack.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 16) {
				Class<?> goalOpenDoors = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor", false);
				newGoal = goalOpenDoors.getConstructor(entInsent, boolean.class).newInstance(nmsEnt, false);
			} else if (mark == 17) {
				Class<?> goalPanic = ReflectionUtils.getNMSClass("PathfinderGoalPanic", false);
				double spd = panicSpeed.getSingle(e).doubleValue();
				newGoal = goalPanic.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 18) {
				Class<?> goalRandomLook = ReflectionUtils.getNMSClass("PathfinderGoalRandomLookaround", false);
				newGoal = goalRandomLook.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 19) {
				Class<?> goalWander = ReflectionUtils.getNMSClass("PathfinderGoalRandomStroll", false);
				double spd = randomWalkSpeed.getSingle(e).doubleValue();
				int interval = randomWalkInterval.getSingle(e).getTicks();
				newGoal = goalWander.getConstructor(entCreature, double.class, int.class).newInstance(nmsEnt, spd, interval);
			} else if (mark == 20) {
				Class<?> goalSit = ReflectionUtils.getNMSClass("PathfinderGoalSit", false);
				if (!(ent instanceof Tameable))
					return;
				newGoal = goalSit.getConstructor(entTameable).newInstance(nmsEnt);
			} else if (mark == 21) {
				Class<?> goalSwell = ReflectionUtils.getNMSClass("PathfinderGoalSwell", false);
				if (!(ent instanceof Creeper))
					return;
				newGoal = goalSwell.getConstructor(nmsEnt.getClass()).newInstance(nmsEnt);
			} else if (mark == 22) {
				Class<?> goalSquid = ReflectionUtils.getNMSClass("EntitySquid$PathfinderGoalSquid", false);
				if (!(ent instanceof Squid))
					return;
				Constructor<?> constr = goalSquid.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 23) {
				if (ent instanceof Blaze) {
					Class<?> goalBlazeFireball = ReflectionUtils.getNMSClass("EntityBlaze$PathfinderGoalBlazeFireball", false);
					Constructor<?> constr = goalBlazeFireball.getDeclaredConstructor(nmsEnt.getClass());
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt);
				} else if (ent instanceof Ghast) {
					Class<?> goalGhastFireball = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastAttackTarget", false);
					Constructor<?> constr = goalGhastFireball.getDeclaredConstructor(nmsEnt.getClass());
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt);
				}
			} else if (mark == 24) {
				Class<?> goalHideInBlock = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishHideInBlocks", false);
				if (!(ent instanceof Silverfish))
					return;
				Constructor<?> constr = goalHideInBlock.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 25) {
				Class<?> goalWakeSilverfish = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishWakeOthers", false);
				if (!(ent instanceof Silverfish))
					return;
				Constructor<?> constr = goalWakeSilverfish.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 26) {
				Class<?> goalPickBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPickupBlock", false);
				if (!(ent instanceof Enderman))
					return;
				Constructor<?> constr = goalPickBlocks.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 27) {
				Class<?> goalPlaceBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPlaceBlock", false);
				if (!(ent instanceof Enderman))
					return;
				Constructor<?> constr = goalPlaceBlocks.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 28) {
				target = true;
				Class<?> goalAttackLooker = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalPlayerWhoLookedAtTarget", false);
				if (!(ent instanceof Enderman))
					return;
				Constructor<?> constr = goalAttackLooker.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 29) {
				Class<?> goalGhastMoveTarget = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastMoveTowardsTarget", false);
				if (!(ent instanceof Ghast))
					return;
				Constructor<?> constr = goalGhastMoveTarget.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 30) {
				Class<?> goalGhastIdleMove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastIdleMove", false);
				if (!(ent instanceof Ghast))
					return;
				Constructor<?> constr = goalGhastIdleMove.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 31) {
				// TODO: Add more goal/target selectors

				/* Classes that have their own pathfinder goals:
				 * Rabbit, 3 goals, 2 adapted copies, 1 new (eat carrot crops)
				 * Slime, 4 goals, random jump, go to near player, go in random direction and idle.
				 * ZPigMan, 2 goals, anger and anger other (adapted HurtByTarget to work with Anger tag)
				 */
				
				/* Goals to add:
				 * Tempt - Mob follows you with a certain item in hand (e.g cow follows wheat)
				 * AttackNonTamed - Used by ocelots, to attack chickens (maybe for wolves to attack sheep to?)
				 */
			}
			if (newGoal == null)
				return;
			Class<?> goal = ReflectionUtils.getNMSClass("PathfinderGoal", false);
			Class<?> goalSelector = ReflectionUtils.getNMSClass("PathfinderGoalSelector", false);
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