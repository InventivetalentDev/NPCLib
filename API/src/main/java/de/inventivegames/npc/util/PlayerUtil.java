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

package de.inventivegames.npc.util;

import de.inventivegames.npc.NPCLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;

public class PlayerUtil {

	public static int getVersion(Player p) {
		try {
			final Object handle = Reflection.getHandle(p);
			final Object connection = NMSClass.nmsFieldPlayerConnection.get(handle);
			final Object network = NMSClass.nmsFieldNetworkManager.get(connection);
			final Object channel;
			if (NPCLib.getServerVersion() == 170) {
				channel = NMSClass.nmsFieldNetworkManagerM.get(network);
			} else {
				channel = NMSClass.nmsFieldNetworkManagerI.get(network);
			}
			final Object version = NPCLib.getServerVersion() == 170 ? NMSClass.nmsNetworkGetVersion.invoke(network, channel) : 47;
			return (int) version;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String uniquifyName(String name) {
		String prefix = "";
		String suffix = "";
		while (prefix.length() < 8) {
			prefix += randomColorCode();
		}
		while (suffix.length() < 8) {
			suffix += randomColorCode();
		}
		return prefix + ChatColor.RESET + name + suffix;
	}

	public static String randomColorCode() {
		return ChatColor.values()[new Random().nextInt(ChatColor.values().length)].toString();
	}

}
