package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import net.minecraft.resource.metadata.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.Reader;

@Mixin(ResourceMetadata.class)
public interface ResourceMetadataMixin {
  @Redirect(
    method = "create",
    at = @At(
      value = "NEW",
      target = "(Ljava/io/Reader;)Ljava/io/BufferedReader;"
    )
  )
  private static BufferedReader redirectNewBufferedReader(Reader reader) {
    return IOUtil.toBufferedReader(reader);
  }
}
