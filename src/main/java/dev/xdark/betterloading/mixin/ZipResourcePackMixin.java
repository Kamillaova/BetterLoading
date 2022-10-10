package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.FileResourcePackExt;
import net.minecraft.resource.ZipResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mixin(ZipResourcePack.class)
public abstract class ZipResourcePackMixin implements FileResourcePackExt {
  @Shadow
  protected abstract ZipFile getZipFile() throws IOException;

  @Override
  public InputStream tryOpenFile(String name) throws IOException {
    var zipFile = this.getZipFile();
    var zipEntry = zipFile.getEntry(name);
    if (zipEntry == null) return null;
    return zipFile.getInputStream(zipEntry);
  }
}
