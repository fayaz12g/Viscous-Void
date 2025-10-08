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

        // Register renderer with tint color
        SimpleFluidRenderHandler handler = new SimpleFluidRenderHandler(stillTex, flowTex, fogColor);
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.VOID, ModFluids.FLOWING_VOID, handler);
    }
}
