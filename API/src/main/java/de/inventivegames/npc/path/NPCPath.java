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

package de.inventivegames.npc.path;

import de.inventivegames.npc.NPC;
import de.inventivegames.npc.NPCLib;
import de.inventivegames.npc.entity.NPCEntity;
import de.inventivegames.npc.living.NPCPlayer;
import de.inventivegames.npc.util.MathUtil;
import de.inventivegames.npc.util.NMSClass;
import de.inventivegames.npc.util.Reflection;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;

public class NPCPath {

	private final NPC    npc;
	private final Object pathEntity;
	private final double speed;
	private       double progress;

	private Object currNMSPoint;
	private Vector currentPoint;

	protected NPCPath(NPC npc, Object path, double speed) {
		this.npc = npc;
		this.pathEntity = path;
		this.speed = speed;
		this.progress = 0D;

		try {
			this.currNMSPoint = path.getClass().getDeclaredMethod("a", de.inventivegames.npc.util.Reflection.getNMSClass("Entity")).invoke(this.pathEntity, ((NPCEntity) npc).getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.currentPoint = this.getCurrentPoint();
	}

	/**
	 * Updates the path and its movement
	 *
	 * @return <code>true</code> if the movement is still active, <code>false</code> if the path is completed
	 */
	public boolean update() {
		int current = MathUtil.floor(this.progress);
		double d = this.progress - current;
		double d1 = 1 - d;

		try {
			if (d + this.speed < 1) {
				double dx = (this.currentPoint.getX() - this.npc.getLocation().getX()) * this.speed;
				double dz = (this.currentPoint.getZ() - this.npc.getLocation().getZ()) * this.speed;

				if (this.npc.hasCollision()) {
					dx += Math.random() / 10;
					dx += Math.random() / 10;
				}

				((NPCEntity) this.npc).move(dx, 0, dz);
				if(this.npc instanceof NPCPlayer) {
					((NPCEntity) this.npc).checkMovement(dx, 0, dz);
				}
				this.progress += this.speed;
			} else {
				double bx = (this.currentPoint.getX() - this.npc.getLocation().getX()) * d1;
				double bz = (this.currentPoint.getZ() - this.npc.getLocation().getZ()) * d1;

				this.pathEntity.getClass().getMethod("a").invoke(this.pathEntity);
				if (!(boolean) this.pathEntity.getClass().getMethod("b").invoke(this.pathEntity)) {
					try {
						this.currNMSPoint = this.pathEntity.getClass().getDeclaredMethod("a", de.inventivegames.npc.util.Reflection.getNMSClass("Entity")).invoke(this.pathEntity, ((NPCEntity) this.npc).getEntity());
					} catch (Exception e) {
						e.printStackTrace();
					}
					this.currentPoint = this.getCurrentPoint();

					double d2 = this.speed - d1;

					double dx = bx + (this.currentPoint.getX() - this.npc.getLocation().getX()) * d2;
					double dy = this.currentPoint.getY() - this.npc.getLocation().getY();
					double dz = bz + (this.currentPoint.getZ() - this.npc.getLocation().getZ()) * d2;

					if (this.npc.hasCollision()) {
						dx += Math.random() / 10;
						dx += Math.random() / 10;
					}

					((NPCEntity) this.npc).move(dx, dy, dz);
					if(this.npc instanceof NPCPlayer) {
						((NPCEntity) this.npc).checkMovement(dx, dy, dz);
					}
					this.progress += this.speed;
				} else {
					((NPCEntity) this.npc).move(bx, 0, bz);
					if(this.npc instanceof NPCPlayer) {
						((NPCEntity) this.npc).checkMovement(bx, 0, bz);
					}
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * @return {@link Vector} containing the current target point
	 */
	public Vector getCurrentPoint() {
		Vector vector = new Vector();

		try {
			vector.setX((double) Vec3DFieldResolver.resolve("x", "a").get(this.currNMSPoint));
			vector.setY((double) Vec3DFieldResolver.resolve("y", "b").get(this.currNMSPoint));
			vector.setZ((double) Vec3DFieldResolver.resolve("z", "c").get(this.currNMSPoint));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vector;
	}

	/**
	 * @param npc   {@link NPC} to find the path for
	 * @param to    target {@link Location}
	 * @param range maximum range to find the path in
	 * @param speed speed of the movement
	 * @return new {@link de.inventivegames.npc.path.NPCPath} instance
	 * @throws IllegalArgumentException if the speed is higher than 1
	 */
	public static NPCPath findPath(NPC npc, Location to, double range, double speed) {
		if (speed > 1) { throw new IllegalArgumentException("Speed must not be higher than 1"); }

		try {
			// double fromX = npc.getLocation().getX();
			// double fromY = npc.getLocation().getY();
			// double fromZ = npc.getLocation().getZ();

			double toX = to.getX();
			double toY = to.getY();
			double toZ = to.getZ();

			int i = (int) (range + 8.0D);

			Object pathEntity = null;

			if (npc instanceof NPCEntity) {
				if (((NPCEntity) npc).getPathfinder() != null) {
					if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_8_R1)) {
						pathEntity = nmsPathfinder.getMethod("a", nmsEntity, int.class, int.class, int.class, float.class).invoke(((NPCEntity) npc).getPathfinder(), ((NPCEntity) npc).getEntity(), toX, toY, toZ, (float) range);
					} else {
						Object posFrom = nmsBlockPosition.getConstructor(nmsEntity).newInstance(((NPCEntity) npc).getEntity());
						Object posTo = nmsBlockPosition.getConstructor(double.class, double.class, double.class).newInstance(toX, toY, toZ);

						Object chunkCache = nmsChunkCache.getConstructor(nmsWorld, nmsBlockPosition, nmsBlockPosition, int.class).newInstance(Reflection.getHandle(npc.getLocation().getWorld()), nmsBlockPosition.getMethod("a", int.class, int.class, int.class).invoke(posFrom, -i, -i, -i), nmsBlockPosition.getMethod("a", int.class, int.class, int.class).invoke(posTo, i, i, i), 0);

						if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
							pathEntity = nmsPathfinder.getMethod("a", nmsIBlockAccess, nmsEntity, nmsBlockPosition, float.class).invoke(((NPCEntity) npc).getPathfinder(), chunkCache, ((NPCEntity) npc).getEntity(), posTo, (float) range);
						} else {
							//TODO: 1.9 - Player
							if (!(npc instanceof NPCPlayer)) {
								pathEntity = nmsPathfinder.getMethod("a", nmsIBlockAccess, NMSClass.nmsEntityInsentient, nmsBlockPosition, float.class).invoke(((NPCEntity) npc).getPathfinder(), chunkCache, ((NPCEntity) npc).getEntity(), posTo, (float) range);
							}
						}
					}
				}
			}

			if (pathEntity != null) { return new NPCPath(npc, pathEntity, speed); } else { return null; }

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static NMSClassResolver nmsClassResolver = new NMSClassResolver();

	static FieldResolver Vec3DFieldResolver;

	protected static Class<?> nmsVec3D;
	protected static Class<?> nmsPathfinder;
	protected static Class<?> nmsEntity;
	protected static Class<?> nmsChunkCache;
	protected static Class<?> nmsBlockPosition;
	protected static Class<?> nmsWorld;
	protected static Class<?> nmsIBlockAccess;

	static {
		try {
			nmsVec3D = Reflection.getNMSClass("Vec3D");
			nmsPathfinder = Reflection.getNMSClass("Pathfinder");
			nmsEntity = Reflection.getNMSClass("Entity");
			nmsChunkCache = Reflection.getNMSClass("ChunkCache");

			try {
				nmsBlockPosition = Reflection.getNMSClassWithException("BlockPosition");// 1.8 only class
			} catch (Exception e) {
				if (NPCLib.getServerVersion() >= 180) {
					e.printStackTrace();
				}
			}

			nmsWorld = Reflection.getNMSClass("World");
			nmsIBlockAccess = Reflection.getNMSClass("IBlockAccess");

			Vec3DFieldResolver = new FieldResolver(nmsVec3D);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
