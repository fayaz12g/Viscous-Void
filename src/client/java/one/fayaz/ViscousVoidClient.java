package one.fayaz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.registry.Registries;
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
        BlockRenderLayerMap.putFluid(VoidModFluids.VOID, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putFluid(VoidModFluids.FLOWING_VOID, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(VoidModFluids.VOID_BLOCK, BlockRenderLayer.TRANSLUCENT);

        // Register custom renderer with fog color control
        FluidRenderHandlerRegistry.INSTANCE.register(VoidModFluids.VOID, VoidModFluids.FLOWING_VOID,
                new SimpleFluidRenderHandler(
                        Identifier.of("minecraft:block/water_still"),
                        Identifier.of("minecraft:block/water_flow"),
                        0x808080
                ));

        // Ensure fluid + block are translucent
        BlockRenderLayerMap.putFluid(VoidModFluidsTwo.VOID_TWO, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putFluid(VoidModFluidsTwo.FLOWING_VOID_TWO, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(VoidModFluidsTwo.VOID_BLOCK_TWO, BlockRenderLayer.TRANSLUCENT);

        // Register custom renderer with fog color control
        FluidRenderHandlerRegistry.INSTANCE.register(VoidModFluidsTwo.VOID_TWO, VoidModFluidsTwo.FLOWING_VOID_TWO,
                new SimpleFluidRenderHandler(
                        Identifier.of("minecraft:block/water_still"),
                        Identifier.of("minecraft:block/water_flow"),
                        0xFF5556
                ));


        // Loop over all registered potion fluids in the maps
        for (var entry : ModFluids.FLUID_BLOCKS.entrySet()) {
            System.out.println("Looping through " + entry.getKey());
            var potion = entry.getKey();
            var block = entry.getValue();

            // Get corresponding still/flowing fluids
            var stillFluid = ModFluids.STILL_FLUIDS.get(potion);
            var flowingFluid = ModFluids.FLOWING_FLUIDS.get(potion);

            // Generate the ID you used when registering the block
            String potionId = Registries.POTION.getId(potion).getPath();
            Identifier blockId = Identifier.of(ViscousVoid.MOD_ID, potionId + "_block");

            System.out.println("Potion ID: " + potionId);
            System.out.println("Block ID: " + blockId);

            // Assign a unique color per potion
            int color;
            switch (potionId) {
                case "healing": color = 0xFF5555; break;
                case "regeneration": color = 0x55FF55; break;
                case "swiftness": color = 0x5555FF; break;
                case "long_slowness": color = 0xAAAAFF; break;
                case "strength": color = 0xFFAA00; break;
                case "poison": color = 0x33CC33; break;
                default: color = 0xFF5556; break;
            }

            System.out.println("Potion color: " + color);

            // Ensure fluid + block are translucent
            BlockRenderLayerMap.putBlock(block, BlockRenderLayer.TRANSLUCENT);
            BlockRenderLayerMap.putFluid(stillFluid, BlockRenderLayer.TRANSLUCENT);
            BlockRenderLayerMap.putFluid(flowingFluid, BlockRenderLayer.TRANSLUCENT);

            // Register custom renderer with fog color control
            FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, flowingFluid,
                    new SimpleFluidRenderHandler(
                            Identifier.of("minecraft:block/water_still"),
                            Identifier.of("minecraft:block/water_flow"),
                            color
                    ));
        }
    }
}

