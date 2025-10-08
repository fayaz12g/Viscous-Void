package one.fayaz;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ViscousVoidChunkGenerator extends ChunkGenerator {

    public static final MapCodec<ViscousVoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, ViscousVoidChunkGenerator::new)
    );

    public ViscousVoidChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig,
                                                  StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(() -> {
            BlockState fog = ModFluids.VOID_BLOCK.getDefaultState();
            BlockState water = Blocks.WATER.getDefaultState();

            // Fill entire chunk with fog
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = chunk.getBottomY(); y < chunk.getTopSectionCoord() * 16; y++) {
                        chunk.setBlockState(new BlockPos(x, y, z), fog);
                    }
                }
            }

            return chunk;
        });
    }

    net.minecraft.util.math.random.Random random1 = new net.minecraft.util.math.random.Random() {
        @Override
        public Random split() {
            return null;
        }

        @Override
        public RandomSplitter nextSplitter() {
            return null;
        }

        @Override
        public void setSeed(long seed) {

        }

        @Override
        public int nextInt() {
            return 0;
        }

        @Override
        public int nextInt(int bound) {
            return 0;
        }

        @Override
        public long nextLong() {
            return 0;
        }

        @Override
        public boolean nextBoolean() {
            return false;
        }

        @Override
        public float nextFloat() {
            return 0;
        }

        @Override
        public double nextDouble() {
            return 0;
        }

        @Override
        public double nextGaussian() {
            return 0;
        }
    };

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        // Place End Cities during surface building phase
        long seed = chunk.getPos().toLong();
        java.util.Random random = new java.util.Random(seed);

        // 5% chance per chunk to place an End City
        if (random.nextFloat() < 0.05f) {
            int cityX = chunk.getPos().getStartX() + 8;
            int cityZ = chunk.getPos().getStartZ() + 8;
            int cityY = 50 + random.nextInt(80); // Y between 50-130

            BlockPos cityPos = new BlockPos(cityX, cityY, cityZ);

            // Try to place the End City structure (no platform needed)
            try {
                net.minecraft.structure.StructureTemplateManager templateManager =
                        region.toServerWorld().getStructureTemplateManager();

                // Get a random End City piece
                String[] endCityPieces = {
                        "end_city/base_floor",
                        "end_city/base_roof",
                        "end_city/tower_base",
                        "end_city/tower_piece",
                        "end_city/tower_top"
                };

                String pieceName = endCityPieces[random.nextInt(endCityPieces.length)];
                net.minecraft.util.Identifier structureId = net.minecraft.util.Identifier.of("minecraft", pieceName);

                var template = templateManager.getTemplateOrBlank(structureId);

                if (template != null) {
                    net.minecraft.structure.StructurePlacementData placementData =
                            new net.minecraft.structure.StructurePlacementData()
                                    .setRotation(net.minecraft.util.BlockRotation.values()[random.nextInt(4)])
                                    .setMirror(net.minecraft.util.BlockMirror.NONE)
                                    .setIgnoreEntities(false);

                    // Use flag 2 to place blocks without updates, allowing placement in liquid
                    template.place(region, cityPos, cityPos, placementData, random1, 2);
                }
            } catch (Exception e) {
                // Structure placement failed, continue without it
            }
        }
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {

    }

    @Override
    public void populateEntities(ChunkRegion region) {
        // No entities for now
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }


    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return world.getTopYInclusive();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        int height = world.getTopYInclusive() - world.getBottomY();
        BlockState[] states = new BlockState[height];
        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.WATER.getDefaultState();
        }
        return new VerticalBlockSample(world.getBottomY(), states);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

    }

    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        text.add("Viscous Void Generator");
    }
}