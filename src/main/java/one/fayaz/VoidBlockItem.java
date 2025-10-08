package one.fayaz;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VoidBlockItem extends BlockItem {
    public VoidBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();

        if (player != null) {
            FluidState fluidState = world.getFluidState(player.getBlockPos());
            if (fluidState.getFluid() == VoidModFluids.VOID || fluidState.getFluid() == VoidModFluids.FLOWING_VOID) {
                // Allow placement if touching a VOID_STILL block
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = pos.offset(dir);
                    if (world.getFluidState(neighbor).getFluid() == VoidModFluids.VOID) {
                        BlockState state = this.getBlock().getDefaultState();
                        world.setBlockState(pos, state, Block.NOTIFY_ALL_AND_REDRAW);
                        stack.decrement(1);
                        return ActionResult.SUCCESS;
                    }
                }
                return ActionResult.FAIL;
            }
        }

        // Otherwise, default behavior
        return super.place(context);
    }
}
