/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package de.inventivegames.npc.entity;

import de.inventivegames.npc.NPC;
import de.inventivegames.npc.NPCLib;
import de.inventivegames.npc.ai.AIList;
import de.inventivegames.npc.ai.NPCAI;
import de.inventivegames.npc.animation.NPCAnimation;
import de.inventivegames.npc.equipment.EquipmentSlot;
import de.inventivegames.npc.event.*;
import de.inventivegames.npc.living.NPCPlayer;
import de.inventivegames.npc.path.NPCPath;
import de.inventivegames.npc.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.inventivetalent.reflection.minecraft.Minecraft;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class NPCEntityBase implements NPC, NPCEntity {

	protected Object minecraftServer;
	protected Object worldServer;

	protected String name;

	protected Object interactManager;

	/* Object containing the actual EntityPlayer */
	protected Object theEntity;

	/* Pathfinder object for the entity */
	protected Object  pathFinder;
	protected NPCPath path;

	private final AIList ai = new AIList();

	/* Enitity target */
	private Entity target;

	/* If the entity is invulnerable */
	private boolean invulnerable = true;
	/* If the entity is collidable */
	private boolean collision    = false;
	/* If the entity has gravity */
	private boolean hasGravity   = true;
	/* The gravity amount */
	private float   gravity      = 0.1f;
	/* If the entity can be moved or if its frozen */
	private boolean frozen       = true;

	/* If the entity is controllable (by players) */
	private boolean controllable = false;

	/**
	 * Base NPC entity which can be normally spawned but does not override any NMS Entity methods and can therefore be damaged etc.
	 *
	 * @param world    {@link World} to spawn the entity in
	 * @param location {@link Location} to spawn the entity at
	 */
	public NPCEntityBase(@Nonnull org.bukkit.World world, @Nonnull Location location) throws Exception {
		if (world == null || location == null) { throw new IllegalArgumentException("None of the arguments must be null!"); }

		this.minecraftServer = Reflection.getMethod(Bukkit.getServer().getClass(), "getServer").invoke(Bukkit.getServer());
		this.worldServer = Reflection.getHandle(world);
	}

	protected abstract void initialize(World world, Location location) throws Exception;

	@Override
	public int getEntityID() {
		try {
			return (int) AccessUtil.setAccessible(NMSClass.nmsEntity.getDeclaredField("id")).get(this.theEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void despawn() {
		this.getBukkitEntity().remove();
	}

	@Override
	public void setLocation(Location location) {
		this.getBukkitEntity().teleport(location);
		setYaw(location.getYaw());
		setPitch(location.getPitch());
	}

	@Override
	public void teleport(Location location) {
		this.setLocation(location);
	}

	@Override
	public Object getEntity() {
		return this.theEntity;
	}

	@Override
	public LivingEntity getBukkitEntity() {
		try {
			return (LivingEntity) AccessUtil.setAccessible(NMSClass.nmsEntity.getDeclaredField("bukkitEntity")).get(this.theEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Location getLocation() {
		try {
			return new Location((World) NMSClass.nmsWorld.getMethod("getWorld").invoke(NMSClass.nmsEntity.getDeclaredField("world").get(this.theEntity)), (double) NMSClass.nmsEntity.getDeclaredField("locX").get(this.theEntity), (double) NMSClass.nmsEntity.getDeclaredField("locY").get(this.theEntity), (double) NMSClass.nmsEntity.getDeclaredField("locZ")
					.get(this.theEntity), (float) NMSClass.nmsEntity.getDeclaredField("yaw").get(this.theEntity), (float) NMSClass.nmsEntity.getDeclaredField("pitch").get(this.theEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setInvulnerable(boolean flag) {
		this.invulnerable = flag;
	}

	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	@Override
	public void setCollision(boolean flag) {
		this.collision = flag;
	}

	@Override
	public boolean hasCollision() {
		return this.collision;
	}

	@Override
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	@Override
	public float getGravity() {
		return this.gravity;
	}

	@Override
	public void setGravity(boolean flag) {
		this.hasGravity = flag;
	}

	@Override
	public boolean hasGravity() {
		return this.hasGravity;
	}

	@Override
	public void setFrozen(boolean flag) {
		this.frozen = flag;
	}

	@Override
	public boolean isFrozen() {
		return this.frozen;
	}

	@Override
	public boolean pathfindTo(Location location) {
		return this.pathfindTo(location, 0.2D);
	}

	@Override
	public boolean pathfindTo(Location location, double speed) {
		return this.pathfindTo(location, speed, 30.0D);
	}

	@Override
	public boolean pathfindTo(Location location, double speed, double range) {
		NPCPath path = NPCPath.findPath(this, location, range, speed);
		return (this.path = path) != null;
	}

	/*
	 * AI Methods
	 */

	@Override
	public boolean addAIBehaviour(NPCAI ai) {
		if (this.ai.contains(ai)) { return false; }
		return this.ai.add(ai);
	}

	@Override
	public boolean removeAIBehaviour(NPCAI ai) {
		if (!this.ai.contains(ai)) { return false; }
		return this.ai.remove(ai);
	}

	@Override
	public List<NPCAI> getAIBehaviours() {
		return new ArrayList<>(this.ai);
	}

	private void notifyAIBehaviours() {
		for (NPCAI ai : this.getAIBehaviours()) {
			ai.onUpdate();
		}
	}

	/*
	 * Look methods
	 */

	@Override
	public void setTarget(Entity target) {
		this.target = target;
		if (target != null) {
			this.lookAt(target.getLocation());
		}
	}

	@Override
	public Entity getTarget() {
		return this.target;
	}

	@Override
	public void lookAt(Location location) {
		if (this.getLocation().getWorld() != location.getWorld()) { return; }
		double dx = location.getX() - this.getBukkitEntity().getEyeLocation().getX();
		double dy = location.getY() - this.getBukkitEntity().getEyeLocation().getY();
		double dz = location.getZ() - this.getBukkitEntity().getEyeLocation().getZ();
		double xzd = Math.sqrt(dx * dx + dz * dz);
		double yd = Math.sqrt(xzd * xzd + dy * dy);

		double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0D;
		if (yaw < 0) {
			yaw += 360;
		}
		double pitch = -Math.toDegrees(Math.atan2(dy, yd));

		this.setYaw((float) yaw);
		this.setPitch((float) pitch);
	}

	@Override
	public void setControllable(boolean flag) {
		if (flag == this.isControllable()) { return; }
		this.controllable = flag;
	}

	@Override
	public boolean isControllable() {
		return this.controllable;
	}

	@Override
	public void setPassenger(Entity ent) {
		this.getBukkitEntity().setPassenger(ent);
	}

	@Override
	public Entity getPassenger() {
		return this.getBukkitEntity().getPassenger();
	}

	@Override
	public void setYaw(float yaw) {
		try {
			NMSClass.nmsEntity.getDeclaredField("yaw").set(this.theEntity, NMSUtil.clampYaw(yaw));

			NMSClass.nmsEntityLiving.getDeclaredField(Minecraft.VERSION.olderThan(Minecraft.Version.v1_8_R1) ? "aP" : NPCLib.getServerVersion() <= 181 ? "aJ" : NPCLib.getServerVersion() <= 183 ? "aL" : "aP").set(this.theEntity, yaw);
			NMSClass.nmsEntityLiving.getDeclaredField(Minecraft.VERSION.olderThan(Minecraft.Version.v1_8_R1) ? "aL" : NPCLib.getServerVersion() <= 181 ? "aF" : NPCLib.getServerVersion() <= 182 ? "aH" : NPCLib.getServerVersion() <= 183 ? "aG" : "aL").set(this.theEntity, yaw);
			NMSClass.nmsEntityLiving.getDeclaredField(Minecraft.VERSION.olderThan(Minecraft.Version.v1_8_R1) ? "aK" : NPCLib.getServerVersion() <= 181 ? "aE" : NPCLib.getServerVersion() <= 183 ? "aG" : "aK").set(this.theEntity, yaw);

			/* This seems to be the 'real' field */
			NMSClass.nmsEntityLiving.getDeclaredField(Minecraft.VERSION.olderThan(Minecraft.Version.v1_8_R1) ? "aO" : NPCLib.getServerVersion() <= 181 ? "aI" : NPCLib.getServerVersion() <= 183 ? "aK" : "aO").set(this.theEntity, yaw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public float getYaw() {
		try {
			return (float) NMSClass.nmsEntity.getDeclaredField("yaw").get(this.theEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void setPitch(float pitch) {
		try {
			NMSClass.nmsEntity.getDeclaredField("pitch").set(this.theEntity, pitch);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public float getPitch() {
		try {
			return (float) NMSClass.nmsEntity.getDeclaredField("pitch").get(this.theEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * NPCEntity methods
	 */

	@Override
	public Object getPathfinder() {
		return this.pathFinder;
	}

	public void setPath(NPCPath path) {
		this.path = path;
	}

	@Override
	public NPCPath getPath() {
		return this.path;
	}

	@Override
	public void move(double x, double y, double z) {
		try {
			NMSClass.nmsEntity.getDeclaredMethod("move", double.class, double.class, double.class).invoke(this.theEntity, x, y, z);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void checkMovement(double x, double y, double z) {
		try {
			NMSClass.nmsEntityHuman.getDeclaredMethod("checkMovement", double.class, double.class, double.class).invoke(this.theEntity, x, y, z);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playAnimation(NPCAnimation animation) {
		try {
			this.broadcastPacket(ClassBuilder.buildPacketPlayOutAnimation(this.getEntityID(), animation.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setEquipment(de.inventivegames.npc.equipment.EquipmentSlot slot, ItemStack item) {
		switch (slot) {
			case HAND:
				this.getBukkitEntity().getEquipment().setItemInHand(item);
				break;
			case FEET:
				this.getBukkitEntity().getEquipment().setBoots(item);
				break;
			case LEGS:
				this.getBukkitEntity().getEquipment().setLeggings(item);
				break;
			case CHEST:
				this.getBukkitEntity().getEquipment().setChestplate(item);
				break;
			case HEAD:
				this.getBukkitEntity().getEquipment().setHelmet(item);
				break;
			default:
				break;
		}
	}

	@Override
	public ItemStack getEquipment(EquipmentSlot slot) {
		switch (slot) {
			case HAND:
				return this.getBukkitEntity().getEquipment().getItemInHand();
			case FEET:
				return this.getBukkitEntity().getEquipment().getBoots();
			case LEGS:
				return this.getBukkitEntity().getEquipment().getLeggings();
			case CHEST:
				return this.getBukkitEntity().getEquipment().getChestplate();
			case HEAD:
				return this.getBukkitEntity().getEquipment().getHelmet();
			default:
				return null;
		}
	}

	private static final int DISTANCE = Bukkit.getViewDistance() * 16;

	private void broadcastPacket(Object packet) {
		for (Player p : this.getBukkitEntity().getWorld().getPlayers()) {
			if (this.getLocation().distanceSquared(p.getLocation()) <= DISTANCE * DISTANCE) {
				try {
					this.sendPacket(p, packet);
				} catch (Exception e) {
				}
			}
		}
	}

	protected void sendPacket(Player p, Object packet) throws Exception {
		if (p == null || packet == null) { return; }
		if (p == this.theEntity || p == this.getBukkitEntity()) { return; }
		if (p instanceof NPCEntityNMS) { return; }
		if (NPCLib.isNPC(p)) { return; }
		Object handle = Reflection.getHandle(p);
		if (handle != null) {
			Object connection = AccessUtil.setAccessible(handle.getClass().getDeclaredField("playerConnection")).get(handle);
			if (connection != null) {
				Reflection.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, new Object[] { packet });
			}
		}
	}

	/*
	 * Overridden methods
	 */

	public void onUpdate() {
		if (this.getTarget() != null) {
			if (this.getTarget().isDead() || this.getTarget() instanceof Player && !((Player) this.getTarget()).isOnline()) {
				this.setTarget(null);
			} else if (this instanceof NPCPlayer && !((NPCPlayer) this).isLying() && this.getLocation().getWorld().equals(this.getTarget().getWorld()) && this.getLocation().distanceSquared(this.getTarget().getLocation()) <= 32 * 32) {
				if (this.getTarget() instanceof LivingEntity) {
					this.lookAt(((LivingEntity) this.getTarget()).getEyeLocation());
				} else {
					this.lookAt(this.getTarget().getLocation());
				}
			}
		}
		if (this.getPath() != null) {
			if (!this.getPath().update()) {
				NPCPathFinishEvent event = new NPCPathFinishEvent(this, this.getPath());
				this.setPath(null);
				Bukkit.getPluginManager().callEvent(event);
			}
		}
		this.notifyAIBehaviours();
	}

	public boolean onInteract(Object entity, NPCInteractEvent.InteractType action) {
		Player player = null;
		if (entity instanceof Player) { player = (Player) entity; }
		if (NMSClass.nmsEntityHuman.equals(entity.getClass())) {
			try {
				player = (Player) NMSClass.nmsEntityHuman.getDeclaredMethod("getBukkitEntity").invoke(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (player == null) { return true; }
		NPCInteractEvent event = new NPCInteractEvent(this, player, action);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }
		return true;
	}

	public boolean onDamage(Object damageSource, float damage, Object entity) {
		String damageName = "outOfWorld";
		try {
			damageName = (String) NMSClass.nmsDamageSource.getDeclaredField("translationIndex").get(damageSource);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DamageCause cause = DamageCause.CUSTOM;
		switch (damageName) {
			case "inFire":
				cause = DamageCause.FIRE;
				break;
			case "onFire":
				cause = DamageCause.FIRE_TICK;
				break;
			case "lava":
				cause = DamageCause.LAVA;
				break;
			case "inWall":
				cause = DamageCause.SUFFOCATION;
				break;
			case "drown":
				cause = DamageCause.DROWNING;
				break;
			case "starve":
				cause = DamageCause.STARVATION;
				break;
			case "cactus":
				cause = DamageCause.CONTACT;// TODO
				break;
			case "fall":
				cause = DamageCause.FALL;
				break;
			case "outOfWorld":
				cause = DamageCause.VOID;
				break;
			case "generic":
//				System.out.println("generic");
				cause = DamageCause.CUSTOM;// TODO
				break;
			case "magic":
				cause = DamageCause.MAGIC;
				break;
			case "wither":
				cause = DamageCause.WITHER;
				break;
			case "anvil":
				cause = DamageCause.FALLING_BLOCK;// TODO
				break;
			case "fallingBlock":
				cause = DamageCause.FALLING_BLOCK;
				break;
			case "thorns":
				cause = DamageCause.THORNS;
				break;
			case "indirectMagic":// TODO Probably poison
//				System.out.println("indirectMagic");
				break;
			case "fireball":
//				System.out.println("fireball");
				cause = DamageCause.FIRE;// TODO
				break;
			case "thrown":
//				System.out.println("thrown");
				cause = DamageCause.ENTITY_ATTACK;// TODO
				break;
			case "arrow":
				cause = DamageCause.PROJECTILE;// TODO
				break;
			case "mob":
				cause = DamageCause.ENTITY_ATTACK;
				break;
			case "player":
				cause = DamageCause.ENTITY_ATTACK;// TODO
				break;
			case "explosion.player":
				cause = DamageCause.ENTITY_EXPLOSION;// TODO
				break;
			case "explosion":
				cause = DamageCause.ENTITY_EXPLOSION;// TODO
				break;
			default:
				break;
		}

		Object damager = null;
		try {
			damager = entity != null ? Reflection.getBukkitEntity(entity) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		NPCDamageEvent event = new NPCDamageEvent(this, cause, damage, (Entity) damager);
		event.setCancelled(this.isInvulnerable());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }

		return true;
	}

	public boolean onCollide(Entity with) {
		NPCCollideEvent event = new NPCCollideEvent(this, with);
		event.setCancelled(!this.hasCollision());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }
		return true;
	}

	public boolean onMotion(double x, double y, double z) {
		NPCMotionEvent event = new NPCMotionEvent(this, new Vector(x, y, z));
		event.setCancelled(this.isFrozen());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }
		return true;
	}

	public boolean onControl(float side, float forward, NPCControlEvent event) {
		if (!this.isControllable()) { event.setCancelled(true); }
		if (this.isFrozen() || this instanceof NPCPlayer && ((NPCPlayer) this).isLying()) { event.setCancelled(true); }
		if (this.getPassenger() == null || !(this.getPassenger() instanceof Player)) { event.setCancelled(true); }
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) { return false; }
		return true;
	}

	public boolean onDespawn() {
		NPCDespawnEvent event = new NPCDespawnEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		return true;
	}

}
