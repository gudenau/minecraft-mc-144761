package net.gudenau.minecraft.mc144761.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.texture.Sprite$Interpolation")
public abstract class Sprite$InterpolationMixin implements AutoCloseable{
    @Shadow protected abstract int lerp(double delta, int to, int from);
    @Shadow private Sprite field_21757;

    @Shadow @Final private NativeImage[] images;

    @Shadow protected abstract int getPixelColor(int frameIndex, int layer, int x, int y);

    /**
     * @author gudenau
     * @reason Perf
     */
    @Overwrite
    private void apply(){
        SpriteAccessor spriteAccessor = (SpriteAccessor)field_21757;

        AnimationResourceMetadata metadata = spriteAccessor.getAnimationMetadata();
        int frameIndex = spriteAccessor.getFrameIndex();

        double delta = 1.0D - (double)spriteAccessor.getFrameTicks() / metadata.getFrameTime(frameIndex);
        int i = metadata.getFrameIndex(frameIndex);
        int j = metadata.getFrameCount() == 0 ? field_21757.getFrameCount() : metadata.getFrameCount();
        int k = metadata.getFrameIndex((frameIndex + 1) % j);
        if (i != k && k >= 0 && k < field_21757.getFrameCount()){
            for(int l = 0; l < images.length; ++l) {
                Sprite.Info info = spriteAccessor.getInfo();
                int m = info.getWidth() >> l;
                int n = info.getHeight() >> l;

                for(int y = 0; y < n; ++y) {
                    for(int x = 0; x < m; ++x) {
                        int dest = getPixelColor(i, l, x, y);
                        int source = getPixelColor(k, l, x, y);
                        int red = lerp(delta, dest >> 16 & 255, source >> 16 & 255);
                        int green = lerp(delta, dest >> 8 & 255, source >> 8 & 255);
                        int blue = lerp(delta, dest & 255, source & 255);
                        int alpha = lerp(delta, (dest >>> 24) & 255, (source >>> 24) & 255);
                        images[l].setPixelColor(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
                    }
                }
            }

            spriteAccessor.invokeUpload(0, 0, images);
        }
    }
}
