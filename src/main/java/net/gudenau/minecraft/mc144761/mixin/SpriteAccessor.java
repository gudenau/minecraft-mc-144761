package net.gudenau.minecraft.mc144761.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Sprite.class)
public interface SpriteAccessor{
    @Accessor int getFrameTicks();
    @Accessor AnimationResourceMetadata getAnimationMetadata();
    @Accessor int getFrameIndex();
    @Accessor Sprite.Info getInfo();
    @Invoker void invokeUpload(int x, int y, NativeImage[] images);
}
