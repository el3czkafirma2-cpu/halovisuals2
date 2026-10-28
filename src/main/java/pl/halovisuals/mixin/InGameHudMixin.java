package pl.halovisuals.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.halovisuals.Module;
import pl.halovisuals.ModuleManager;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void hv$customCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        Module m = ModuleManager.get("Crosshair");
        if (m != null && m.enabled) ci.cancel();
    }
}
