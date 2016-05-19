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

import de.inventivegames.npc.NPCLib.NPCType;
import de.inventivegames.npc.entity.NPCEntity;
import de.inventivegames.npc.entity.NPCEntityBase;
import de.inventivegames.npc.event.NPCInteractEvent.InteractType;
import de.inventivegames.npc.util.NMSClass;
import de.inventivegames.npc.util.Reflection;
import de.inventivegames.packetlistener.handler.PacketHandler;
import de.inventivegames.packetlistener.handler.PacketOptions;
import de.inventivegames.packetlistener.handler.ReceivedPacket;
import de.inventivegames.packetlistener.handler.SentPacket;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.reflection.util.AccessUtil;

import java.lang.reflect.Field;
import java.util.*;

public class NPCLibPlugin extends JavaPlugin implements Listener {

	private static   NPCLibPlugin instance;
	protected static boolean      enabled;

	protected static Class<?> profileClass;
	protected static Class<?> metaStoreClass;

	protected static PacketHandler packetHandler;

	@Override
	public void onEnable() {
		instance = this;

		NPCLib.serverVersionString = Reflection.getVersion().substring(Reflection.getVersion().substring(Reflection.getVersion().length() - 1).lastIndexOf("."));
		NPCLib.serverVersionString = NPCLib.serverVersionString.substring(0, NPCLib.serverVersionString.length() - 1);

		this.getLogger().info("Server version is " + NPCLib.serverVersionString);

		if (Reflection.getVersion().contains("1_9")) {
			NPCLib.serverVersion = 190;
		}
		if (Reflection.getVersion().contains("1_8")) {
			NPCLib.serverVersion = 180;
		}
		if (Reflection.getVersion().contains("1_8_R1")) {
			NPCLib.serverVersion = 181;
		}
		if (Reflection.getVersion().contains("1_8_R2")) {
			NPCLib.serverVersion = 182;
		}
		if (Reflection.getVersion().contains("1_8_R3")) {
			NPCLib.serverVersion = 183;
		}
		if (Reflection.getVersion().contains("1_7")) {
			NPCLib.serverVersion = 170;
		}

		try {
			profileClass = Class.forName("de.inventivegames.npc.profile.NPCProfile_" + NPCLib.serverVersionString.substring(0, NPCLib.serverVersionString.lastIndexOf("_R")));
			this.getLogger().info("Found compatible Profile class: " + profileClass.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			metaStoreClass = Class.forName("de.inventivegames.npc.meta.UUIDMetadataStore_" + NPCLib.serverVersionString);
			this.getLogger().info("Found compatible Metadata class: " + metaStoreClass.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.getLogger().info("Compatible NPC types: " + Arrays.toString(NPCType.values()));

		try {
			Field metaField = AccessUtil.setAccessible(Reflection.getOBCClass("CraftServer").getDeclaredField("playerMetadata"));
			Object meta = metaField.get(Bukkit.getServer());
			if (!meta.getClass().equals(metaStoreClass)) {
				metaField.set(Bukkit.getServer(), meta);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Bukkit.getPluginManager().isPluginEnabled("NickNamer")) {
			this.getLogger().info("Found NickNamer plugin");
			NickNamerHook.enable();
			NPCLib.useNickNamer = NickNamerHook.enabled;
		}

		PacketHandler.addHandler(packetHandler = new PacketHandler(getInstance()) {

			@Override
			@PacketOptions(forcePlayer = true)
			public void onSend(SentPacket packet) {
				if (packet.hasPlayer()) {
					if (packet.getPacketName().equals("PacketPlayOutNamedEntitySpawn")) {
						Object gameProfile = packet.getPacketValue("b");
						try {
							UUID id = (UUID) (NPCLib.getServerVersion() < 180 ? AccessUtil.setAccessible(NMSClass.classGameProfile.getDeclaredField("id")).get(gameProfile) : gameProfile);
							Player npcEntity = null;
							Iterator<Player> iterator = NPCLibPlugin.this.getPlayersInWorld(packet.getPlayer().getWorld()).iterator();
							while (iterator.hasNext()) {
								Player p = iterator.next();
								if (p.getUniqueId().equals(id)) {
									npcEntity = p;
									break;
								}
							}
							if (npcEntity != null) {
								if (NPCLib.isNPC(npcEntity)) {
									NPC npc = NPCLib.getNPC(npcEntity);
									if (npc != null) {
										((NPCEntity) npc).updateToPlayer(packet.getPlayer());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			@PacketOptions(forcePlayer = true)
			public void onReceive(ReceivedPacket packet) {
				if (packet.hasPlayer()) {
					if (packet.getPacketName().equals("PacketPlayInUseEntity")) {
						int id = (int) packet.getPacketValue("a");
						Object useAction = packet.getPacketValue("action");
						InteractType action = InteractType.fromUseAction(useAction);
						if (action == InteractType.UNKNOWN) {
							return;// UNKNOWN means an invalid packet, so just ignore it
						}
						for (NPC npc : NPCLib.getNPCs(packet.getPlayer().getWorld())) {
							if (npc.getEntityID() == id) {
								if (npc instanceof NPCEntityBase) {
									((NPCEntityBase) npc).onInteract(packet.getPlayer(), action);
								}
								//								NPCInteractEvent event = new NPCInteractEvent(npc, packet.getPlayer(), action);
								//								Bukkit.getPluginManager().callEvent(event);
								//								packet.setCancelled(event.isCancelled());
								break;
							}
						}
					}
				}
			}
		});

		Bukkit.getPluginManager().registerEvents(this, this);

		enabled = true;
	}

	protected Collection<Player> getPlayersInWorld(World w) {
		List<Player> list = new ArrayList<>();
		if (w == null) { return list; }
		List<Entity> ents = Bukkit.getServer().getWorld(w.getUID()).getEntities();
		for (Entity ent : ents) {
			if (ent instanceof Player) {
				list.add((Player) ent);
			}
		}
		return list;
	}

	@Override
	public void onDisable() {
		enabled = false;

		PacketHandler.removeHandler(packetHandler);

		int count = NPCLib.despawnAllNPCs();
		this.getLogger().info("Removed " + count + " NPCs");
	}

	public static Plugin getInstance() {
		return instance;
	}

	public static boolean enabled() {
		return enabled;
	}

	protected static Class<?> getProfileClass() {
		return profileClass;
	}

}
