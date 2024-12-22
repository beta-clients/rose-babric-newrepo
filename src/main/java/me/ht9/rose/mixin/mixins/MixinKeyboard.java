package me.ht9.rose.mixin.mixins;

import me.ht9.rose.Rose;
import me.ht9.rose.event.events.InputEvent;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public class MixinKeyboard
{
    @Inject(
            method = "next",
            at = @At("RETURN")
    )
    private static void next$Return(CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValueZ())
        {
            Rose.bus().post(new InputEvent.KeyInputEvent());
        }
    }
}