package dev.xdark.betterloading;

import io.netty.util.concurrent.FastThreadLocal;

public final class ThreadLocals {
  private static final int BUFFER_SIZE = 8192;

  private static final FastThreadLocal<char[]> CB_LOCAL = new FastThreadLocal<>() {
    @Override
    protected char[] initialValue() {
      return new char[BUFFER_SIZE];
    }
  };

  private ThreadLocals() {}

  public static char[] charBuffer() {
    return CB_LOCAL.get();
  }
}
