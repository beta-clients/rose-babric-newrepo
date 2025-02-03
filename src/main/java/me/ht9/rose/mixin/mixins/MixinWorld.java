package me.ht9.rose.mixin.mixins;

import me.ht9.rose.Rose;
import me.ht9.rose.event.events.EntityEvent;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld
{
    @Inject(method = "entityJoinedWorld", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
    public void entityJoinedWorld$add(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        EntityEvent.EntityJoinEvent event = new EntityEvent.EntityJoinEvent(entity);
        Rose.bus().post(event);
    }

    @Inject(method = "setEntityDead", at = @At("HEAD"))
    public void setEntityDead$Head(Entity entity, CallbackInfo ci)
    {
        EntityEvent.EntityLeaveEvent event = new EntityEvent.EntityLeaveEvent(entity);
        Rose.bus().post(event);
    }
}
