package dev.xdark.betterloading.cache;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public record ResourceKey(
  ResourceType resourceType,
  Identifier identifier
) {}
