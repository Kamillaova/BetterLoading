package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.GameHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Inject(
    method = "<init>",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/datafixer/Schemas;getFixer()Lcom/mojang/datafixers/DataFixer;",
      shift = Shift.BEFORE
    )
  )
  private void bootstrapFinish(RunArgs args, CallbackInfo ci) {
    try {
      GameHelper.finishBootstrap();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
