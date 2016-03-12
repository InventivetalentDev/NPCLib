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

import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSClass {

	private static boolean initialited = false;

	public static NMSClassResolver nmsClassResolver = new NMSClassResolver();

	public static Class<?> nmsEntityTypes;
	public static Class<?> nmsEntity;
	public static Class<?> nmsEntityHuman;
	public static Class<?> nmsEntityLiving;
	public static Class<?> nmsEntityInsentient;
	public static Class<?> nmsEntityPlayer;
	public static Class<?> nmsEntityZombie;
	public static Class<?> nmsMinecraftServer;
	public static Class<?> nmsWorldServer;
	public static Class<?> nmsWorld;
	public static Class<?> nmsPlayerInteractManager;
	public static Class<?> nmsEnumGamemode;
	public static Class<?> nmsPlayerConnection;
	public static Class<?> nmsPathfinder;
	public static Class<?> nmsPathfinderAbstract;
	public static Class<?> nmsPathfinderNormal;
	public static Class<?> nmsDamageSource;
	public static Class<?> nmsPacketPlayOutBed;
	public static Class<?> nmsPacketPlayOutAnimation;
	public static Class<?> nmsPacketPlayOutEntityEquipment;
	public static Class<?> nmsPacketPlayOutEntityTeleport;
	public static Class<?> nmsPathfinderGoalSelector;
	public static Class<?> nmsDataWatcher;
	public static Class<?> nmsWatchableObject;

	public static Class<?> obcCraftServer;
	public static Class<?> obcCraftWorld;
	public static Class<?> obcCraftPlayer;
	public static Class<?> obcCraftZombie;
	public static Class<?> obcCraftItemStack;

	public static Class<?> classGameProfile;
	public static Class<?> ioNettyChannel;
	public static Class<?> comGoogleCommonCollectForwardingMap;
	public static Class<?> comGoogleCommonCollectForwardingMultimap;
	public static Class<?> comGoogleCommonCacheLoadingCache;

	protected static Field nmsFieldPlayerConnection;
	protected static Field nmsFieldNetworkManager;
	protected static Field nmsFieldNetworkManagerI;
	protected static Field nmsFieldNetworkManagerM;

	protected static Method nmsSendPacket;
	protected static Method nmsChatSerializerA;
	protected static Method nmsNetworkGetVersion;

	public static Class<?> nmMinecraftSessionService;

	static {
		if (!initialited) {
			try {
				nmsEntityTypes = Reflection.getNMSClass("EntityTypes");
				nmsEntity = Reflection.getNMSClass("Entity");
				nmsEntityHuman = Reflection.getNMSClass("EntityHuman");
				nmsEntityLiving = Reflection.getNMSClass("EntityLiving");
				nmsEntityInsentient = Reflection.getNMSClass("EntityInsentient");
				nmsEntityPlayer = Reflection.getNMSClass("EntityPlayer");
				nmsEntityZombie = Reflection.getNMSClass("EntityZombie");
				nmsMinecraftServer = Reflection.getNMSClass("MinecraftServer");
				nmsWorldServer = Reflection.getNMSClass("WorldServer");
				nmsWorld = Reflection.getNMSClass("World");
				nmsPlayerInteractManager = Reflection.getNMSClass("PlayerInteractManager");
//				try {
//					nmsEnumGamemode = Reflection.getNMSClassWithException("EnumGamemode");
//				} catch (Exception e1) {
//					nmsEnumGamemode = Reflection.getNMSClass("WorldSettings$EnumGamemode");
//				}
				nmsEnumGamemode = nmsClassResolver.resolve("EnumGamemode", "WorldSettings$EnumGamemode");
				nmsPlayerConnection = Reflection.getNMSClass("PlayerConnection");
				nmsPathfinder = Reflection.getNMSClass("Pathfinder");
				nmsDataWatcher = Reflection.getNMSClass("DataWatcher");
//				nmsWatchableObject = Reflection.getVersion().contains("1_7") || Reflection.getVersion().contains("1_8_R1") ? Reflection.getNMSClass("WatchableObject") : Reflection.getNMSClass("DataWatcher$WatchableObject");
				nmsWatchableObject = nmsClassResolver.resolveSilent("WatchableObject", "DataWatcher$WatchableObject");//Ignore exceptions, since it's not available in 1.9
				try {
					nmsPathfinderAbstract = Reflection.getNMSClassWithException("PathfinderAbstract");
					nmsPathfinderNormal = Reflection.getNMSClassWithException("PathfinderNormal");
				} catch (Exception e) {// Only a 1.8+ class
				}
				nmsDamageSource = Reflection.getNMSClass("DamageSource");
				nmMinecraftSessionService = ClassBuilder.getNMUtilClass("com.mojang.authlib.minecraft.MinecraftSessionService");
				nmsPacketPlayOutBed = Reflection.getNMSClass("PacketPlayOutBed");
				nmsPacketPlayOutAnimation = Reflection.getNMSClass("PacketPlayOutAnimation");
				nmsPacketPlayOutEntityEquipment = Reflection.getNMSClass("PacketPlayOutEntityEquipment");
				nmsPacketPlayOutEntityTeleport = Reflection.getNMSClass("PacketPlayOutEntityTeleport");
				nmsPathfinderGoalSelector = Reflection.getNMSClass("PathfinderGoalSelector");

				obcCraftServer = Reflection.getOBCClass("CraftServer");
				obcCraftWorld = Reflection.getOBCClass("CraftWorld");
				obcCraftPlayer = Reflection.getOBCClass("entity.CraftPlayer");
				obcCraftZombie = Reflection.getOBCClass("entity.CraftZombie");
				obcCraftItemStack = Reflection.getOBCClass("inventory.CraftItemStack");

				classGameProfile = ClassBuilder.getNMUtilClass("com.mojang.authlib.GameProfile");
				ioNettyChannel = ClassBuilder.getNMUtilClass("io.netty.channel.Channel");
				comGoogleCommonCollectForwardingMap = ClassBuilder.getNMUtilClass("com.google.common.collect.ForwardingMap");
				comGoogleCommonCollectForwardingMultimap = ClassBuilder.getNMUtilClass("com.google.common.collect.ForwardingMultimap");
				comGoogleCommonCacheLoadingCache = ClassBuilder.getNMUtilClass("com.google.common.cache.LoadingCache");

				nmsFieldPlayerConnection = Reflection.getField(nmsEntityPlayer, "playerConnection");
				nmsFieldNetworkManager = Reflection.getField(nmsPlayerConnection, "networkManager");
				nmsFieldNetworkManagerI = Reflection.getField(nmsFieldNetworkManager.getType(), "i");
				nmsFieldNetworkManagerM = Reflection.getField(nmsFieldNetworkManager.getType(), "m");

				nmsSendPacket = Reflection.getMethod(nmsPlayerConnection, "sendPacket");
				nmsNetworkGetVersion = Reflection.getMethod(nmsFieldNetworkManager.getType(), "getVersion", ioNettyChannel);

				initialited = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
