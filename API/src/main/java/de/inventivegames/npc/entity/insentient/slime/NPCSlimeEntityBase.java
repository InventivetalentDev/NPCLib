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

package de.inventivegames.npc.entity.insentient.slime;

import de.inventivegames.npc.entity.insentient.NPCInsentientEntityBase;
import de.inventivegames.npc.living.NPCSlime;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Slime;

public class NPCSlimeEntityBase extends NPCInsentientEntityBase implements NPCSlime {

	public NPCSlimeEntityBase(World world, Location location) throws Exception {
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
	public Slime getBukkitEntity() {
		return (Slime) super.getBukkitEntity();
	}

	@Override
	public int getSize() {
		return this.getBukkitEntity().getSize();
	}

	@Override
	public void setSize(int size) {
		this.getBukkitEntity().setSize(size);
	}

	/* Deprecated methods (only supported in NPCPlayer) */

	@Override
	@Deprecated
	public void setGameMode(GameMode mode) {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public GameMode getGameMode() {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public void setLying(boolean flag) {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public boolean isLying() {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public void setShownInList(boolean flag) {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public boolean isShownInList() {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public void setPing(int ping) {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

	@Override
	@Deprecated
	public int getPing() {
		throw new UnsupportedOperationException("This method is not supported in this class");
	}

}
