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

package de.inventivegames.npc.entity.insentient.villager;

import de.inventivegames.npc.NPC;
import de.inventivegames.npc.entity.NPCEntityNMS;
import de.inventivegames.npc.event.NPCControlEvent;
import de.inventivegames.npc.event.NPCInteractEvent;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class NPCVillagerEntity_v1_7_R4 extends NPCVillagerEntityBase {

	/**
	 * Base NPC entity which can be normally spawned but does not override any NMS Entity methods and can therefore be damaged etc.
	 *
	 * @param world    {@link World} to spawn the entity in
	 * @param location {@link Location} to spawn the entity at
	 */
	public NPCVillagerEntity_v1_7_R4(@Nonnull World world, @Nonnull Location location) throws Exception {
		super(world, location);

		this.theEntity = new NPCEntityVillager((WorldServer) this.worldServer);

		this.initialize(world, location);
	}

	protected class NPCEntityVillager extends EntityVillager implements NPCEntityNMS {

		@Override
		public NPC getNPC() {
			return NPCVillagerEntity_v1_7_R4.this;
		}

		public NPCEntityVillager(net.minecraft.server.v1_7_R4.World world) {
			super(world);
		}

		@Override
		public void e() {
			super.e();

			if (!NPCVillagerEntity_v1_7_R4.this.isFrozen()) {
				// Apply velocity
				this.motY = this.onGround ? Math.max(0.0, this.motY) : this.motY;
				this.move(this.motX, this.motY, this.motZ);
				this.motX *= 0.800000011920929;
				this.motY *= 0.800000011920929;
				this.motZ *= 0.800000011920929;
				if (NPCVillagerEntity_v1_7_R4.this.hasGravity() && !this.onGround) {
					this.motY -= NPCVillagerEntity_v1_7_R4.this.getGravity();
				}
			}

			if (NPCVillagerEntity_v1_7_R4.this.isControllable()) {
				((EntityLiving) this).e();
			}

			NPCVillagerEntity_v1_7_R4.this.onUpdate();
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			if (NPCVillagerEntity_v1_7_R4.this.onInteract(entityhuman, NPCInteractEvent.InteractType.RIGHT_CLICK)) { return super.a(entityhuman); }
			return false;
		}

		@Override
		public boolean damageEntity(DamageSource damagesource, float f) {
			Object damager = null;
			if (damagesource instanceof EntityDamageSource) {
				damager = ((EntityDamageSource) damagesource).getEntity();
			}
			if (NPCVillagerEntity_v1_7_R4.this.onDamage(damagesource, f, damager)) { return super.damageEntity(damagesource, f); }
			return false;
		}

		@Override
		public void collide(Entity arg0) {
			if (NPCVillagerEntity_v1_7_R4.this.onCollide(arg0.getBukkitEntity())) {
				super.collide(arg0);
			}
		}

		@Override
		public void g(double d0, double d1, double d2) {
			if (NPCVillagerEntity_v1_7_R4.this.onMotion(d0, d1, d2)) {
				super.g(d0, d1, d2);
			}
		}

		@Override
		public void e(float motionSide, float motionForward) {
			if (this.passenger == null || !(this.passenger instanceof EntityHuman)) {
				super.e(motionSide, motionForward);
				this.W = 0.5f;
				return;
			}

			motionSide = ((EntityHuman) this.passenger).bd * 0.5f;
			motionForward = ((EntityHuman) this.passenger).be;

			if (motionForward <= 0f) {
				motionForward *= 0.25f;
			}
			motionSide *= 0.75f;

			NPCControlEvent event = new NPCControlEvent((NPC) NPCVillagerEntity_v1_7_R4.this, (Player) NPCVillagerEntity_v1_7_R4.this.getPassenger(), motionSide, motionForward);
			if (!NPCVillagerEntity_v1_7_R4.this.onControl(motionSide, motionForward, event)) { return; }
			if (event.isCancelled()) { return; }

			motionSide = event.getSidewaysMotion();
			motionForward = event.getForwardMotion();

			this.lastYaw = this.yaw = this.passenger.yaw;
			this.pitch = this.passenger.pitch - 0.5f;

			this.b(this.yaw, this.pitch);
			this.aO = this.aM = this.yaw;

			this.W = 1.0f;

			float speed = 0.25f;
			this.i(speed);
			super.e(motionSide, motionForward);
		}

		@Override
		public void die() {
			if (NPCVillagerEntity_v1_7_R4.this.onDespawn()) {
				super.die();
			}
		}

		@Override
		public EntityAgeable createChild(EntityAgeable entityAgeable) {
			return null;
		}

	}
}
