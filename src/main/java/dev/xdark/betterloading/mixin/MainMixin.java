package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.GameHelper;
import io.netty.util.concurrent.FastThreadLocalThread;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin {
    @ModifyConstant(
        method = "main([Ljava/lang/String;)V",
        constant = @Constant(intValue = 1)
    )
    private static int modifyOptimizeDataFixer(int original) {
        return 0;
    }

    @Inject(
        method = "main([Ljava/lang/String;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void assistLaunch(String[] args, CallbackInfo ci) {
        Thread t;
        if (!((t = Thread.currentThread()) instanceof FastThreadLocalThread)) {
            System.out.println("Re-entering into the entrypoint in fast thread local thread");
            var thread = new FastThreadLocalThread(() -> Main.main(args));
            thread.setContextClassLoader(t.getContextClassLoader());
            thread.start();
            ci.cancel();
        }
    }

    @Inject(
        method = "main([Ljava/lang/String;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/SharedConstants;createGameVersion()V",
            shift = At.Shift.AFTER
        )
    )
    private static void bootstrap(String[] args, boolean optimizeDataFixer, CallbackInfo ci) {
        GameHelper.bootstrap();
    }

    @Redirect(
        method = "main([Ljava/lang/String;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Bootstrap;initialize()V"
        )
    )
    private static void onBootstrap() {}

    // Do nothing, checked in GameHelper
    @Redirect(
        method = "main([Ljava/lang/String;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Bootstrap;logMissing()V"
        )
    )
    private static void logMissing() {}
}
