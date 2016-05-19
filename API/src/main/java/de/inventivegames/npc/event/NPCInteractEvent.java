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

package de.inventivegames.npc.event;

import de.inventivegames.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends NPCEvent implements Cancellable {

	public static enum InteractType {
		RIGHT_CLICK,
		LEFT_CLICK,
		UNKNOWN;

		public static InteractType fromUseAction(Object useAction) {
			if (useAction == null) { return UNKNOWN; }
			@SuppressWarnings("rawtypes")
			int i = ((Enum) useAction).ordinal();
			switch (i) {
				case 0:
					return RIGHT_CLICK;
				case 1:
					return LEFT_CLICK;
				default:
					break;
			}
			return UNKNOWN;
		}
	}

	private static final HandlerList handlerList = new HandlerList();

	private boolean cancelled;

	private Player       player;
	private InteractType type;

	public NPCInteractEvent(NPC npc, Player player, InteractType type) {
		super(npc);

		this.player = player;
		this.type = type;
	}

	/**
	 * @return The player that interacted with the NPC
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * @return The {@link de.inventivegames.npc.event.NPCInteractEvent.InteractType}
	 */
	public InteractType getType() {
		return this.type;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean flag) {
		this.cancelled = flag;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
