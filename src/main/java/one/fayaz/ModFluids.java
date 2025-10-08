package one.fayaz;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class ModFluids {

    // Maps to hold all potion fluids
    public static final Map<Potion, FlowableFluid> STILL_FLUIDS = new HashMap<>();
    public static final Map<Potion, FlowableFluid> FLOWING_FLUIDS = new HashMap<>();
    public static final Map<Potion, Block> FLUID_BLOCKS = new HashMap<>();
    public static final Map<Potion, Item> BUCKETS = new HashMap<>();

    public static void register() {
        // Iterate through all potions
        for (Potion potion : Registries.POTION) {
            String potionId = Registries.POTION.getId(potion).getPath();

            // Register still fluid
            FlowableFluid still = Registry.register(
                    Registries.FLUID,
                     Identifier.of(ViscousVoid.MOD_ID, potionId + "_still"),
                    new PotionFluid.Still(potion)
            );
            STILL_FLUIDS.put(potion, still);

            // Register flowing fluid
            FlowableFluid flowing = Registry.register(
                    Registries.FLUID,
                     Identifier.of(ViscousVoid.MOD_ID, potionId + "_flow"),
                    new PotionFluid.Flowing(potion)
            );
            FLOWING_FLUIDS.put(potion, flowing);

            // Register fluid block
            Block block = Registry.register(
                    Registries.BLOCK,
                     Identifier.of(ViscousVoid.MOD_ID, potionId + "_block"),
                    new FluidBlock(still, AbstractBlock.Settings
                            .copy(Blocks.WATER)
                            .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                                     Identifier.of(ViscousVoid.MOD_ID, potionId + "_block")))
                    ) {}
            );
            FLUID_BLOCKS.put(potion, block);

            // Register bucket
            Item bucket = Registry.register(
                    Registries.ITEM,
                     Identifier.of(ViscousVoid.MOD_ID, potionId + "_bucket"),
                    new BucketItem(still, new Item.Settings()
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                                     Identifier.of(ViscousVoid.MOD_ID, potionId + "_bucket")))
                            .recipeRemainder(Items.BUCKET).maxCount(1))
            );
            BUCKETS.put(potion, bucket);
        }
    }

}
