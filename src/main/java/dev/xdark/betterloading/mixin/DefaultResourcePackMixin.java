package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.cache.ResourceCache;
import dev.xdark.betterloading.internal.DefaultResourcePackExt;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Mixin(DefaultResourcePack.class)
public abstract class DefaultResourcePackMixin implements DefaultResourcePackExt, ResourcePackExt, ResourceFactoryExt {
  @Shadow
  @Nullable
  protected abstract InputStream findInputStream(ResourceType type, Identifier id);

  private ResourceCache cache;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(PackResourceMetadata metadata, String[] namespaces, CallbackInfo ci) {
    cache = new ResourceCache((ResourcePack) this);
  }

  @Override
  public InputStream superTryOpen(ResourceType type, Identifier id) {
    return findInputStream(type, id);
  }

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) {
    return superTryOpen(type, id);
  }

  @Override
  public NativeImageHolder tryLoadImage(ResourceType type, Identifier id) throws IOException {
    return cache.loadNativeImage(type, id);
  }

  @Override
  public JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier id) throws IOException {
    return cache.loadUnbakedJsonModel(type, id);
  }

  @Override
  public JsonUnbakedModel tryGetJsonUnbakedModel(Identifier id) throws IOException {
    return tryLoadUnbakedModel(ResourceType.CLIENT_RESOURCES, id);
  }

  @Override
  public NativeImageHolder tryGetNativeImage(Identifier id) throws IOException {
    return tryLoadImage(ResourceType.CLIENT_RESOURCES, id);
  }

  @Override
  public JsonUnbakedModel getJsonUnbakedModel(Identifier id) throws IOException {
    var model = tryGetJsonUnbakedModel(id);
    if (model == null) throw new FileNotFoundException("Could not get client resource from vanilla pack: " + id.toString());
    return model;
  }

  @Override
  public NativeImageHolder getNativeImage(Identifier id) throws IOException {
    var image = tryGetNativeImage(id);
    if (image == null) throw new FileNotFoundException("Could not get client resource from vanilla pack: " + id.toString());
    return image;
  }
}
