package one.fayaz;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViscousVoid implements ModInitializer {
    public static final String MOD_ID = "viscous-void";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Dimension registry key
    public static final RegistryKey<World> VISCOUS_VOID_WORLD = RegistryKey.of(
            RegistryKeys.WORLD,
            Identifier.of(MOD_ID, "viscous_void")
    );

    static {
        // Register chunk generator in static initializer to ensure it happens early
        Registry.register(
                Registries.CHUNK_GENERATOR,
                id("viscous_void"),
                ViscousVoidChunkGenerator.CODEC
        );
        LOGGER.info("Chunk generator registered in static block");
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Viscous Void mod!");

        // Register fluids
        ModFluids.register();

        // Register dimension type
        ViscousVoidDimension.register();

        // Register portal handler
        ViscousVoidPortalHandler.register();

        LOGGER.info("Viscous Void dimension registered!");
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}