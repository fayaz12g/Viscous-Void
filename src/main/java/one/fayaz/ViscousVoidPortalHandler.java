package one.fayaz;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.*;

public class ViscousVoidPortalHandler {

    // Track players who are currently in a beacon beam to prevent instant re-teleport
    private static final Map<UUID, Long> TELEPORT_COOLDOWN = new HashMap<>();
    private static final long COOLDOWN_TICKS = 60; // 3 seconds

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkPortalActivation(player);
            }
        });

        ViscousVoid.LOGGER.info("Portal handler registered!");
    }

    private static void checkPortalActivation(ServerPlayerEntity player) {
        // Check cooldown
        UUID playerId = player.getUuid();
        Long lastTeleport = TELEPORT_COOLDOWN.get(playerId);
        long currentTime = player.getEntityWorld().getTime();

        if (lastTeleport != null && currentTime - lastTeleport < COOLDOWN_TICKS) {
            return;
        }

        // Check if player has Conduit Power
        if (!player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
            return;
        }

        // Check if player is swimming/in water
        if (!player.isSwimming() && !player.isTouchingWater()) {
            return;
        }

        // Check if player is in a beacon beam
        if (!isInBeaconBeam(player)) {
            return;
        }

        // Activate portal!
        teleportPlayer(player);
        TELEPORT_COOLDOWN.put(playerId, currentTime);
    }

    private static boolean isInBeaconBeam(ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        ServerWorld world = (ServerWorld) player.getEntityWorld();

        // Check upwards for a beacon beam
        // Beacon beams extend from the beacon up to build height
        for (int y = playerPos.getY(); y >= world.getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(playerPos.getX(), y, playerPos.getZ());

            // Check if there's a beacon at this position
            if (world.getBlockState(checkPos).getBlock() == Blocks.BEACON) {
                // Found a beacon below, now verify it's actually active
                return isBeaconActive(world, checkPos);
            }

            // Stop checking if we hit a non-transparent block that would block the beam
            if (!world.getBlockState(checkPos).isTransparent() &&
                    world.getBlockState(checkPos).getBlock() != Blocks.WATER &&
                    world.getBlockState(checkPos).getBlock() != Blocks.BEACON) {
                break;
            }
        }

        return false;
    }

    private static boolean isBeaconActive(ServerWorld world, BlockPos beaconPos) {
        // Check if beacon has a valid pyramid base
        // Check for at least one valid beacon base block in the layer below
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = beaconPos.add(x, -1, z);
                if (isBeaconBaseBlock(world, checkPos)) {
                    return true; // Found at least one valid base block
                }
            }
        }

        return false;
    }

    private static boolean isBeaconBaseBlock(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.IRON_BLOCK ||
                world.getBlockState(pos).getBlock() == Blocks.GOLD_BLOCK ||
                world.getBlockState(pos).getBlock() == Blocks.DIAMOND_BLOCK ||
                world.getBlockState(pos).getBlock() == Blocks.EMERALD_BLOCK ||
                world.getBlockState(pos).getBlock() == Blocks.NETHERITE_BLOCK;
    }

    private static final Set<UUID> ENTERED_VOID = new HashSet<>();


    private static void teleportPlayer(ServerPlayerEntity player) {
        ServerWorld currentWorld = (ServerWorld) player.getEntityWorld();
        RegistryKey<World> currentDimension = currentWorld.getRegistryKey();

        ServerWorld targetWorld;
        Vec3d targetPos;

        MinecraftServer server = player.getEntityWorld().getServer();

        // Determine target dimension
        if (currentDimension.equals(ViscousVoid.VISCOUS_VOID_WORLD)) {
            // Going back to Overworld
            targetWorld = server.getWorld(World.OVERWORLD);
            // Try to find the player's spawn point or bed
            targetPos = findSafeReturnPosition(player, targetWorld);
            ViscousVoid.LOGGER.info("Teleporting {} from Viscous Void to Overworld", player.getName().getString());
        } else {
            // Going to Viscous Void
            targetWorld = server.getWorld(ViscousVoid.VISCOUS_VOID_WORLD);
            // Enter at a safe position in the middle of the dimension
            targetPos = new Vec3d(player.getX(), 64, player.getZ());
            ViscousVoid.LOGGER.info("Teleporting {} from {} to Viscous Void",
                    player.getName().getString(), currentDimension.getValue());
        }

        if (targetWorld != null) {
            // If entering the Viscous Void for the first time
            if (targetWorld.getRegistryKey().equals(ViscousVoid.VISCOUS_VOID_WORLD)
                    && !ENTERED_VOID.contains(player.getUuid())) {

                BlockPos portalCenter = new BlockPos(
                        MathHelper.floor(targetPos.x),
                        MathHelper.floor(targetPos.y),
                        MathHelper.floor(targetPos.z)
                );

                buildPortalStructure(targetWorld, portalCenter);
                ENTERED_VOID.add(player.getUuid());
            }


            TeleportTarget teleportTarget = new TeleportTarget(
                    targetWorld,
                    targetPos,
                    Vec3d.ZERO,
                    player.getYaw(),
                    player.getPitch(),
                    TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET
            );
            player.teleportTo(teleportTarget);
            player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
        }

    }

    record PortalLink(BlockPos overworldPos, BlockPos voidPos) {}

    private static BlockPos findExistingPortal(ServerWorld world, BlockPos searchCenter, int radius) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos closest = null;
        double closestDist = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mutable.set(searchCenter.getX() + x, searchCenter.getY() + y, searchCenter.getZ() + z);
                    if (world.getBlockState(mutable).getBlock() == Blocks.BEACON) {
                        double dist = mutable.getSquaredDistance(searchCenter);
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = mutable.toImmutable();
                        }
                    }
                }
            }
        }
        return closest;
    }


    private static void buildPortalStructure(ServerWorld world, BlockPos basePos) {
        // --- Base pyramid layer (3x3 of iron) ---
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(basePos.add(x, -1, z), Blocks.IRON_BLOCK.getDefaultState());
            }
        }

        // --- Beacon at the center ---
        world.setBlockState(basePos, Blocks.BEACON.getDefaultState());

        // --- Conduit Frame Setup ---
        // Frame now sits directly above the beacon with a 1-block air gap
        BlockPos frameBase = basePos.up(2); // beacon (y) + 1 air + 1 frame base

        BlockState prismarine = Blocks.PRISMARINE.getDefaultState();

        // --- Cross shape frame ---
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // Cross shape — skip corners and center
                if ((Math.abs(x) == 2 && z != 0) || (Math.abs(z) == 2 && x != 0)) continue;
                if (x == 0 && z == 0) continue;
                world.setBlockState(frameBase.add(x, 0, z), prismarine);
            }
        }

        // --- Vertical arms at ends of cross ---
        for (int[] offset : new int[][]{
                {2, 0}, {-2, 0}, {0, 2}, {0, -2}
        }) {
            BlockPos armBase = frameBase.add(offset[0], 0, offset[1]);
            world.setBlockState(armBase.up(1), prismarine);
            world.setBlockState(armBase.up(2), prismarine);
        }

        // --- Sea Lanterns at the tips around the conduit ---
        for (int[] offset : new int[][]{
                {2, 0}, {-2, 0}, {0, 2}, {0, -2}
        }) {
            BlockPos tipPos = frameBase.add(offset[0], 1, offset[1]); // middle height of arms
            world.setBlockState(tipPos, Blocks.SEA_LANTERN.getDefaultState());
        }

        // --- Conduit placement ---
        BlockPos conduitPos = basePos.up(3); // one block above the frame, directly above beacon
        world.setBlockState(conduitPos, Blocks.CONDUIT.getDefaultState());

        // --- Fill surrounding area with water ---
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 5; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos fillPos = basePos.add(x, y, z);
                    if (world.isAir(fillPos)) {
                        world.setBlockState(fillPos, Blocks.WATER.getDefaultState());
                    }
                }
            }
        }

        // --- Keep vertical beam above beacon clear ---
        for (int y = 1; y <= 3; y++) {
            world.setBlockState(basePos.up(y), Blocks.WATER.getDefaultState());
        }

        ViscousVoid.LOGGER.info("Constructed Viscous Void portal structure at {}", basePos);
    }


    private static Vec3d findSafeReturnPosition(ServerPlayerEntity player, ServerWorld overworld) {
        // Try to use spawn point first - check if player has a spawn point set
        BlockPos spawnPos = player.getWorldSpawnPos(overworld, player.getBlockPos());
        if (spawnPos != null) {
            return new Vec3d(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        }

        // Fall back to world spawn
        BlockPos worldSpawn = overworld.getSpawnPoint().getPos();
        return new Vec3d(worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5);
    }
}