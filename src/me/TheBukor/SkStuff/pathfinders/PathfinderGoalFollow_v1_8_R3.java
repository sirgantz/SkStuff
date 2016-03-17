package me.TheBukor.SkStuff.pathfinders;

import java.util.List;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoal;

public class PathfinderGoalFollow_v1_8_R3 extends PathfinderGoal {
	private EntityCreature follower;
	private EntityLiving followed;
	private Class<?> followedClass;
	private float radius;
	private double speed;
	private boolean isByName;
	private String customName;

	public PathfinderGoalFollow_v1_8_R3(EntityCreature follower, Class<?> followedClass, float radius, double speed, boolean isByName, String customName) {
		this.follower = follower;
		this.followedClass = followedClass;
		this.radius = radius;
		this.speed = speed;
		this.isByName = isByName;
		this.customName = customName;
	}

	// a() is shouldExecute()
	@SuppressWarnings("unchecked")
	@Override
	public boolean a() {
		if (followed == null) {
			List<?> list = follower.world.a((Class<? extends Entity>) followedClass, follower.getBoundingBox().grow(radius, 4.0D, radius));
			if (list.isEmpty()) {
				return false;
			}
			if (isByName) {
				for (Object entity : list) {
					if (((EntityLiving) entity).getCustomName().equals(customName)) {
						followed = (EntityLiving) entity;
						return true;
					}
				}
			} else {
				followed = (EntityLiving) list.get(0);
				return true;
			}
		}
		return false;
	}

	// b() is shouldContinueExecuting()
	@Override
	public boolean b() {
		if (followed.dead) {
			return false;
		} else if (isByName) {
			if (!followed.getCustomName().equals(customName)) {
				return false;
			}
		}
		return !follower.getNavigation().m(); // m() means hasNoPath()
	}

	// c() is execute()
	@Override
	public void c() {
		follower.getNavigation().a(followed, speed);
	}
}