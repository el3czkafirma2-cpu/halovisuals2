package pl.halovisuals.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.halovisuals.Module;
import pl.halovisuals.ModuleManager;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void hv$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Module m = ModuleManager.get("No Hurt Cam");
        if (m != null && m.enabled) ci.cancel();
    }
}
