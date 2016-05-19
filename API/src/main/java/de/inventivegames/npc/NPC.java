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

package de.inventivegames.npc;

import de.inventivegames.npc.ai.NPCAI;
import de.inventivegames.npc.animation.NPCAnimation;
import de.inventivegames.npc.equipment.EquipmentSlot;
import de.inventivegames.npc.path.NPCPath;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface NPC {

	/**
	 * @return The ID of this entity
	 */
	public int getEntityID();

	/**
	 * @return the {@link java.util.UUID} of the entity
	 */
	public UUID getUUID();

	/**
	 * @return the (display-) name of the entity
	 */
	public String getName();

	/**
	 * Despawns the entity
	 */
	public void despawn();

	/**
	 * @param location The new location of the entity
	 */
	public void setLocation(Location location);

	/**
	 * @param location The new location of the entity
	 * @see de.inventivegames.npc.NPC#setLocation(Location)
	 */
	public void teleport(Location location);

	/**
	 * @return The {@link Entity} instance of the entity including its methods
	 */
	public LivingEntity getBukkitEntity();

	/**
	 * @return The {@link Location} of the entity
	 */
	public Location getLocation();

	/**
	 * @param flag <code>true</code> if the entity should be invulnerable
	 */
	public void setInvulnerable(boolean flag);

	/**
	 * @return <code>true</code> if the entity is invulnerable
	 */
	public boolean isInvulnerable();

	/**
	 * @param flag <code>true</code> if the entity should have a collision
	 */
	public void setCollision(boolean flag);

	/**
	 * @return <code>true</code> If the entity has a collision
	 */
	public boolean hasCollision();

	/**
	 * @param gravity The amount of gravity
	 */
	public void setGravity(float gravity);

	/**
	 * @return The amount of gravity
	 */
	public float getGravity();

	/**
	 * @param flag <code>true</code> if the entity should have gravity
	 */
	public void setGravity(boolean flag);

	/**
	 * @return <code>true</code> if the entity has gravity
	 */
	public boolean hasGravity();

	/**
	 * @param flag <code>true</code> if the entity should be frozen
	 */
	public void setFrozen(boolean flag);

	/**
	 * @return <code>true</code> if the entity is frozen
	 */
	public boolean isFrozen();

	/**
	 * @param location target {@link Location}
	 * @return <code>true</code> if a path has been found
	 * @see #pathfindTo(Location, double)
	 * @see #pathfindTo(Location, double, double)
	 */
	public boolean pathfindTo(Location location);

	/**
	 * @param location target {@link Location}
	 * @param speed    speed of the entity
	 * @return <code>true</code> if a path has been found
	 * @see #pathfindTo(Location)
	 * @see #pathfindTo(Location, double, double)
	 */
	public boolean pathfindTo(Location location, double speed);

	/**
	 * @param location target {@link Location}
	 * @param speed    speed of the entity
	 * @param range    path detection range
	 * @return <code>true</code> if a path has been found
	 * @see #pathfindTo(Location)
	 * @see #pathfindTo(Location, double)
	 */
	public boolean pathfindTo(Location location, double speed, double range);

	/**
	 * @param ai {@link de.inventivegames.npc.ai.NPCAI} to add
	 * @return <code>true</code> if it has been added
	 */
	public boolean addAIBehaviour(NPCAI ai);

	/**
	 * @param ai {@link de.inventivegames.npc.ai.NPCAI} to remove
	 * @return <code>true</code> if it has been removed
	 */
	public boolean removeAIBehaviour(NPCAI ai);

	/**
	 * @return {@link java.util.List} of registered {@link de.inventivegames.npc.ai.NPCAI} behaviours
	 */
	public List<NPCAI> getAIBehaviours();

	/**
	 * @param target {@link Entity} target of the player
	 */
	public void setTarget(Entity target);

	/**
	 * @return {@link Entity} target of the player
	 */
	public Entity getTarget();

	/**
	 * @param location {@link Location} to look at
	 */
	public void lookAt(Location location);

	/**
	 * Makes the NPC controllable (by players)
	 *
	 * @param flag <code>true</code> if the NPC should be controllable
	 */
	public void setControllable(boolean flag);

	/**
	 * @return <code>true</code> if the NPC is controllable
	 */
	public boolean isControllable();

	/**
	 * @param ent The passenger of the NPC. May be null
	 */
	public void setPassenger(Entity ent);

	/**
	 * @return The passenger if the NPC
	 */
	public Entity getPassenger();

	/**
	 * @param yaw new yaw
	 */
	public void setYaw(float yaw);

	/**
	 * @return the yaw
	 */
	public float getYaw();

	/**
	 * @param pitch new pitch
	 */
	public void setPitch(float pitch);

	/**
	 * @return the pitch
	 */
	public float getPitch();

	/**
	 * @return {@link de.inventivegames.npc.path.NPCPath} of the entity
	 */
	public NPCPath getPath();

	/**
	 * @param animation {@link NPCAnimation} to be played
	 */
	public void playAnimation(NPCAnimation animation);

	/**
	 * @param slot {@link EquipmentSlot} to be changed
	 * @param item {@link ItemStack} in the slot
	 */
	public void setEquipment(de.inventivegames.npc.equipment.EquipmentSlot slot, ItemStack item);

	/**
	 * @param slot {@link EquipmentSlot} to check
	 * @return {@link ItemStack} in the slot
	 */
	public ItemStack getEquipment(EquipmentSlot slot);

	/* Deprecated methods - moved to NPCPlayer */

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract void setGameMode(GameMode mode);

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract GameMode getGameMode();

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract void setLying(boolean flag);

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract boolean isLying();

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract void setShownInList(boolean flag);

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract boolean isShownInList();

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract void setPing(int ping);

	/**
	 * @deprecated Method has been moved to {@link de.inventivegames.npc.living.NPCPlayer}
	 */
	@Deprecated
	public abstract int getPing();
}
