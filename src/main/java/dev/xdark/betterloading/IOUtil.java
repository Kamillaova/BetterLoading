package dev.xdark.betterloading;

import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public final class IOUtil {
  private IOUtil() {}

  public static BufferedReader toBufferedReader(Reader reader) {
    if (reader instanceof BufferedReader br) return br;
    return UnsafeIO.createBufferedReader(reader, ThreadLocals.charBuffer());
  }

  public static BufferedReader toBufferedReader(InputStream in, Charset charset) {
    return toBufferedReader(new InputStreamReader(in, charset));
  }

  public static BufferedReader toBufferedReader(File file, Charset charset) throws FileNotFoundException {
    return toBufferedReader(new FileInputStream(file), charset);
  }

  public static JsonReader toJsonReader(Reader reader) {
    return new JsonReader(toBufferedReader(reader));
  }

  public static JsonReader toJsonReader(InputStream in, Charset charset) {
    return toJsonReader(toBufferedReader(in, charset));
  }
}
