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

package de.inventivegames.npc.entity.player;

import de.inventivegames.npc.NPCLibPlugin;
import de.inventivegames.npc.animation.NPCAnimation;
import de.inventivegames.npc.channel.ControllableChannel;
import de.inventivegames.npc.entity.NPCEntity;
import de.inventivegames.npc.entity.NPCEntityBase;
import de.inventivegames.npc.event.NPCSpawnEvent;
import de.inventivegames.npc.living.NPCPlayer;
import de.inventivegames.npc.profile.NPCProfile;
import de.inventivegames.npc.skin.Hand;
import de.inventivegames.npc.skin.SkinLayer;
import de.inventivegames.npc.util.*;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.reflection.minecraft.DataWatcher;
import org.inventivetalent.reflection.minecraft.Minecraft;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class NPCPlayerEntityBase extends NPCEntityBase implements NPCPlayer, NPCEntity {

	protected final UUID       uuid;
	protected       NPCProfile profile;

	/* If the player is show in the player list */
	private boolean showInList = false;
	/* ping (as shown in the player list) */
	private int     ping       = 1;

	/* If the entity is currently lying */
	private boolean lying = false;

	/**
	 * Base NPC entity which can be normally spawned but does not override any EntityPlayer methods and can therefore be damaged etc.
	 *
	 * @param world    {@link World} to spawn the entity in
	 * @param location {@link Location} to spawn the entity at
	 * @param profile  GameProfile to be assigned to the entity
	 */
	public NPCPlayerEntityBase(@Nonnull org.bukkit.World world, @Nonnull Location location, @Nonnull NPCProfile profile) throws Exception {
		super(world, location);
		if (profile == null) { throw new IllegalArgumentException("None of the arguments must be null!"); }

		this.profile = profile;
		this.uuid = (UUID) AccessUtil.setAccessible(NMSClass.classGameProfile.getDeclaredField("id")).get(this.profile.getProfile());
		this.name = (String) AccessUtil.setAccessible(NMSClass.classGameProfile.getDeclaredField("name")).get(this.profile.getProfile());
		this.interactManager = NMSClass.nmsPlayerInteractManager.getConstructor(NMSClass.nmsWorld).newInstance(Reflection.getHandle(world));
	}

	/**
	 * Initializes the entity with necessary values. Should be called after overriding all methods
	 *
	 * @param world    {@link World} to spawn the entity in
	 * @param location {@link Location} to spawn the entity at
	 */
	@Override
	protected void initialize(World world, Location location) throws Exception {
		/* Change gamemode (to survival) */
		AccessUtil.setAccessible(NMSClass.nmsPlayerInteractManager.getDeclaredMethod("b", NMSClass.nmsEnumGamemode)).invoke(NMSClass.nmsEntityPlayer.getDeclaredField("playerInteractManager").get(this.theEntity), NMSClass.nmsEnumGamemode.getEnumConstants()[0]);
		/* Set player connection */
		AccessUtil.setAccessible(NMSClass.nmsEntityPlayer.getDeclaredField("playerConnection")).set(this.theEntity, ClassBuilder.buildPlayerConnection(ClassBuilder.buildNetworkManager(false), this.theEntity));
		/* TODO Change something with sleep? */
		AccessUtil.setAccessible(NMSClass.nmsEntityHuman.getDeclaredField("fauxSleeping")).set(this.theEntity, true);
		/* Create the bukkit entity */
		AccessUtil.setAccessible(NMSClass.nmsEntity.getDeclaredField("bukkitEntity")).set(this.theEntity, NMSClass.obcCraftPlayer.getConstructor(NMSClass.obcCraftServer, NMSClass.nmsEntityPlayer).newInstance(Bukkit.getServer(), this.theEntity));

		if (Minecraft.VERSION.newerThan(Minecraft.Version.v1_8_R1)) {
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
		AccessUtil.setAccessible(NMSClass.nmsEntity.getDeclaredMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)).invoke(this.theEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		for (Player player : Bukkit.getOnlinePlayers()) {
			this.updatePlayerList(player);
		}

		Object worldServer = Reflection.getHandle(world);
		/* Add to the world entities */
		AccessUtil.setAccessible(NMSClass.nmsWorld.getDeclaredMethod("addEntity", NMSClass.nmsEntity)).invoke(worldServer, this.theEntity);

		/* But remove from the list of players */
		if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {//Removing the player in 1.9+ causes the update methods to not be called...
			((List) AccessUtil.setAccessible(NMSClass.nmsWorld.getDeclaredField("players")).get(worldServer)).remove(this.theEntity);
		}

		NPCSpawnEvent event = new NPCSpawnEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public Player getBukkitEntity() {
		return (Player) super.getBukkitEntity();
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public void setGameMode(GameMode mode) {
		try {
			AccessUtil.setAccessible(NMSClass.nmsPlayerInteractManager.getDeclaredMethod("setGameMode", NMSClass.nmsEnumGamemode)).invoke(NMSClass.nmsEntityPlayer.getDeclaredField("playerInteractManager").get(this.theEntity), NMSClass.nmsEnumGamemode.getEnumConstants()[mode.ordinal()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public GameMode getGameMode() {
		try {
			Object gamemode = AccessUtil.setAccessible(NMSClass.nmsPlayerInteractManager.getDeclaredMethod("getGameMode")).invoke(NMSClass.nmsEntityPlayer.getDeclaredField("playerInteractManager").get(this.theEntity));
			int i = (int) Enum.class.getMethod("ordinal").invoke(gamemode);
			return GameMode.values()[i];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GameMode.SURVIVAL;
	}

	@Override
	public void setShownInList(boolean flag) {
		if (this.showInList == flag) { return; }
		this.showInList = flag;
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.updatePlayerList(player);
		}
	}

	@Override
	public boolean isShownInList() {
		return this.showInList;
	}

	@Override
	public void setPing(int ping) {
		if (this.ping == ping) { return; }
		this.ping = ping;
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.updatePlayerList(player);
		}
	}

	@Override
	public int getPing() {
		return this.ping;
	}

	@Override
	public void despawn() {
		Bukkit.getScheduler().runTask(NPCLibPlugin.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					Object worldServer = Reflection.getHandle(getBukkitEntity().getWorld());

					//Basically the same as in net.minecraft.server.PlayerList#disconnect(EntityPlayer)
					AccessUtil.setAccessible(NMSClass.nmsWorld.getDeclaredMethod("kill", NMSClass.nmsEntity)).invoke(worldServer, theEntity);

					for (Player player : Bukkit.getOnlinePlayers()) {
						updatePlayerList(player);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		super.despawn();
	}

	@Override
	public void setLocation(Location location) {
		ControllableChannel controllableChannel = null;
		if (Minecraft.VERSION.newerThan(Minecraft.Version.v1_9_R1)) {
			//Let's hope this doesn't backfire at some point...
			try {
				Object connection = AccessUtil.setAccessible(NMSClass.nmsEntityPlayer.getDeclaredField("playerConnection")).get(this.theEntity);
				Object networkManager = AccessUtil.setAccessible(NMSClass.nmsPlayerConnection.getDeclaredField("networkManager")).get(connection);
				controllableChannel = (ControllableChannel) AccessUtil.setAccessible(NMSClass.nmsNetworkManager.getDeclaredField("channel")).get(networkManager);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			controllableChannel.setOpen(true);
		}
		this.getBukkitEntity().teleport(location);
		if (controllableChannel != null) { controllableChannel.setOpen(false); }
		setYaw(location.getYaw());
		setPitch(location.getPitch());
	}

	/*
	 * Packet methods
	 */

	@Override
	public void setLying(boolean flag) {
		if (flag == this.lying) { return; }
		if (flag) {
			try {
				this.setYaw(0);
				this.setPitch(0);
				for (Player p : this.getLocation().getWorld().getPlayers()) {
					this.sendBedPacket(p);
				}
				this.lying = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.playAnimation(NPCAnimation.LEAVE_BED);
			this.lying = false;
		}
	}

	@Override
	public void setSkinLayers(SkinLayer... active) {
		int i = 0;

		for (SkinLayer l : active) {
			i |= l.getShifted();
		}

		try {
			//			ClassBuilder.setDataWatcherValue(ClassBuilder.getDataWatcher(this.getBukkitEntity()), 10, (byte) i);
			DataWatcher.setValue(ClassBuilder.getDataWatcher(this.getBukkitEntity()), 10, DataWatcher.V1_9.ValueType.ENTITY_HUMAN_SKIN_LAYERS, (byte) i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setMainHand(Hand hand) {
		try {
			DataWatcher.setValue(ClassBuilder.getDataWatcher(this.getBukkitEntity()), 0, DataWatcher.V1_9.ValueType.ENTITY_HUMAN_MAIN_HAND, (byte) (hand == Hand.LEFT ? 0 : 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isLying() {
		return this.lying;
	}

	private void sendBedPacket(Player p) {
		int version = PlayerUtil.getVersion(p);
		Location bedLoc = this.getLocation();
		bedLoc.setY(1);
		/* Create a fake bed for all 1.8 players since the entity will not render lying otherwise */
		if (version >= 47) {// 47 = 1.8
			p.sendBlockChange(bedLoc, Material.BED_BLOCK, (byte) 0);
		}
		try {
			this.sendPacket(p, ClassBuilder.buildPacketPlayOutBed(this.getEntityID(), bedLoc.getBlockX(), bedLoc.getBlockY(), bedLoc.getBlockZ()));
			double height = 0.25;
			this.getBukkitEntity().teleport(new Location(this.getLocation().getWorld(), bedLoc.getBlockX(), this.getLocation().getBlockY(), this.getLocation().getBlockZ(), this.getBukkitEntity().getLocation().getYaw(), this.getBukkitEntity().getLocation().getPitch()));
			Object teleportPacket = ClassBuilder.buildPacketPlayOutEntityTeleport(this.getEntityID(), bedLoc.getBlockX(), this.getLocation().getBlockY() + height, this.getLocation().getBlockZ(), this.getBukkitEntity().getLocation().getYaw(), this.getBukkitEntity().getLocation().getPitch(), true, false);
			this.sendPacket(p, teleportPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePlayerList(final Player player) {
		if (player.getName().equals(this.getName())) {

		}
		try {
			this.sendPacket(player, ClassBuilder.buildPlayerInfoPacket(0, this.profile.getProfile(), this.getPing(), this.getGameMode().ordinal(), this.getName()));
			new BukkitRunnable() {

				@Override
				public void run() {
					if (!player.isOnline()) { return; }
					if (!NPCPlayerEntityBase.this.isShownInList() || getBukkitEntity().isDead()) {
						try {
							NPCPlayerEntityBase.this.sendPacket(player, ClassBuilder.buildPlayerInfoPacket(4, NPCPlayerEntityBase.this.profile.getProfile(), NPCPlayerEntityBase.this.getPing(), NPCPlayerEntityBase.this.getGameMode().ordinal(), NPCPlayerEntityBase.this.getName()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.runTaskLater(NPCLibPlugin.getInstance(), 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateToPlayer(final Player player) {
		try {
			this.updatePlayerList(player);
			new BukkitRunnable() {

				@Override
				public void run() {
					if (NPCPlayerEntityBase.this.isLying()) {
						NPCPlayerEntityBase.this.sendBedPacket(player);
					}
				}
			}.runTaskLater(NPCLibPlugin.getInstance(), 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
