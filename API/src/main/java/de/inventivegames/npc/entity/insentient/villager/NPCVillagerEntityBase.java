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

import de.inventivegames.npc.entity.insentient.NPCInsentientEntityBase;
import de.inventivegames.npc.living.NPCVillager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Villager;

import javax.annotation.Nonnull;

public class NPCVillagerEntityBase extends NPCInsentientEntityBase implements NPCVillager {

	/**
	 * Base NPC entity which can be normally spawned but does not override any NMS Entity methods and can therefore be damaged etc.
	 *
	 * @param world    {@link World} to spawn the entity in
	 * @param location {@link Location} to spawn the entity at
	 */
	public NPCVillagerEntityBase(@Nonnull World world, @Nonnull Location location) throws Exception {
		super(world, location);
	}

	@Override
	protected void initialize(World world, Location location) throws Exception {
		super.initialize(world, location);

		this.clearGoalSelector();
		this.clearTargetSelector();

		setLocation(location);
	}

	@Override
	public Villager getBukkitEntity() {
		return (Villager) super.getBukkitEntity();
	}

	@Override
	public boolean isBaby() {
		return !this.getBukkitEntity().isAdult();
	}

	@Override
	public void setBaby(boolean flag) {
		if (flag) { this.getBukkitEntity().setBaby(); } else { this.getBukkitEntity().setAdult(); }
	}

	@Override
	public void setProfession(Villager.Profession profession) {
		this.getBukkitEntity().setProfession(profession);
	}

	@Override
	public Villager.Profession getProfession() {
		return this.getBukkitEntity().getProfession();
	}

}
