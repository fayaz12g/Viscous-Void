package one.fayaz;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Iterator;

public class VoidModFluidsTwo {
    public static FlowableFluid VOID_TWO;
    public static FlowableFluid FLOWING_VOID_TWO;
    public static Block VOID_BLOCK_TWO;

    public static Item VOID_BUCKET_TWO;

    public static void register() {
        VOID_TWO = Registry.register(Registries.FLUID,
                Identifier.of(ViscousVoid.MOD_ID, "void_two_still"), new VoidFluid.Still());

        FLOWING_VOID_TWO = Registry.register(Registries.FLUID,
                Identifier.of(ViscousVoid.MOD_ID, "void_two_flow"), new VoidFluid.Flowing());

        VOID_BLOCK_TWO = Registry.register(Registries.BLOCK,
                Identifier.of(ViscousVoid.MOD_ID, "void_two_block"),
                new FluidBlock(VoidModFluidsTwo.VOID_TWO, AbstractBlock.Settings
                        .copy(Blocks.WATER)
                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ViscousVoid.MOD_ID, "void_two_block")))
                        .mapColor(MapColor.DULL_PINK)) {});


        VOID_BUCKET_TWO = Registry.register(Registries.ITEM,
                Identifier.of(ViscousVoid.MOD_ID, "void_two_bucket"), new BucketItem(VoidModFluidsTwo.VOID_TWO, new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ViscousVoid.MOD_ID, "void_two_bucket")))
                        .recipeRemainder(Items.BUCKET).maxCount(1)));
    }

    static {
        Iterator var0 = Registries.FLUID.iterator();

        while(var0.hasNext()) {
            Fluid fluid = (Fluid)var0.next();
            UnmodifiableIterator var2 = fluid.getStateManager().getStates().iterator();

            while(var2.hasNext()) {
                FluidState fluidState = (FluidState)var2.next();
                Fluid.STATE_IDS.add(fluidState);
            }
        }

    }
}