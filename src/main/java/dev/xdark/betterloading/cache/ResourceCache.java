package dev.xdark.betterloading.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.xdark.betterloading.IOUtil;
import dev.xdark.betterloading.internal.ResourcePackExt;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class ResourceCache {
  private static final NativeImageHolder MISSING_IMAGE = new NativeImageHolder(null);
  private static final JsonUnbakedModel MISSING_UNBAKED_MODEL = new JsonUnbakedModel(
    null,
    null,
    null,
    false,
    null,
    null,
    null
  );

  private final Cache<ResourceKey, NativeImageHolder> imageCache = newCache()
    .<ResourceKey, NativeImageHolder>removalListener((key, value, cause) -> {
      if (value != MISSING_IMAGE) {
        ((CachedNativeImage) value.getImage()).doClose();
      }
    }).build();
  private final Cache<ResourceKey, JsonUnbakedModel> jsonUnbakedModelCache = newCache().build();
  private final ResourcePack resourcePack;

  public ResourceCache(ResourcePack resourcePack) {
    this.resourcePack = resourcePack;
  }

  public NativeImageHolder loadNativeImage(ResourceType type, Identifier identifier) throws IOException {
    return loadFromCache(imageCache, new ResourceKey(type, identifier), (rp, key) -> {
      try (var in = ((ResourcePackExt) rp).tryOpen(key.resourceType(), key.identifier())) {
        return in == null ? null : new NativeImageHolder(CachedNativeImage.read(in));
      }
    }, MISSING_IMAGE);
  }

  public JsonUnbakedModel loadUnbakedJsonModel(ResourceType type, Identifier identifier) throws IOException {
    return loadFromCache(jsonUnbakedModelCache, new ResourceKey(type, identifier), (rp, key) -> {
      var in = ((ResourcePackExt) rp).tryOpen(key.resourceType(), key.identifier());
      if (in == null) {
        return null;
      }
      try (var reader = IOUtil.toJsonReader(in, StandardCharsets.UTF_8)) {
        return JsonUnbakedModelDeserializer.INSTANCE.read(reader);
      }
    }, MISSING_UNBAKED_MODEL);
  }

  private <K, V> V loadFromCache(Cache<K, V> cache, K key, ValueLoader<K, V> loader, V missing) throws IOException {
    var value = cache.getIfPresent(key);
    if (value != null) {
      if (value == missing) {
        return null;
      }
      return value;
    }
    if ((value = loader.load(resourcePack, key)) == null) {
      cache.put(key, missing);
    } else {
      cache.put(key, value);
    }
    return value;
  }

  @SuppressWarnings("unchecked")
  private static <K, V> Caffeine<K, V> newCache() {
    return (Caffeine<K, V>) Caffeine.newBuilder()
      .expireAfterAccess(1L, TimeUnit.HOURS)
      .softValues();
  }

  private interface ValueLoader<K, V> {
    V load(ResourcePack rp, K key) throws IOException;
  }
}
