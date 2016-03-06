package me.TheBukor.SkStuff.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffSetPathGoal extends Effect {
	private Expression<Integer> goalPriority;
	private Expression<? extends EntityData<?>> typeToAvoid;
	private Expression<Number> avoidRadius;
	private Expression<Number> avoidSpeed;
	private Expression<Number> avoidSpeedNear;
	private Expression<Number> breedSpeed;
	private Expression<Number> fleeSunSpeed;
	private Expression<Number> followOwnerSpeed;
	private Expression<Number> followMinDist;
	private Expression<Number> followMaxDist;
	private Expression<Number> followAdultsSpeed;
	private Expression<? extends EntityData<?>> typesToFightBack;
	private Expression<Boolean> callForHelp;
	private Expression<Number> jumpOnBlockSpeed;
	private Expression<Number> leapHeight;
	private Expression<? extends EntityData<?>> lookType;
	private Expression<Number> lookRadius;
	private Expression<? extends EntityData<?>> meleeTarget;
	private Expression<Number> meleeSpeed;
	private Expression<Boolean> meleeMemorize;
	private Expression<Number> moveTargetSpeed;
	private Expression<Number> moveTargetRadius;
	private Expression<? extends EntityData<?>> nearTarget;
	private Expression<Boolean> checkSight;
	private Expression<Number> panicSpeed;
	private Expression<Number> randomWalkSpeed;
	private Expression<Timespan> randomWalkInterval;
	private Expression<ItemStack> temptItem;
	private Expression<Number> temptSpeed;
	private Expression<Boolean> temptScared;
	private Expression<? extends EntityData<?>> nonTamedTarget;
	private Expression<LivingEntity> entities;

	private int mark;

	private Class<?> craftItemClass = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
	private Class<?> craftLivEnt = ReflectionUtils.getOBCClass("entity.CraftLivingEntity");
	private Class<?> entAnimal = ReflectionUtils.getNMSClass("EntityAnimal");
	private Class<?> entCreature = ReflectionUtils.getNMSClass("EntityCreature");
	private Class<?> entInsent = ReflectionUtils.getNMSClass("EntityInsentient");
	private Class<?> entTameable = ReflectionUtils.getNMSClass("EntityTameableAnimal");
	private Class<?> nmsItemClass = ReflectionUtils.getNMSClass("Item");

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		goalPriority = (Expression<Integer>) expr[0];
		mark = result.mark;
		if (mark == 0) {
			typeToAvoid = (Expression<EntityData<?>>) expr[1];
			avoidRadius = (Expression<Number>) expr[2];
			avoidSpeed = (Expression<Number>) expr[3];
			avoidSpeedNear = (Expression<Number>) expr[4];
		} else if (mark == 2) {
			breedSpeed = (Expression<Number>) expr[5];
		} else if (mark == 4) {
			fleeSunSpeed = (Expression<Number>) expr[6];
		} else if (mark == 6) {
			followOwnerSpeed = (Expression<Number>) expr[7];
			followMinDist = (Expression<Number>) expr[8];
			followMaxDist = (Expression<Number>) expr[9];
		} else if (mark == 7) {
			followAdultsSpeed = (Expression<Number>) expr[10];
		} else if (mark == 8) {
			typesToFightBack = (Expression<EntityData<?>>) expr[11];
			callForHelp = (Expression<Boolean>) expr[12];
		} else if (mark == 9) {
			jumpOnBlockSpeed = (Expression<Number>) expr[13];
		} else if (mark == 10) {
			leapHeight = (Expression<Number>) expr[14];
		} else if (mark == 11) {
			lookType = (Expression<EntityData<?>>) expr[15];
			lookRadius = (Expression<Number>) expr[16];
		} else if (mark == 12) {
			meleeTarget = (Expression<EntityData<?>>) expr[17];
			meleeSpeed = (Expression<Number>) expr[18];
			meleeMemorize = (Expression<Boolean>) expr[19];
		} else if (mark == 13) {
			moveTargetSpeed = (Expression<Number>) expr[20];
			moveTargetRadius = (Expression<Number>) expr[21];
		} else if (mark == 14) {
			nearTarget = (Expression<EntityData<?>>) expr[22];
			checkSight = (Expression<Boolean>) expr[23];
		} else if (mark == 17) {
			panicSpeed = (Expression<Number>) expr[24];
		} else if (mark == 19) {
			randomWalkSpeed = (Expression<Number>) expr[25];
			randomWalkInterval = (Expression<Timespan>) expr[26];
		} else if (mark == 31) {
			temptItem = (Expression<ItemStack>) expr[27];
			temptSpeed = (Expression<Number>) expr[28];
			temptScared = (Expression<Boolean>) expr[29];
		} else if (mark == 32) {
			nonTamedTarget = (Expression<EntityData<?>>) expr[30];
		}
		entities = (Expression<LivingEntity>) expr[31];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "add pathfinder goal to" + entities.toString(e, debug);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		LivingEntity[] ents = entities.getAll(e);
		for (LivingEntity ent : ents) {
			if (ent == null || ent instanceof Player)
				return;
			int priority = (goalPriority == null ? 4 : goalPriority.getSingle(e).intValue());

			if (priority < 0)
				priority = 0;
			else if (priority > 9)
				priority = 9;

			Object obcEnt = craftLivEnt.cast(ent);
			Object nmsEnt = null;
			boolean target = false;
			Object newGoal = null;
			try {
				nmsEnt = entInsent.cast(obcEnt.getClass().getMethod("getHandle").invoke(obcEnt));
				if (mark == 0) {
					float radius = (avoidRadius == null ? 6.0F : avoidRadius.getSingle(e).floatValue());
					double spd = (avoidSpeed == null ? 1.0D : avoidSpeed.getSingle(e).doubleValue());
					double nearSpd = (avoidSpeedNear == null ? 1.2D : avoidSpeedNear.getSingle(e).doubleValue());
					EntityData<?>[] types = typeToAvoid.getAll(e);
					if (ent instanceof Rabbit) {
						Class<?> goalRabbitAvoid = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitAvoidTarget");
						for (EntityData<?> entData : types) {
							if (LivingEntity.class.isAssignableFrom(entData.getType()))
								newGoal = goalRabbitAvoid.getDeclaredConstructor(nmsEnt.getClass(), Class.class, float.class, double.class, double.class).newInstance(nmsEnt, entData.getType(), radius, spd, nearSpd);
						}
					} else {
						Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget");
						for (EntityData<?> entData : types) {
							if (LivingEntity.class.isAssignableFrom(entData.getType()))
								newGoal = goalAvoid.getConstructor(entCreature, Class.class, float.class, double.class, double.class).newInstance(nmsEnt, entData.getType(), radius, spd, nearSpd);
						}
					}
				} else if (mark == 1) {
					Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor");
					newGoal = goalBreakDoor.getConstructor(entInsent).newInstance(nmsEnt);
				} else if (mark == 2) {
					if (!(ent instanceof Animals))
						return;
					double spd = (breedSpeed == null ? 1.0D : breedSpeed.getSingle(e).doubleValue());
					Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed");
					newGoal = goalBreed.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
				} else if (mark == 3) {
					Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile");
					newGoal = goalEatGrass.getConstructor(entInsent).newInstance(nmsEnt);
				} else if (mark == 4) {
					double spd = (fleeSunSpeed == null ? 1.0D : fleeSunSpeed.getSingle(e).doubleValue());
					Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun");
					newGoal = goalFleeSun.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
				} else if (mark == 5) {
					Class<?> goalFloat = ReflectionUtils.getNMSClass("PathfinderGoalFloat");
					newGoal = goalFloat.getConstructor(entInsent).newInstance(nmsEnt);
				} else if (mark == 6) {
					if (!(ent instanceof Tameable))
						return;
					double spd = (followOwnerSpeed == null ? 1.0D : followOwnerSpeed.getSingle(e).doubleValue());
					float minDist = (followMinDist == null ? 3.0F : followMinDist.getSingle(e).floatValue());
					float maxDist = (followMaxDist == null ? 10.0F : followMaxDist.getSingle(e).floatValue());
					Class<?> goalFollowOwner = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner");
					newGoal = goalFollowOwner.getConstructor(entTameable, double.class, float.class, float.class).newInstance(nmsEnt, spd, maxDist, minDist);
				} else if (mark == 7) {
					if (!(ent instanceof Animals))
						return;
					double spd = (followAdultsSpeed == null ? 1.0D : followAdultsSpeed.getSingle(e).doubleValue());
					Class<?> goalFollowAdults = ReflectionUtils.getNMSClass("PathfinderGoalFollowParent");
					newGoal = goalFollowAdults.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
				} else if (mark == 8) {
					target = true;
					boolean callHelp = (callForHelp == null ? false : callForHelp.getSingle(e));
					EntityData<?>[] types = typesToFightBack.getAll(e);
					List<Class<?>> typesClasses = new ArrayList<Class<?>>();
					for (EntityData<?> entData : types) {
						if (LivingEntity.class.isAssignableFrom(entData.getType()))
							typesClasses.add(entData.getType());
					}
					Class<?>[] finalTypes = Arrays.copyOf(typesClasses.toArray(), typesClasses.size(), Class[].class);
					Class<?> goalReactAttack = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget");
					newGoal = goalReactAttack.getConstructor(entCreature, boolean.class, Class[].class).newInstance(nmsEnt, callHelp, finalTypes);
				} else if (mark == 9) {
					if (!(ent instanceof Ocelot))
						return;
					double spd = (jumpOnBlockSpeed == null ? 1.0D : jumpOnBlockSpeed.getSingle(e).doubleValue());
					Class<?> goalJumpOnBlock = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock");
					newGoal = goalJumpOnBlock.getConstructor(nmsEnt.getClass(), double.class).newInstance(nmsEnt, spd);
				} else if (mark == 10) {
					float height = (leapHeight == null ? 0.4F : leapHeight.getSingle(e).floatValue());
					Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget");
					newGoal = goalLeapTarget.getConstructor(entInsent, float.class).newInstance(nmsEnt, height);
				} else if (mark == 11) {
					float radius = (lookRadius == null ? 1.0F : lookRadius.getSingle(e).floatValue());
					EntityData<?>[] types = lookType.getAll(e);
					Class<?> goalLookEntities = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer");
					for (EntityData<?> entData : types) {
						if (LivingEntity.class.isAssignableFrom(entData.getType()))
							newGoal = goalLookEntities.getConstructor(entInsent, Class.class, float.class).newInstance(nmsEnt, entData.getType(), radius);
					}
				} else if (mark == 12) {
					EntityData<?>[] types = meleeTarget.getAll(e);
					if (ent instanceof Spider) {
						Class<?> goalSpiderMelee = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderMeleeAttack");
						for (EntityData<?> entData : types) {
							if (LivingEntity.class.isAssignableFrom(entData.getType()))
								newGoal = ReflectionUtils.getConstructor(goalSpiderMelee, nmsEnt.getClass(), Class.class).newInstance(nmsEnt, entData.getType());
						}
					} else {
						double spd = (meleeSpeed == null ? 1.0D : meleeSpeed.getSingle(e).doubleValue());
						boolean memorize = (meleeMemorize == null ? false : meleeMemorize.getSingle(e));
						Class<?> goalMeleeAttack = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack");
						for (EntityData<?> entData : types) {
							if (LivingEntity.class.isAssignableFrom(entData.getType()))
								newGoal = goalMeleeAttack.getConstructor(entCreature, Class.class, double.class, boolean.class).newInstance(nmsEnt, entData.getType(), spd, memorize);
						}
					}
				} else if (mark == 13) {
					double spd = (moveTargetSpeed == null ? 1.0D : moveTargetSpeed.getSingle(e).doubleValue());
					float radius = (moveTargetRadius == null ? 32.0F : moveTargetRadius.getSingle(e).floatValue());
					Class<?> goalGotoTarget = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget");
					newGoal = goalGotoTarget.getConstructor(entCreature, double.class, float.class).newInstance(nmsEnt, spd, radius);
				} else if (mark == 14) {
					target = true;
					EntityData<?>[] types = nearTarget.getAll(e);
					if (ent instanceof Spider) {
						Class<?> goalSpiderNearTarget = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderNearestAttackableTarget");
						for (EntityData<?> entData : types) {
							if (LivingEntity.class.isAssignableFrom(entData.getType()))
								newGoal = ReflectionUtils.getConstructor(goalSpiderNearTarget, nmsEnt.getClass(), Class.class).newInstance(nmsEnt, entData.getType());
						}
					} else {
						boolean checkView = (checkSight == null ? true : checkSight.getSingle(e));
						Class<?> goalNearTarget = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget");
						for (EntityData<?> entData : types) {
							if (LivingEntity.class.isAssignableFrom(entData.getType()))
								newGoal = goalNearTarget.getConstructor(entCreature, Class.class, boolean.class).newInstance(nmsEnt, entData.getType(), checkView);
						}
					}
				} else if (mark == 15) {
					Class<?> goalOcelotAttack = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack");
					newGoal = goalOcelotAttack.getConstructor(entInsent).newInstance(nmsEnt);
				} else if (mark == 16) {
					Class<?> goalOpenDoors = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor");
					newGoal = goalOpenDoors.getConstructor(entInsent, boolean.class).newInstance(nmsEnt, false);
				} else if (mark == 17) {
					double spd = (panicSpeed == null ? 1.25D : panicSpeed.getSingle(e).doubleValue());
					if (ent instanceof Rabbit) {
						Class<?> goalRabbitPanic = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitPanic");
						newGoal = goalRabbitPanic.getDeclaredConstructor(nmsEnt.getClass(), double.class).newInstance(nmsEnt, spd);
					} else {
						Class<?> goalPanic = ReflectionUtils.getNMSClass("PathfinderGoalPanic");
						newGoal = goalPanic.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
					}
				} else if (mark == 18) {
					Class<?> goalRandomLook = ReflectionUtils.getNMSClass("PathfinderGoalRandomLookaround");
					newGoal = goalRandomLook.getConstructor(entInsent).newInstance(nmsEnt);
				} else if (mark == 19) {
					double spd = (randomWalkSpeed == null ? 1.0D : randomWalkSpeed.getSingle(e).doubleValue());
					int interval = (randomWalkInterval == null ? 120 : randomWalkInterval.getSingle(e).getTicks());
					Class<?> goalWander = ReflectionUtils.getNMSClass("PathfinderGoalRandomStroll");
					newGoal = goalWander.getConstructor(entCreature, double.class, int.class).newInstance(nmsEnt, spd, interval);
				} else if (mark == 20) {
					if (!(ent instanceof Tameable))
						return;
					Class<?> goalSit = ReflectionUtils.getNMSClass("PathfinderGoalSit");
					newGoal = goalSit.getConstructor(entTameable).newInstance(nmsEnt);
				} else if (mark == 21) {
					if (!(ent instanceof Creeper))
						return;
					Class<?> goalSwell = ReflectionUtils.getNMSClass("PathfinderGoalSwell");
					newGoal = goalSwell.getConstructor(nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 22) {
					if (!(ent instanceof Squid))
						return;
					Class<?> goalSquid = ReflectionUtils.getNMSClass("EntitySquid$PathfinderGoalSquid");
					newGoal = ReflectionUtils.getConstructor(goalSquid, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 23) {
					if (ent instanceof Blaze) {
						Class<?> goalBlazeFireball = ReflectionUtils.getNMSClass("EntityBlaze$PathfinderGoalBlazeFireball");
						newGoal = ReflectionUtils.getConstructor(goalBlazeFireball, nmsEnt.getClass()).newInstance(nmsEnt);
					} else if (ent instanceof Ghast) {
						Class<?> goalGhastFireball = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastAttackTarget");
						newGoal = ReflectionUtils.getConstructor(goalGhastFireball, nmsEnt.getClass()).newInstance(nmsEnt);
					}
				} else if (mark == 24) {
					if (!(ent instanceof Silverfish))
						return;
					Class<?> goalHideInBlock = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishHideInBlock");
					newGoal = ReflectionUtils.getConstructor(goalHideInBlock, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 25) {
					if (!(ent instanceof Silverfish))
						return;
					Class<?> goalWakeSilverfish = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishWakeOthers");
					newGoal = ReflectionUtils.getConstructor(goalWakeSilverfish, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 26) {
					if (!(ent instanceof Enderman))
						return;
					Class<?> goalPickBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPickupBlock");
					newGoal = ReflectionUtils.getConstructor(goalPickBlocks, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 27) {
					if (!(ent instanceof Enderman))
						return;
					Class<?> goalPlaceBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPlaceBlock");
					newGoal = ReflectionUtils.getConstructor(goalPlaceBlocks, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 28) {
					if (!(ent instanceof Enderman))
						return;
					target = true;
					Class<?> goalAttackLooker = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalPlayerWhoLookedAtTarget");
					newGoal = ReflectionUtils.getConstructor(goalAttackLooker, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 29) {
					if (!(ent instanceof Ghast))
						return;
					Class<?> goalGhastMoveTarget = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastMoveTowardsTarget");
					newGoal = ReflectionUtils.getConstructor(goalGhastMoveTarget, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 30) {
					if (!(ent instanceof Ghast))
						return;
					Class<?> goalGhastIdleMove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastIdleMove");
					newGoal = ReflectionUtils.getConstructor(goalGhastIdleMove, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 31) {
					ItemStack itemStack = temptItem.getSingle(e);
					if (itemStack.getType() == Material.AIR || itemStack == null)
						return;
					Object nmsItemStack = craftItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(itemStack, itemStack);
					Object nmsItem = nmsItemStack.getClass().getMethod("getItem").invoke(nmsItemStack);
					double spd = (temptSpeed == null ? 1.0D : temptSpeed.getSingle(e).doubleValue());
					boolean scared = (temptScared == null ? false : temptScared.getSingle(e));
					Class<?> goalTempt = ReflectionUtils.getNMSClass("PathfinderGoalTempt");
					newGoal = goalTempt.getConstructor(entCreature, double.class, nmsItemClass, boolean.class).newInstance(nmsEnt, spd, nmsItem, scared);
				} else if (mark == 32) {
					if (!(ent instanceof Tameable))
						return;
					target = true;
					EntityData<?>[] types = nonTamedTarget.getAll(e);
					Class<?> goalTargetNonTamed = ReflectionUtils.getNMSClass("PathfinderGoalRandomTargetNonTamed");
					for (EntityData<?> entData : types) {
						if (LivingEntity.class.isAssignableFrom(entData.getType()))
							newGoal = goalTargetNonTamed.getConstructor(entTameable, Class.class, boolean.class, Predicate.class).newInstance(nmsEnt, entData.getType(), false, Predicates.alwaysTrue());
					}
				} else if (mark == 33) {
					if (!(ent instanceof Guardian))
						return;
					Class<?> goalGuardianAttack = ReflectionUtils.getNMSClass("EntityGuardian$PathfinderGoalGuardianAttack");
					newGoal = ReflectionUtils.getConstructor(goalGuardianAttack, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 34) {
					if (!(ent instanceof PigZombie))
						return;
					target = true;
					Class<?> goalAnger = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAnger");
					newGoal = ReflectionUtils.getConstructor(goalAnger, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 35) {
					if (!(ent instanceof PigZombie))
						return;
					target = true;
					Class<?> goalAngerOther = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAngerOther");
					newGoal = ReflectionUtils.getConstructor(goalAngerOther, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 36) {
					if (!(ent instanceof Rabbit))
						return;
					Class<?> goalEatCarrots = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalEatCarrots");
					newGoal = ReflectionUtils.getConstructor(goalEatCarrots, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 37) {
					if (!(ent instanceof Rabbit))
						return;
					Class<?> goalRabbitAttack = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalKillerRabbitMeleeAttack");
					newGoal = ReflectionUtils.getConstructor(goalRabbitAttack, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 38) {
					if (!(ent instanceof Slime))
						return;
					Class<?> goalJump = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomJump");
					newGoal = ReflectionUtils.getConstructor(goalJump, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 39) {
					if (!(ent instanceof Slime))
						return;
					Class<?> goalRandomDir = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomDirection");
					newGoal = ReflectionUtils.getConstructor(goalRandomDir, nmsEnt.getClass()).newInstance(nmsEnt);
				} else if (mark == 40) {
					if (!(ent instanceof Slime))
						return;
					Class<?> goalSlimeWander = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeIdle");
					newGoal = ReflectionUtils.getConstructor(goalSlimeWander, nmsEnt.getClass()).newInstance(nmsEnt);
				}
				if (newGoal == null)
					return;
				SkStuff.getNMSMethods().addPathfinderGoal(nmsEnt, priority, newGoal, target);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
				ex.printStackTrace();
			}
		}
	}
}