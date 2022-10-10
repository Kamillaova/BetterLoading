package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.font.UnicodeFontLoaderTemplates;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Arrays;

@Mixin(UnicodeTextureFont.class)
public abstract class UnicodeTextureFontMixin {
  @Shadow @Final static Logger LOGGER;
  @Shadow @Final private ResourceManager resourceManager;
  @Shadow @Final private String template;

  @Shadow
  private static native int getStart(byte size);

  @Shadow
  private static native int getEnd(byte size);

  @ModifyConstant(method = "<init>", constant = @Constant(intValue = 256, ordinal = 0))
  private int modifyLoop(int original) {
    return 0;
  }

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(
    ResourceManager resourceManager,
    byte[] sizes,
    String template,
    CallbackInfo ci
  ) {
    for (var i = 0; i < 256; i++) {
      var index = i * 256;
      NativeImageHolder $image;
      parse:
      try {
        if (($image = ((ResourceFactoryExt) resourceManager).tryGetNativeImage(getImageId(index)))
          != null) {
          var image = $image.getImage();
          if (image.getWidth() != 256 || image.getHeight() != 256) {
            break parse;
          }
          for (var k = 0; k < 256; ++k) {
            var b = sizes[index + k];
            if (b != 0 && getStart(b) > getEnd(b)) {
              sizes[index + k] = 0;
            }
          }
        }
      } catch (IOException ex) {
        LOGGER.warn("Could not read unicode page {}", index, ex);
      }
      Arrays.fill(sizes, index, index + 256, (byte) 0);
    }
  }

  /**
   * @author xDark
   * @reason use image cache if possible
   */
  @Nullable
  @Overwrite
  private NativeImage getGlyphImage(Identifier glyphId) {
    try {
      return ((ResourceFactoryExt) this.resourceManager).getNativeImage(glyphId).getImage();
    } catch (IOException ex) {
      LOGGER.error("Couldn't load texture {}", glyphId, ex);
      return null;
    }
  }

  /**
   * @author xDark
   * @reason remove {@link String#format(String, Object...)} calls
   */
  @Overwrite
  private Identifier getImageId(int codePoint) {
    var identifier = UnicodeFontLoaderTemplates.create(this.template, codePoint);
    return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
  }
}
