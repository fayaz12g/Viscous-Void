package one.fayaz;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PotionFluid extends FlowableFluid {

    @Override
    public Fluid getFlowing() {
        return VoidModFluids.FLOWING_VOID;
    }

    @Override
    public Fluid getStill() {
        return VoidModFluids.VOID;
    }

    @Override
    protected boolean isInfinite(ServerWorld world) {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        // soft ambient fog sound rarely
        if (!state.isStill() && random.nextInt(300) == 0) {
            world.playSoundClient(
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_WATER_AMBIENT,
                    SoundCategory.BLOCKS,
                    0.05F,
                    0.8F + random.nextFloat() * 0.2F,
                    false
            );
        }
    }

    public void onEntityCollision(FluidState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living) {
            Vec3d vel = entity.getVelocity();

            // Buoyant effect — prevents sinking
            double buoyancy = 0.04;
            double newY = vel.y + buoyancy;

            // Keep Y stable if trying to float still
            if (Math.abs(vel.y) < 0.02) newY = 0.0;

            // Faster swim speed horizontally
            double multiplier = 1.6;
            Vec3d newVel = new Vec3d(vel.x * multiplier, newY, vel.z * multiplier);
            entity.setVelocity(newVel);

            // Reset fall distance to avoid fall damage
            entity.fallDistance = 0.0F;

            // Enable swimming animation if in this fluid
            living.setSwimming(true);
        }
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected int getMaxFlowDistance(WorldView world) {
        return 2;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 2;
    }

    @Override
    public Item getBucketItem() {
        return VoidModFluids.VOID_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 15;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return VoidModFluids.VOID_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state) {
        return this instanceof Still;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    public boolean isIn(TagKey<Fluid> tag) {
        return tag.id().getNamespace().equals(ViscousVoid.MOD_ID) && tag.id().getPath().contains("void");
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    // Flowing state
    public static class Flowing extends PotionFluid {
        public Flowing(Potion potion) {
            super();
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(new Property[]{LEVEL});
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    // Still state
    public static class Still extends PotionFluid {
        public Still(Potion potion) {
            super();
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
