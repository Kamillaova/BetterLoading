package dev.xdark.betterloading.cache;

import net.minecraft.client.texture.NativeImage;

public final class NativeImageHolder {
  private int[] pixels;
  private final NativeImage image;

  public NativeImageHolder(NativeImage image) {
    this.image = image;
  }

  public NativeImage getImage() {
    return image;
  }

  @SuppressWarnings("deprecation")
  public int[] makePixelArray() {
    var pixels = this.pixels;
    if (pixels == null) {
      return this.pixels = image.makePixelArray();
    }
    return pixels;
  }
}
