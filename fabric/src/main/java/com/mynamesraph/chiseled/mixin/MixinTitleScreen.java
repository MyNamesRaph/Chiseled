package com.mynamesraph.chiseled.mixin;


import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    
    /*@Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        Constants.getLOG().info("This line is printed by an example mod mixin from Fabric!");
        Constants.getLOG().info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }*/
}