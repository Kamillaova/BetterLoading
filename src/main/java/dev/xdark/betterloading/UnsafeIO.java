package dev.xdark.betterloading;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;

import static dev.xdark.betterloading.RuntimeHelper.LOOKUP;
import static dev.xdark.betterloading.RuntimeHelper.UNSAFE;
import static dev.xdark.betterloading.RuntimeHelper.sneaky;

final class UnsafeIO {
  private static final MethodHandle MH_SET__READER_LOCK;
  private static final MethodHandle MH_SET__BR_IN;
  private static final MethodHandle MH_SET__BR_CB;

  private UnsafeIO() {}

  static BufferedReader createBufferedReader(Reader reader, char[] buffer) {
    try {
      var br = (BufferedReader) UNSAFE.allocateInstance(BufferedReader.class);
      MH_SET__READER_LOCK.invokeExact((Reader) br, (Object) reader);
      MH_SET__BR_IN.invokeExact(br, reader);
      MH_SET__BR_CB.invokeExact(br, buffer);
      return br;
    } catch (Throwable t) {
      throw sneaky(t);
    }
  }

  static {
    try {
      var lookup = LOOKUP;
      MH_SET__READER_LOCK = lookup.findSetter(Reader.class, "lock", Object.class);
      MH_SET__BR_IN = lookup.findSetter(BufferedReader.class, "in", Reader.class);
      MH_SET__BR_CB = lookup.findSetter(BufferedReader.class, "cb", char[].class);
    } catch (Throwable t) {
      throw new ExceptionInInitializerError(t);
    }
  }
}
