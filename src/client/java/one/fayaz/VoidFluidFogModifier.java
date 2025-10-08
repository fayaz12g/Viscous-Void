package one.fayaz;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.WaterFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
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

        // Similar to water fog but customizable
        data.environmentalStart = -8.0F;
        data.environmentalEnd = 196.0F;

        if (cameraEntity instanceof ClientPlayerEntity clientPlayerEntity) {
            data.environmentalEnd = data.environmentalEnd * Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
        }

        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
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
        System.out.println("VoidFluidFogModifier.getFogColor called! Returning RED");

        long l = Util.getMeasuringTimeMs();
        int i = ((Biome)world.getBiome(camera.getBlockPos()).value()).getWaterFogColor();
        if (updateTime < 0L) {
            waterFogColor = i;
            lerpedWaterFogColor = i;
            updateTime = l;
        }

        float f = MathHelper.clamp((float)(l - updateTime) / 5000.0F, 0.0F, 1.0F);
        int j = ColorHelper.lerp(f, lerpedWaterFogColor, waterFogColor);
        if (waterFogColor != i) {
            waterFogColor = i;
            lerpedWaterFogColor = j;
            updateTime = l;
        }

        // Force red for testing
        return 0xFF0000;
    }

    @Override
    public void onSkipped() {
        updateTime = -1L;
    }
}