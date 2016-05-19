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

package de.inventivegames.npc.profile;

import de.inventivegames.npc.NPCLib;
import de.inventivegames.npc.util.NMSClass;
import org.bukkit.Bukkit;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.util.Iterator;
import java.util.UUID;

public class NPCProfileBase implements NPCProfile {

	protected static Object SKIN_CACHE;

	static FieldResolver MinecraftServerFieldResolver = new FieldResolver(NMSClass.nmsMinecraftServer);

	@SuppressWarnings({
			"rawtypes",
			"deprecation" })
	protected static Object loadSkin(String name) {
		if (NPCLib.useNickNamer()) { return null; }
		if (name == null || name.isEmpty()) { return null; }
		try {
			Object profile = NMSClass.classGameProfile.getConstructor(UUID.class, String.class).newInstance(Bukkit.getOfflinePlayer(name).getUniqueId(), name);
			Object mcServer = NMSClass.nmsMinecraftServer.getDeclaredMethod("getServer").invoke(null);
			Object sessionService = MinecraftServerFieldResolver.resolveByFirstType(NMSClass.nmMinecraftSessionService).get(mcServer);
			profile = NMSClass.nmMinecraftSessionService.getDeclaredMethod("fillProfileProperties", NMSClass.classGameProfile, boolean.class).invoke(sessionService, profile, true);
			Object properties = NMSClass.classGameProfile.getDeclaredMethod("getProperties").invoke(profile);
			Iterable iterable = (Iterable) NMSClass.comGoogleCommonCollectForwardingMultimap.getDeclaredMethod("get", Object.class).invoke(properties, (Object) "textures");
			Iterator iterator = iterable.iterator();
			return iterator.hasNext() ? iterator.next() : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static NPCProfileBase loadProfile(String name, String skin) {
		try {
			return makeProfile(name, NMSClass.comGoogleCommonCacheLoadingCache.getDeclaredMethod("get", Object.class).invoke(SKIN_CACHE, skin));
		} catch (Exception e) {
			try {
				return new NPCProfileBase(name);
			} catch (Exception e1) {
				e.printStackTrace();
				e1.printStackTrace();
			}
		}
		return null;
	}

	public static NPCProfileBase makeProfile(String name, Object data) {
		try {
			Object nmsProfile = NMSClass.classGameProfile.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), name);
			Object properties = NMSClass.classGameProfile.getDeclaredMethod("getProperties").invoke(nmsProfile);
			NMSClass.comGoogleCommonCollectForwardingMultimap.getDeclaredMethod("put", Object.class, Object.class).invoke(properties, "textures", data);
			return new NPCProfileBase(nmsProfile);
		} catch (Exception e) {
			try {
				return new NPCProfileBase(name);
			} catch (Exception e1) {
				e.printStackTrace();
				e1.printStackTrace();
			}
		}
		return null;
	}

	protected final Object theProfile;

	public NPCProfileBase(String name) throws Exception {
		this(NMSClass.classGameProfile.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), name));
	}

	public NPCProfileBase(Object profile) {
		this.theProfile = profile;
	}

	@Override
	public UUID getUUID() {
		try {
			return (UUID) AccessUtil.setAccessible(NMSClass.classGameProfile.getDeclaredField("id")).get(this.theProfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getName() {
		try {
			return (String) AccessUtil.setAccessible(NMSClass.classGameProfile.getDeclaredField("name")).get(this.theProfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getProfile() {
		return this.theProfile;
	}

}
