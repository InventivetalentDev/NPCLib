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

package de.inventivegames.npc.living;

import de.inventivegames.npc.NPC;
import de.inventivegames.npc.skin.Hand;
import de.inventivegames.npc.skin.SkinLayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public interface NPCPlayer extends NPC {

	@Override
	public Player getBukkitEntity();

	/**
	 * @param mode The {@link GameMode} of the entity
	 */
	public void setGameMode(GameMode mode);

	/**
	 * @return The {@link GameMode} of the entity
	 */
	public GameMode getGameMode();

	/**
	 * @param flag <code>true</code> if the player should be show in the player list
	 */
	public void setShownInList(boolean flag);

	/**
	 * @return <code>true</code> if the player is shown in the player list
	 */
	public boolean isShownInList();

	/**
	 * @param ping new ping of the player
	 */
	public void setPing(int ping);

	/**
	 * @return ping if the player (not the actual ping)
	 */
	public int getPing();

	/**
	 * Updates the visible skin layers
	 *
	 * @param layers {@link de.inventivegames.npc.skin.SkinLayer} array of all visible layers
	 */
	public void setSkinLayers(SkinLayer... layers);

	/**
	 * Set the main hand
	 *
	 * @param hand {@link Hand}
	 */
	void setMainHand(Hand hand);

	/**
	 * @param flag <code>true</code> if the entity should be lying (sleeping)
	 */
	public void setLying(boolean flag);

	/**
	 * @return <code>true</code> if the entity is lying (sleeping)
	 */
	public boolean isLying();

}
