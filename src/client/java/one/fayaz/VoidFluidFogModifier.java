package one.fayaz;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.WaterFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class VoidFluidFogModifier extends WaterFogModifier {
    private static final int field_60592 = 96;
    private static final float field_60593 = 5000.0F;
    private static int waterFogColor = -1;
    private static int lerpedWaterFogColor = -1;
    private static long updateTime = -1L;

    private static boolean hasLogged = false;

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
        if (!hasLogged) {
            System.out.println("VoidFluidFogModifier.applyStartEndModifier called!");
            hasLogged = true;
        }

        // Default fog
        data.environmentalStart = -8.0F;
        data.environmentalEnd = 96.0F;

        if (cameraEntity instanceof ClientPlayerEntity player) {
            // Detect if player is in your custom fluid
            FluidState fluidState = cameraEntity.getEntityWorld().getFluidState(cameraEntity.getBlockPos());
            if (fluidState.getFluid() == VoidModFluids.VOID || fluidState.getFluid() == VoidModFluids.FLOWING_VOID) {
                // Almost no fog
                data.environmentalStart = 0.0F;
                data.environmentalEnd = 100000.0F; // Very far away, effectively full render distance
                data.skyEnd = data.environmentalEnd;
                data.cloudEnd = data.environmentalEnd;
            } else {
                // Keep water-like behavior for other fluids
                data.environmentalEnd *= Math.max(0.25F, player.getUnderwaterVisibility());
            }
        }
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        boolean result = submersionType == CameraSubmersionType.WATER;
        if (result && !hasLogged) {
            System.out.println("VoidFluidFogModifier.shouldApply returned true!");
        }
        return result;
    }

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        if (camera.getFocusedEntity() instanceof ClientPlayerEntity player) {
            FluidState fluidState = player.getEntityWorld().getFluidState(player.getBlockPos());
            if (fluidState.getFluid() == VoidModFluids.VOID || fluidState.getFluid() == VoidModFluids.FLOWING_VOID) {
                // Very slight gray tint
                return 0x202020;
            }
        }
        // Default water fog
        return super.getFogColor(world, camera, viewDistance, skyDarkness);
    }

    @Override
    public void onSkipped() {
        updateTime = -1L;
    }
}