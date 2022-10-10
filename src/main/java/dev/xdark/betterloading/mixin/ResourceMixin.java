package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourceExt;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Mixin(Resource.class)
public abstract class ResourceMixin implements ResourceExt {
  @Shadow
  public abstract InputStream getInputStream();

  @Override
  public NativeImageHolder readImage() throws IOException {
    return new NativeImageHolder(NativeImage.read(getInputStream()));
  }

  @Override
  public JsonUnbakedModel readUnbakedModel() throws IOException {
    return JsonUnbakedModelDeserializer.INSTANCE.read(
      IOUtil.toJsonReader(
        getInputStream(),
        StandardCharsets.UTF_8
      )
    );
  }

  @Redirect(
    method = "getReader",
    at = @At(
      value = "NEW",
      target = "(Ljava/io/Reader;)Ljava/io/BufferedReader;"
    )
  )
  private static BufferedReader redirectNewBufferedReader(Reader reader) {
    return IOUtil.toBufferedReader(reader);
  }
}
