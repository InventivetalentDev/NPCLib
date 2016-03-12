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

package de.inventivegames.npc.entity.living;

import de.inventivegames.npc.NPCLib;
import de.inventivegames.npc.entity.NPCEntityBase;
import de.inventivegames.npc.event.NPCSpawnEvent;
import de.inventivegames.npc.util.AccessUtil;
import de.inventivegames.npc.util.NMSClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.UUID;

public abstract class NPCLivingEntityBase extends NPCEntityBase {

	public NPCLivingEntityBase(World world, Location location) throws Exception {
		super(world, location);
	}

	@Override
	protected void initialize(World world, Location location) throws Exception {
		if (NPCLib.getServerVersion() >= 180) {
			try {
				Object normalPathfinder = NMSClass.nmsPathfinderNormal.newInstance();
				if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {//TODO: 1.9
					NMSClass.nmsPathfinderNormal.getDeclaredMethod("a", boolean.class).invoke(normalPathfinder, true);
				}
				this.pathFinder = NMSClass.nmsPathfinder.getConstructor(NMSClass.nmsPathfinderAbstract).newInstance(normalPathfinder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// TODO Make Pathfinder compatible with 1.7
		}

		/* Update the location and rotation */
		AccessUtil.setAccessible(NMSClass.nmsEntity.getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class)).invoke(this.theEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());


		/* Add to the world entities */
		AccessUtil.setAccessible(NMSClass.nmsWorld.getDeclaredMethod("addEntity", NMSClass.nmsEntity)).invoke(this.worldServer, this.theEntity);

		NPCSpawnEvent event = new NPCSpawnEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public UUID getUUID() {
		try {
			if(Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
				return (UUID) AccessUtil.setAccessible(NMSClass.nmsEntityZombie.getDeclaredField("b")).get(this.getEntity());
			}else{
				return (UUID) AccessUtil.setAccessible(NMSClass.nmsEntity.getDeclaredField("uniqueID")).get(this.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateToPlayer(Player player) {
	}

}
