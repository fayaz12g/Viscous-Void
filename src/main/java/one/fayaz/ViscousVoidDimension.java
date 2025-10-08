package one.fayaz;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class ViscousVoidDimension {

    public static final RegistryKey<DimensionType> VISCOUS_VOID_TYPE = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE,
            ViscousVoid.id("viscous_void")
    );

    public static void register() {
        // The actual dimension type is registered via data files
        ViscousVoid.LOGGER.info("Dimension type key created: {}", VISCOUS_VOID_TYPE.getValue());
    }
}