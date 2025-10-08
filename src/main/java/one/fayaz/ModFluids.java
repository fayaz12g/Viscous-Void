package one.fayaz;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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

public class ModFluids {
    public static FlowableFluid VOID;
    public static FlowableFluid FLOWING_VOID;
    public static Block VOID_BLOCK;

    public static Item VOID_BUCKET;

    public static void register() {
        VOID = Registry.register(Registries.FLUID,
                Identifier.of(ViscousVoid.MOD_ID, "void_still"), new VoidFluid.Still());

        FLOWING_VOID = Registry.register(Registries.FLUID,
                Identifier.of(ViscousVoid.MOD_ID, "void_flow"), new VoidFluid.Flowing());

        VOID_BLOCK = Registry.register(Registries.BLOCK,
                Identifier.of(ViscousVoid.MOD_ID, "void_block"),
                new FluidBlock(ModFluids.VOID, AbstractBlock.Settings
                        .copy(Blocks.WATER)
                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ViscousVoid.MOD_ID, "void_block")))
                        .mapColor(MapColor.DULL_PINK)) {});


        VOID_BUCKET = Registry.register(Registries.ITEM,
                Identifier.of(ViscousVoid.MOD_ID, "void_bucket"), new BucketItem(ModFluids.VOID, new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ViscousVoid.MOD_ID, "void_bucket")))
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