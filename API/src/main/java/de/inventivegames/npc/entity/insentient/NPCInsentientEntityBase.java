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

package de.inventivegames.npc.entity.insentient;

import de.inventivegames.npc.entity.living.NPCLivingEntityBase;
import de.inventivegames.npc.util.AccessUtil;
import de.inventivegames.npc.util.NMSClass;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;

public abstract class NPCInsentientEntityBase extends NPCLivingEntityBase {

	public NPCInsentientEntityBase(World world, Location location) throws Exception {
		super(world, location);
	}

	public Object getGoalSelector() {
		try {
			return AccessUtil.setAccessible(NMSClass.nmsEntityInsentient.getDeclaredField("goalSelector")).get(this.theEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Collection getGoalListB() {
		try {
			return (Collection) AccessUtil.setAccessible(NMSClass.nmsPathfinderGoalSelector.getDeclaredField("b")).get(this.getGoalSelector());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	public Collection getGoalListC() {
		try {
			return (Collection) AccessUtil.setAccessible(NMSClass.nmsPathfinderGoalSelector.getDeclaredField("c")).get(this.getGoalSelector());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void clearGoalSelector() {
		this.getGoalListB().clear();
		this.getGoalListC().clear();
	}

	public Object getTargetSelector() {
		try {
			return AccessUtil.setAccessible(NMSClass.nmsEntityInsentient.getDeclaredField("targetSelector")).get(this.theEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Collection getTargetListB() {
		try {
			return (Collection) AccessUtil.setAccessible(NMSClass.nmsPathfinderGoalSelector.getDeclaredField("b")).get(this.getTargetSelector());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	public Collection getTargetListC() {
		try {
			return (Collection) AccessUtil.setAccessible(NMSClass.nmsPathfinderGoalSelector.getDeclaredField("c")).get(this.getTargetSelector());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void clearTargetSelector() {
		this.getTargetListB().clear();
		this.getTargetListC().clear();
	}

}
