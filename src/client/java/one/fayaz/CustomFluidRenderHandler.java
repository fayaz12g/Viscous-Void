package one.fayaz;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class CustomFluidRenderHandler implements FluidRenderHandler {
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final int tintColor;
    private Sprite[] sprites;

    public CustomFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, int tintColor) {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.tintColor = tintColor;
    }

    @Override
    public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return sprites;
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return tintColor;
    }

    @Override
    public void reloadTextures(SpriteAtlasTexture textureAtlas) {
        this.sprites = new Sprite[] {
                textureAtlas.getSprite(stillTexture),
                textureAtlas.getSprite(flowingTexture)
        };
    }
}