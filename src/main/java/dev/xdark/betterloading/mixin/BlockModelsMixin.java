package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.BlockStateExt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(BlockModels.class)
public abstract class BlockModelsMixin {
  @Shadow @Final private BakedModelManager modelManager;
  @Shadow @Final private Map<BlockState, BakedModel> models;

  @Shadow
  public static native ModelIdentifier getModelId(BlockState state);

  @Redirect(
    method = "getModel",
    at = @At(
      value = "INVOKE",
      target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
    )
  )
  private Object getModel(Map<?, ?> instance, Object o) {
    return ((BlockStateExt) o).getModel();
  }

  @Inject(
    method = "reload",
    at = @At(
      value = "INVOKE",
      target = "Ljava/util/Map;clear()V",
      shift = Shift.AFTER
    ),
    cancellable = true
  )
  private void onReload(CallbackInfo ci) {
    ci.cancel();
    var modelManager = this.modelManager;
    var models = this.models;
    for (var block : Registry.BLOCK) {
      var states = block.getStateManager().getStates();
      for (int i = 0, j = states.size(); i < j; i++) {
        var state = states.get(i);
        var model = modelManager.getModel(getModelId(state));
        models.put(state, model);
        ((BlockStateExt) state).setModel(model);
      }
    }
  }

  /**
   * @author xDark
   * @reason use optimized version of propertyMapToString
   */
  @Overwrite
  public static ModelIdentifier getModelId(Identifier id, BlockState state) {
    return new ModelIdentifier(id, ((BlockStateExt) state).propertyMapToString());
  }
}
