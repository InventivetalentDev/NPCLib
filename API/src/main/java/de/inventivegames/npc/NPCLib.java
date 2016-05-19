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

import de.inventivegames.npc.entity.NPCEntityNMS;
import de.inventivegames.npc.profile.NPCProfile;
import de.inventivegames.npc.profile.NPCProfileBase;
import de.inventivegames.npc.util.ClassBuilder;
import de.inventivegames.npc.util.NMSClass;
import de.inventivegames.npc.util.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.inventivetalent.reflection.util.AccessUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked") public class NPCLib {

	public static enum NPCType {
		PLAYER(EntityType.PLAYER, "de.inventivegames.npc.entity.player.NPCPlayerEntity_%s", null, "NPCPlayer", -1),
		ZOMBIE(EntityType.ZOMBIE, "de.inventivegames.npc.entity.insentient.zombie.NPCZombieEntity_%s", "NPCEntityZombie", "NPCZombie", 54),
		SLIME(EntityType.SLIME, "de.inventivegames.npc.entity.insentient.slime.NPCSlimeEntity_%s", "NPCEntitySlime", "NPCSlime", 55),
		VILLAGER(EntityType.VILLAGER, "de.inventivegames.npc.entity.insentient.villager.NPCVillagerEntity_%s", "NPCEntityVillager", "NPCVillager", 120);

		protected EntityType                    entityType;
		protected String                        name;
		protected int                           id;
		protected Class<? extends NPC>          wrapperClass;
		protected Class<? extends NPCEntityNMS> entityClass;

		private NPCType(EntityType type, String wrapperClass, String entityClass, String name, int id) {
			this.entityType = type;
			this.name = name;
			this.id = id;
			try {
				this.wrapperClass = (Class<? extends NPC>) Class.forName(String.format(wrapperClass, NPCLib.serverVersionString));
				if (entityClass != null) {
					this.entityClass = (Class<? extends NPCEntityNMS>) Class.forName(String.format(wrapperClass + "$" + entityClass, NPCLib.serverVersionString));
					injectEntity(this.entityClass, id, name);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		protected static NPCType getByEntityType(EntityType type) {
			if (type == null) { return null; }
			for (NPCType t : values()) {
				if (t.entityType == type) { return t; }
			}
			return null;
		}

	}

	protected static String serverVersionString = "1_7_R4";
	protected static int    serverVersion       = 170;
	protected static boolean useNickNamer;

	public static boolean canSpawn(EntityType type) {
		return NPCType.getByEntityType(type) != null;
	}

	public static NPC spawnNPC(EntityType type, Location loc) {
		return spawnNPC(type, loc, "");
	}

	public static NPC spawnNPC(EntityType type, Location loc, String name) {
		if (!canSpawn(type)) { throw new IllegalArgumentException(type + " is not supported yet"); }

		return spawnNPC(NPCType.getByEntityType(type), loc, name);
	}

	public static NPC spawnNPC(NPCType type, Location loc) {
		return spawnNPC(type, loc, "");
	}

	public static NPC spawnNPC(NPCType type, Location loc, String name) {
		Class<? extends NPC> npcClass = type.wrapperClass;

		if (type == NPCType.PLAYER) { return spawnPlayerNPC(loc, name); }
		try {
			NPC npc = npcClass.getConstructor(World.class, Location.class).newInstance(loc.getWorld(), loc);
			if (npc != null) {
				npc.getBukkitEntity().setMetadata("NPCLib", new FixedMetadataValue(NPCLibPlugin.getInstance(), true));
				return npc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/* Deprecated spawn methods */

	@Deprecated
	public static NPC spawnNPC(Location loc, String name) {
		return spawnNPC(loc, name, name);
	}

	@Deprecated
	public static NPC spawnNPC(Location loc, String name, String skinOwner) {
		return spawnPlayerNPC(loc, name, skinOwner);
	}

	@SuppressWarnings({ "rawtypes" })
	private static void injectEntity(Class<?> clazz, int id, String name) {
		try {
			((Map) AccessUtil.setAccessible(NMSClass.nmsEntityTypes.getDeclaredField("c")).get(null)).put(name, clazz);
			((Map) AccessUtil.setAccessible(NMSClass.nmsEntityTypes.getDeclaredField("d")).get(null)).put(clazz, name);
			((Map) AccessUtil.setAccessible(NMSClass.nmsEntityTypes.getDeclaredField("f")).get(null)).put(clazz, Integer.valueOf(id));
			NPCLibPlugin.getInstance().getLogger().info("Injected " + clazz.getSimpleName() + " as " + name + " with id " + id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static NPC spawnPlayerNPC(Location loc, String name) {
		return spawnPlayerNPC(loc, name, name);
	}

	public static NPC spawnPlayerNPC(Location loc, String name, String skinOwner) {
		if (!NPCLibPlugin.enabled()) { throw new IllegalStateException("NPCLib is not enabled!"); }
		if (name == null) { throw new IllegalArgumentException("The name cannot be null"); }
		if (name.length() > 16) { throw new IllegalArgumentException("The name can only be a maximum of 16 characters long"); }

		NPC npc = null;

		try {
			NPCProfile profile = new NPCProfileBase(name);
			if (!NPCLib.useNickNamer) {
				if (skinOwner == null || skinOwner.isEmpty()) {
					profile = new NPCProfileBase(name);
				} else {
					if (skinOwner.length() <= 16) {
						profile = (NPCProfile) NPCLibPlugin.getProfileClass().getMethod("loadProfile", String.class, String.class).invoke(null, name, skinOwner);
					} else if (skinOwner.length() > 32) {
						try {
							JSONObject json = new JSONObject(skinOwner);
							if (json.has("properties")) {
								json = json.getJSONArray("properties").getJSONObject(0);
							}
							String pName = "textures";
							String value = json.getString("value");
							String signature = null;
							if (json.has("signature")) {
								signature = json.getString("signature");
							}
							Object property = ClassBuilder.getNMUtilClass("com.mojang.authlib.properties.Property").getConstructor(String.class, String.class, String.class).newInstance(pName, value, signature);
							profile = NPCProfileBase.makeProfile(name, property);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (profile == null) { throw new IllegalArgumentException("Invalid skin owner"); }
			}
			npc = NPCType.PLAYER.wrapperClass.getConstructor(World.class, Location.class, NPCProfile.class).newInstance(loc.getWorld(), loc, profile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (npc != null) {
			if (NPCLib.useNickNamer) {
				NickNamerHook.setNick(npc.getUUID(), npc.getName());
				NickNamerHook.setSkin(npc.getUUID(), skinOwner);
			}

			npc.getBukkitEntity().setMetadata("NPCLib", new FixedMetadataValue(NPCLibPlugin.getInstance(), true));
		}

		return npc;
	}

	public static boolean isNPC(Entity entity) {
		if (entity == null) { return false; }
		return entity.hasMetadata("NPCLib");
	}

	public static int despawnAllNPCs() {
		int count = 0;
		for (World w : Bukkit.getWorlds()) {
			count += despawnNPCs(w);
		}
		return count;
	}

	public static int despawnNPCs(World world) {
		int count = 0;
		List<Entity> entities = Bukkit.getServer().getWorld(world.getUID()).getEntities();
		for (Entity ent : entities) {
			if (isNPC(ent)) {
				ent.remove();
				count++;
			}
		}
		return count;
	}

	public static synchronized List<NPC> getNPCs(World world) {
		List<NPC> list = new ArrayList<>();
		List<Entity> entities = Bukkit.getServer().getWorld(world.getUID()).getEntities();
		for (Entity ent : entities) {
			if (isNPC(ent)) {
				NPC npc = getNPC(ent);
				if (npc != null) {
					list.add(npc);
				}
			}
		}

		return list;
	}

	public static NPC getNPC(Entity ent) {
		if (!isNPC(ent)) { return null; }

		try {
			Object obj = Reflection.getHandleWithException(ent);
			if (obj instanceof NPC) { return (NPC) obj; } else if (obj instanceof NPCEntityNMS) { return ((NPCEntityNMS) obj).getNPC(); }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Deprecated
	public static int getServerVersion() {
		return serverVersion;
	}

	public static boolean useNickNamer() {
		return useNickNamer;
	}

}
