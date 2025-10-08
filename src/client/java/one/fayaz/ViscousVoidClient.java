package one.fayaz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.Identifier;

public class ViscousVoidClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("ViscousVoidClient.onInitializeClient called!");

        // Try to manually access and modify FOG_MODIFIERS
        try {
            Class<?> fogRendererClass = Class.forName("net.minecraft.client.render.fog.FogRenderer");
            java.lang.reflect.Field fogModifiersField = fogRendererClass.getDeclaredField("FOG_MODIFIERS");
            fogModifiersField.setAccessible(true);
            java.util.List fogModifiers = (java.util.List) fogModifiersField.get(null);

            System.out.println("Current FOG_MODIFIERS count: " + fogModifiers.size());
            for (int i = 0; i < fogModifiers.size(); i++) {
                System.out.println("  [" + i + "] " + fogModifiers.get(i).getClass().getSimpleName());
            }

            // Find and replace WaterFogModifier
            for (int i = 0; i < fogModifiers.size(); i++) {
                if (fogModifiers.get(i).getClass().getSimpleName().equals("WaterFogModifier")) {
                    System.out.println("Found WaterFogModifier at index " + i + ", replacing...");
                    fogModifiers.set(i, new VoidFluidFogModifier());
                    System.out.println("Replaced! New modifier: " + fogModifiers.get(i).getClass().getSimpleName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to modify FOG_MODIFIERS: " + e.getMessage());
            e.printStackTrace();
        }

        // Ensure fluid + block are translucent
        BlockRenderLayerMap.putFluid(ModFluids.VOID, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putFluid(ModFluids.FLOWING_VOID, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(ModFluids.VOID_BLOCK, BlockRenderLayer.TRANSLUCENT);

        // Gray tint for the fog fluid
        int fogColor = 0x808080;

        // Block color tint (helps when rendering the block itself)
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> fogColor, ModFluids.VOID_BLOCK);

        // Proper texture identifiers for still/flow
        Identifier stillTex = Identifier.of(ViscousVoid.MOD_ID, "block/void_still");
        Identifier flowTex  = Identifier.of(ViscousVoid.MOD_ID, "block/void_flow");

        // Register custom renderer with fog color control
        CustomFluidRenderHandler handler = new CustomFluidRenderHandler(stillTex, flowTex, fogColor);
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.VOID, handler);
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.FLOWING_VOID, handler);
    }
}