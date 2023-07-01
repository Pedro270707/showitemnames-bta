package net.pedroricardo.showitemnames.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.helper.Color;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = GuiIngame.class, remap = false)
public class ShowItemNameMixin {

    private int ticksUntilItemNameDisappears = 300;
    private ItemStack currentItem;
    private int textAlpha = 255;

    @Mixin(value = GuiIngame.class, remap = false)
    private interface GuiIngameAccessors {
        @Accessor("mc")
        Minecraft mc();

        @Accessor("fontrenderer")
        FontRenderer fontrenderer();
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderGameOverlay(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci, StringTranslate stringTranslate, int width, int height, int screenPadding) {
        GuiIngame thisObj = ((GuiIngame) (Object) this);
        Minecraft mc = ((GuiIngameAccessors)thisObj).mc();
        FontRenderer fontRenderer = ((GuiIngameAccessors)thisObj).fontrenderer();

        if (this.currentItem == null || mc.thePlayer.inventory.getCurrentItem() == null
        || (mc.thePlayer.inventory.getCurrentItem().getItem() != this.currentItem.getItem() && !mc.thePlayer.inventory.getCurrentItem().getItemName().equals(this.currentItem.getItemName()))
        || (mc.thePlayer.inventory.getCurrentItem().getMetadata() != this.currentItem.getMetadata())
        || !mc.thePlayer.inventory.getCurrentItem().tag.getString("name").equals(this.currentItem.tag.getString("name"))) {
            this.ticksUntilItemNameDisappears = 300;
            this.currentItem = mc.thePlayer.inventory.getCurrentItem();
            this.textAlpha = 255;
        }

        if (this.ticksUntilItemNameDisappears > 0) {
            if (this.ticksUntilItemNameDisappears < 52) {
                this.textAlpha -= 5;
            }
            this.ticksUntilItemNameDisappears -= 1;
            if (!mc.isometricMode && mc.gameSettings.immersiveMode.drawHotbar() && mc.playerController.shouldDrawHUD()) {
                if (mc.thePlayer.inventory.getCurrentItem() != null) {
                    String name;
                    if (mc.thePlayer.inventory.getCurrentItem().tag.getString("name") == null
                            || !mc.thePlayer.inventory.getCurrentItem().tag.getBoolean("overrideName")) {
                        name = StringTranslate.getInstance().translateKey(mc.thePlayer.inventory.getCurrentItem().getItemName() + ".name");
                    } else {
                        name = mc.thePlayer.inventory.getCurrentItem().tag.getString("name");
                    }

                    if (MathHelper.floor_float(this.textAlpha) > 0) {
                        int color = Color.intToIntARGB(MathHelper.floor_float(this.textAlpha), 255, 255, 255);
                        int offset = 50;
                        if (mc.thePlayer.getGamemode().isPlayerInvulnerable) {
                            offset = 41;
                        }
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        fontRenderer.drawCenteredString(name,
                                        mc.resolution.scaledWidth / 2,
                                        mc.resolution.scaledHeight - offset - screenPadding,
                                        color);
                    }
                }
            }
        }
    }
}
