package dev.xdark.betterloading.font;

import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Map;

public final class UnicodeFontLoaderTemplates {
  private static final Map<String, UnicodeFontLoaderTemplate> TEMPLATE_MAP;

  private UnicodeFontLoaderTemplates() {}

  public static Identifier create(String template, int codePoint) {
    var $template = TEMPLATE_MAP.get(template);
    return $template != null
           ? $template.create(codePoint)
           : new Identifier(String.format(template, Integer.toHexString(codePoint)));
  }

  static {
    TEMPLATE_MAP = Collections.singletonMap(
      "minecraft:font/unicode_page_%s.png",
      codePoint -> new Identifier(
        "minecraft",
        "font/unicode_page_" + Integer.toHexString(codePoint) + ".png"
      )
    );
  }
}
