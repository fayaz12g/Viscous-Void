package one.fayaz;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block TEST_BLOCK = register(
            new Block(AbstractBlock.Settings.copy(Blocks.STONE)),
            "test_block",
            false
        );

    private static Block register(Block block, String name, Boolean shouldRegisterItem) {
        Identifier id = Identifier.of(ViscousVoid.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block.Settings settings = Block.Settings.copy(Blocks.COBBLESTONE).registryKey(key);

        if (shouldRegisterItem) {
            Item blockItem = register(
                new Item.Settings(),
                "test_item"
            );
        }

        return Registry.register(Registries.BLOCK, key, new Block(settings));
    }

    private static Item register(Item.Settings itemSettings, String name) {
        Identifier id = Identifier.of(ViscousVoid.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings()
                .useBlockPrefixedTranslationKey()
                .registryKey(key);

        return Registry.register(Registries.ITEM, key, new Item(settings));
    }

    public static void initialize() {}

}