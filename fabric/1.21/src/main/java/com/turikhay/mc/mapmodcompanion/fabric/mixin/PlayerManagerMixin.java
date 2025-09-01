package com.turikhay.mc.mapmodcompanion.fabric.mixin;

import com.turikhay.mc.mapmodcompanion.fabric.MapModCompanion;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "sendWorldInfo", at = @At("HEAD"))
    private void mapmodcompanion$sendWorldId(ServerPlayerEntity player, ServerWorld world, CallbackInfo ci) {
        MapModCompanion.run("$sendWorldId", mmc -> mmc.sendLevelData(player, world));
    }
}
