package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.ResourceExt;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.IOException;

@Mixin(ResourceTexture.TextureData.class)
public abstract class TextureDataMixin {
  /**
   * @author xDark
   * @reason use image cache if possible
   */
  @Overwrite
  public static ResourceTexture.TextureData load(
    ResourceManager resourceManager,
    Identifier identifier
  ) {
    try {
      var resource = resourceManager.getResourceOrThrow(identifier);
      var image = ((ResourceExt) resource).readImage().getImage();
      TextureResourceMetadata textureResourceMetadata = null;

      try {
        textureResourceMetadata = resource.getMetadata().decode(TextureResourceMetadata.READER).orElse(null);
      } catch (RuntimeException ex) {
        ResourceTexture.LOGGER.warn("Failed reading metadata of: {}", identifier, ex);
      }
      return new ResourceTexture.TextureData(
        textureResourceMetadata,
        image
      );
    } catch (IOException ex) {
      return new ResourceTexture.TextureData(ex);
    }
  }
}
