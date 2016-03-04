package me.TheBukor.SkStuff.effects;

import java.lang.reflect.Constructor;

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
import ch.njol.skript.localization.Language;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.TheBukor.SkStuff.SkStuff;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class EffSetPathGoal extends Effect {
	private Expression<Integer> goalPriority;
	private Expression<EntityData<?>> typeToAvoid;
	private Expression<Number> avoidRadius;
	private Expression<Number> avoidSpeed;
	private Expression<Number> avoidSpeedNear;
	private Expression<Number> breedSpeed;
	private Expression<Number> fleeSunSpeed;
	private Expression<Number> followOwnerSpeed;
	private Expression<Number> followMinDist;
	private Expression<Number> followMaxDist;
	private Expression<Number> followAdultsSpeed;
	private Expression<EntityData<?>> typesToFightBack;
	private Expression<Boolean> callForHelp;
	private Expression<Number> jumpOnBlockSpeed;
	private Expression<Number> leapHeight;
	private Expression<EntityData<?>> lookType;
	private Expression<Number> lookRadius;
	private Expression<EntityData<?>> meleeTarget;
	private Expression<Number> meleeSpeed;
	private Expression<Boolean> meleeMemorize;
	private Expression<Number> moveTargetSpeed;
	private Expression<Number> moveTargetRadius;
	private Expression<EntityData<?>> nearTarget;
	private Expression<Boolean> checkSight;
	private Expression<Number> panicSpeed;
	private Expression<Number> randomWalkSpeed;
	private Expression<Timespan> randomWalkInterval;
	private Expression<ItemStack> temptItem;
	private Expression<Number> temptSpeed;
	private Expression<Boolean> temptScared;
	private Expression<EntityData<?>> nonTamedTarget;
	private Expression<LivingEntity> entity;

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
		entity = (Expression<LivingEntity>) expr[31];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "add pathfinder goal to" + entity.toString(e, debug);
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
		if (priority < 0) {
			priority = 0;
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
			if (mark == 0) {
				boolean wasLocal = Language.setUseLocal(false);
				float radius = 6.0F;
				double spd = 1.0D;
				double nearSpd = 1.2D;
				if (avoidRadius != null)
					radius = avoidRadius.getSingle(e).floatValue();
				if (avoidSpeed != null)
					spd = avoidSpeed.getSingle(e).doubleValue();
				if (avoidSpeedNear != null)
					nearSpd = avoidSpeedNear.getSingle(e).doubleValue();
				EntityData<?> entityData;
				String exprInput = typeToAvoid.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				try {
					entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				} finally {
					Language.setUseLocal(wasLocal);
				}
				if (!LivingEntity.class.isAssignableFrom(entityData.getType())) {
					return;
				}
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				else if (className.equals("LivingEntity"))
					className = "Living";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className);
				if (nmsClass == null)
					return;
				if (ent instanceof Rabbit) {
					Class<?> goalRabbitAvoid = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalRabbitAvoidTarget");
					newGoal = goalRabbitAvoid.getDeclaredConstructor(nmsEnt.getClass(), Class.class, float.class, double.class, double.class).newInstance(nmsEnt, nmsClass, radius, spd, nearSpd);
				} else {
					Class<?> goalAvoid = ReflectionUtils.getNMSClass("PathfinderGoalAvoidTarget");
					newGoal = goalAvoid.getConstructor(entCreature, Class.class, float.class, double.class, double.class).newInstance(nmsEnt, nmsClass, radius, spd, nearSpd);
				}
			} else if (mark == 1) {
				Class<?> goalBreakDoor = ReflectionUtils.getNMSClass("PathfinderGoalBreakDoor");
				newGoal = goalBreakDoor.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 2) {
				Class<?> goalBreed = ReflectionUtils.getNMSClass("PathfinderGoalBreed");
				double spd = 1.0D;
				if (breedSpeed != null)
					spd = breedSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Animals))
					return;
				newGoal = goalBreed.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 3) {
				Class<?> goalEatGrass = ReflectionUtils.getNMSClass("PathfinderGoalEatTile");
				newGoal = goalEatGrass.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 4) {
				Class<?> goalFleeSun = ReflectionUtils.getNMSClass("PathfinderGoalFleeSun");
				double spd = 1.0D;
				if (fleeSunSpeed != null)
					spd = fleeSunSpeed.getSingle(e).doubleValue();
				newGoal = goalFleeSun.getConstructor(entCreature, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 5) {
				Class<?> goalFloat = ReflectionUtils.getNMSClass("PathfinderGoalFloat");
				newGoal = goalFloat.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 6) {
				Class<?> goalFollowOwner = ReflectionUtils.getNMSClass("PathfinderGoalFollowOwner");
				double spd = 1.0D;
				if (followOwnerSpeed != null)
					spd = followOwnerSpeed.getSingle(e).doubleValue();
				float minDist = 3.0F;
				if (followMinDist != null)
					minDist = followMinDist.getSingle(e).floatValue();
				float maxDist = 10.0F;
				if (followMaxDist != null)
					maxDist = followMaxDist.getSingle(e).floatValue();
				if (!(ent instanceof Tameable))
					return;
				newGoal = goalFollowOwner.getConstructor(entTameable, double.class, float.class, float.class).newInstance(nmsEnt, spd, maxDist, minDist);
			} else if (mark == 7) {
				Class<?> goalFollowAdults = ReflectionUtils.getNMSClass("PathfinderGoalFollowParent");
				double spd = 1.0D;
				if (followAdultsSpeed != null)
					spd = followAdultsSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Animals))
					return;
				newGoal = goalFollowAdults.getConstructor(entAnimal, double.class).newInstance(nmsEnt, spd);
			} else if (mark == 8) {
				boolean wasLocal = Language.setUseLocal(false);
				target = true;
				boolean callHelp = false;
				Class<?> goalReactAttack = ReflectionUtils.getNMSClass("PathfinderGoalHurtByTarget");
				EntityData<?> entityData;
				String exprInput = typesToFightBack.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				try {
					entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				} finally {
					Language.setUseLocal(wasLocal);
				}
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				else if (className.equals("LivingEntity"))
					className = "Living";
				className = "Entity" + className;
				Class<?>[] nmsClass = new Class<?>[] { ReflectionUtils.getNMSClass(className) };
				if (nmsClass[0] == null)
					return;
				if (callForHelp != null)
					callHelp = callForHelp.getSingle(e);
				newGoal = goalReactAttack.getConstructor(entCreature, boolean.class, Class[].class).newInstance(nmsEnt, callHelp, nmsClass);
			} else if (mark == 9) {
				Class<?> goalJumpOnBlock = ReflectionUtils.getNMSClass("PathfinderGoalJumpOnBlock");
				double spd = 1.0D;
				if (jumpOnBlockSpeed != null)
					spd = jumpOnBlockSpeed.getSingle(e).doubleValue();
				if (!(ent instanceof Ocelot))
					return;
				newGoal = goalJumpOnBlock.getConstructor(nmsEnt.getClass(), double.class).newInstance(nmsEnt, spd);
			} else if (mark == 10) {
				Class<?> goalLeapTarget = ReflectionUtils.getNMSClass("PathfinderGoalLeapAtTarget");
				float height = 0.4F;
				if (leapHeight != null)
					height = leapHeight.getSingle(e).floatValue();
				newGoal = goalLeapTarget.getConstructor(entInsent, float.class).newInstance(nmsEnt, height);
			} else if (mark == 11) {
				boolean wasLocal = Language.setUseLocal(false);
				Class<?> goalLookEntities = ReflectionUtils.getNMSClass("PathfinderGoalLookAtPlayer");
				EntityData<?> entityData;
				String exprInput = lookType.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				try {
					entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				} finally {
					Language.setUseLocal(wasLocal);
				}
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				else if (className.equals("LivingEntity"))
					className = "Living";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className);
				if (nmsClass == null)
					return;
				float radius = 1.0F;
				if (lookRadius != null)
					radius = lookRadius.getSingle(e).floatValue();
				newGoal = goalLookEntities.getConstructor(entInsent, Class.class, float.class).newInstance(nmsEnt, nmsClass, radius);
			} else if (mark == 12) {
				boolean wasLocal = Language.setUseLocal(false);
				EntityData<?> entityData;
				String exprInput = meleeTarget.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				try {
					entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				} finally {
					Language.setUseLocal(wasLocal);
				}
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				else if (className.equals("LivingEntity"))
					className = "Living";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className);
				if (nmsClass == null)
					return;
				if (ent instanceof Spider) {
					Class<?> goalSpiderMelee = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderMeleeAttack");
					Constructor<?> constr = goalSpiderMelee.getDeclaredConstructor(nmsEnt.getClass(), Class.class);
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt, nmsClass);
				} else {
					Class<?> goalMeleeAttack = ReflectionUtils.getNMSClass("PathfinderGoalMeleeAttack");
					double spd = 1.0D;
					if (meleeSpeed != null)
						spd = meleeSpeed.getSingle(e).doubleValue();
					boolean memorize = false;
					if (meleeMemorize != null)
						memorize = meleeMemorize.getSingle(e);
					newGoal = goalMeleeAttack.getConstructor(entCreature, Class.class, double.class, boolean.class).newInstance(nmsEnt, nmsClass, spd, memorize);
				}
			} else if (mark == 13) {
				Class<?> goalGotoTarget = ReflectionUtils.getNMSClass("PathfinderGoalMoveTowardsTarget");
				double spd = 1.0D;
				if (moveTargetSpeed != null)
					spd = moveTargetSpeed.getSingle(e).doubleValue();
				float radius = 32.0F;
				if (moveTargetRadius != null)
					radius = moveTargetRadius.getSingle(e).floatValue();
				newGoal = goalGotoTarget.getConstructor(entCreature, double.class, float.class).newInstance(nmsEnt, spd, radius);
			} else if (mark == 14) {
				boolean wasLocal = Language.setUseLocal(false);
				target = true;
				boolean checkView = false;
				EntityData<?> entityData;
				String exprInput = nearTarget.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				try {
					entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				} finally {
					Language.setUseLocal(wasLocal);
				}
				if (!LivingEntity.class.isAssignableFrom(entityData.getType()))
					return;
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				else if (className.equals("LivingEntity"))
					className = "Living";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className);
				if (nmsClass == null)
					return;
				if (ent instanceof Spider) {
					Class<?> goalSpiderNearTarget = ReflectionUtils.getNMSClass("EntitySpider$PathfinderGoalSpiderNearestAttackableTarget");
					Constructor<?> constr = goalSpiderNearTarget.getDeclaredConstructor(nmsEnt.getClass(), Class.class);
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt, nmsClass);
				} else {
					if (checkSight != null)
						checkView = checkSight.getSingle(e);
					Class<?> goalNearTarget = ReflectionUtils.getNMSClass("PathfinderGoalNearestAttackableTarget");
					newGoal = goalNearTarget.getConstructor(entCreature, Class.class, boolean.class).newInstance(nmsEnt, nmsClass, checkView);
				}
			} else if (mark == 15) {
				Class<?> goalOcelotAttack = ReflectionUtils.getNMSClass("PathfinderGoalOcelotAttack");
				newGoal = goalOcelotAttack.getConstructor(entInsent).newInstance(nmsEnt);
			} else if (mark == 16) {
				Class<?> goalOpenDoors = ReflectionUtils.getNMSClass("PathfinderGoalOpenDoor");
				newGoal = goalOpenDoors.getConstructor(entInsent, boolean.class).newInstance(nmsEnt, false);
			} else if (mark == 17) {
				double spd = 1.0D;
				if (panicSpeed != null)
					spd = panicSpeed.getSingle(e).doubleValue();
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
				Class<?> goalWander = ReflectionUtils.getNMSClass("PathfinderGoalRandomStroll");
				double spd = 1.0D;
				if (randomWalkSpeed != null)
					spd = randomWalkSpeed.getSingle(e).doubleValue();
				int interval = 120;
				if (randomWalkInterval != null)
					interval = randomWalkInterval.getSingle(e).getTicks();
				newGoal = goalWander.getConstructor(entCreature, double.class, int.class).newInstance(nmsEnt, spd, interval);
			} else if (mark == 20) {
				Class<?> goalSit = ReflectionUtils.getNMSClass("PathfinderGoalSit");
				if (!(ent instanceof Tameable))
					return;
				newGoal = goalSit.getConstructor(entTameable).newInstance(nmsEnt);
			} else if (mark == 21) {
				Class<?> goalSwell = ReflectionUtils.getNMSClass("PathfinderGoalSwell");
				if (!(ent instanceof Creeper))
					return;
				newGoal = goalSwell.getConstructor(nmsEnt.getClass()).newInstance(nmsEnt);
			} else if (mark == 22) {
				Class<?> goalSquid = ReflectionUtils.getNMSClass("EntitySquid$PathfinderGoalSquid");
				if (!(ent instanceof Squid))
					return;
				Constructor<?> constr = goalSquid.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 23) {
				if (ent instanceof Blaze) {
					Class<?> goalBlazeFireball = ReflectionUtils.getNMSClass("EntityBlaze$PathfinderGoalBlazeFireball");
					Constructor<?> constr = goalBlazeFireball.getDeclaredConstructor(nmsEnt.getClass());
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt);
				} else if (ent instanceof Ghast) {
					Class<?> goalGhastFireball = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastAttackTarget");
					Constructor<?> constr = goalGhastFireball.getDeclaredConstructor(nmsEnt.getClass());
					constr.setAccessible(true);
					newGoal = constr.newInstance(nmsEnt);
				}
			} else if (mark == 24) {
				Class<?> goalHideInBlock = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishHideInBlock");
				if (!(ent instanceof Silverfish))
					return;
				Constructor<?> constr = goalHideInBlock.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 25) {
				Class<?> goalWakeSilverfish = ReflectionUtils.getNMSClass("EntitySilverfish$PathfinderGoalSilverfishWakeOthers");
				if (!(ent instanceof Silverfish))
					return;
				Constructor<?> constr = goalWakeSilverfish.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 26) {
				Class<?> goalPickBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPickupBlock");
				if (!(ent instanceof Enderman))
					return;
				Constructor<?> constr = goalPickBlocks.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 27) {
				Class<?> goalPlaceBlocks = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalEndermanPlaceBlock");
				if (!(ent instanceof Enderman))
					return;
				Constructor<?> constr = goalPlaceBlocks.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 28) {
				target = true;
				Class<?> goalAttackLooker = ReflectionUtils.getNMSClass("EntityEnderman$PathfinderGoalPlayerWhoLookedAtTarget");
				if (!(ent instanceof Enderman))
					return;
				Constructor<?> constr = goalAttackLooker.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 29) {
				Class<?> goalGhastMoveTarget = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastMoveTowardsTarget");
				if (!(ent instanceof Ghast))
					return;
				Constructor<?> constr = goalGhastMoveTarget.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 30) {
				Class<?> goalGhastIdleMove = ReflectionUtils.getNMSClass("EntityGhast$PathfinderGoalGhastIdleMove");
				if (!(ent instanceof Ghast))
					return;
				Constructor<?> constr = goalGhastIdleMove.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 31) {
				Class<?> goalTempt = ReflectionUtils.getNMSClass("PathfinderGoalTempt");
				ItemStack itemStack = temptItem.getSingle(e);
				if (itemStack.getType() == Material.AIR || itemStack == null)
					return;
				Object nmsItemStack = craftItemClass.getMethod("asNMSCopy", ItemStack.class).invoke(itemStack, itemStack);
				Object nmsItem = nmsItemStack.getClass().getMethod("getItem").invoke(nmsItemStack);
				double spd = 1.0D;
				if (temptSpeed != null)
					spd = temptSpeed.getSingle(e).doubleValue();
				boolean scared = false;
				if (temptScared != null)
					scared = temptScared.getSingle(e);
				newGoal = goalTempt.getConstructor(entCreature, double.class, nmsItemClass, boolean.class).newInstance(nmsEnt, spd, nmsItem, scared);
			} else if (mark == 32) {
				boolean wasLocal = Language.setUseLocal(false);
				target = true;
				Class<?> goalTargetNonTamed = ReflectionUtils.getNMSClass("PathfinderGoalRandomTargetNonTamed");
				if (!(ent instanceof Tameable))
					return;
				EntityData<?> entityData;
				String exprInput = nonTamedTarget.toString(e, false);
				if (exprInput.startsWith("the ")) {
					exprInput = exprInput.substring(4);
				}
				try {
					entityData = EntityData.parseWithoutIndefiniteArticle(exprInput);
				} finally {
					Language.setUseLocal(wasLocal);
				}
				if (!LivingEntity.class.isAssignableFrom(entityData.getType())) {
					return;
				}
				String className = entityData.getType().getSimpleName();
				if (className.equals("HumanEntity"))
					className = "Human";
				else if (className.equals("LivingEntity"))
					className = "Living";
				className = "Entity" + className;
				Class<?> nmsClass = ReflectionUtils.getNMSClass(className);
				if (nmsClass == null)
					return;
				newGoal = goalTargetNonTamed.getConstructor(entTameable, Class.class, boolean.class, Predicate.class).newInstance(nmsEnt, nmsClass, false, Predicates.alwaysTrue());
			} else if (mark == 33) {
				if (!(ent instanceof Guardian))
					return;
				Class<?> goalGuardianAttack = ReflectionUtils.getNMSClass("EntityGuardian$PathfinderGoalGuardianAttack");
				Constructor<?> constr = goalGuardianAttack.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 34) {
				if (!(ent instanceof PigZombie))
					return;
				target = true;
				Class<?> goalAnger = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAnger");
				Constructor<?> constr = goalAnger.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 35) {
				if (!(ent instanceof PigZombie))
					return;
				target = true;
				Class<?> goalAngerOther = ReflectionUtils.getNMSClass("EntityPigZombie$PathfinderGoalAngerOther");
				Constructor<?> constr = goalAngerOther.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 36) {
				if (!(ent instanceof Rabbit))
					return;
				Class<?> goalEatCarrots = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalEatCarrots");
				Constructor<?> constr = goalEatCarrots.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 37) {
				if (!(ent instanceof Rabbit))
					return;
				Class<?> goalRabbitAttack = ReflectionUtils.getNMSClass("EntityRabbit$PathfinderGoalKillerRabbitMeleeAttack");
				Constructor<?> constr = goalRabbitAttack.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 38) {
				if (!(ent instanceof Slime))
					return;
				Class<?> goalJump = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomJump");
				Constructor<?> constr = goalJump.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 39) {
				if (!(ent instanceof Slime))
					return;
				Class<?> goalRandomDir = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeRandomDirection");
				Constructor<?> constr = goalRandomDir.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			} else if (mark == 40) {
				if (!(ent instanceof Slime))
					return;
				Class<?> goalSlimeWander = ReflectionUtils.getNMSClass("EntitySlime$PathfinderGoalSlimeIdle");
				Constructor<?> constr = goalSlimeWander.getDeclaredConstructor(nmsEnt.getClass());
				constr.setAccessible(true);
				newGoal = constr.newInstance(nmsEnt);
			}
			if (newGoal == null)
				return;
			SkStuff.getNMSMethods().addPathfinderGoal(nmsEnt, priority, newGoal, target);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}