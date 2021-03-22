package com.fudansteam.screen;

import com.fudansteam.Eye;
import com.fudansteam.thread.AutoLoginThread;
import com.fudansteam.utils.BiliUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-21 17:02:45
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class LoginScreen extends Screen {
    
    private static final int QR_SIZE = 120;
    private final Screen parent;
    private static String informKey = "eye.inform.qr.tip";
    public static DynamicTexture texture;
    private static final TranslationTextComponent RESOURCE_PAGE = new TranslationTextComponent("eye.inform.qr.resource_code");
    private static final ITextComponent LOGIN_TOOLTIP = new TranslationTextComponent("eye.widget.tooltip.login");
    private static Style style;
    public static boolean confirmed = false;
    private static AutoLoginThread thread = null;
    
    public LoginScreen(Screen parent) {
        super(new TranslationTextComponent("eye.title.login"));
        this.parent = parent;
        informKey = "eye.inform.qr.tip";
        texture = null;
        confirmed = false;
        BiliUtil.beforeTryLogin();
        onClose();
        thread = new AutoLoginThread();
        thread.start();
    }
    
    @Override
    public void onClose() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
    
    @Override
    public void tick() {
        if (confirmed) {
            login();
            confirmed = false;
        }
    }
    
    @Override
    protected void init() {
        Minecraft minecraft = Minecraft.getInstance();
        this.addButton(new Button(this.width / 2 - 155, 28 + QR_SIZE + 40, 150, 20, new TranslationTextComponent("eye.widget.refresh"), (button) -> {
            informKey = "eye.inform.qr.refreshed";
            BiliUtil.beforeTryLogin();
        }));
        this.addButton(new Button(this.width / 2 + 5, 28 + QR_SIZE + 40, 150, 20, new TranslationTextComponent("eye.widget.back"), (button) -> minecraft.displayGuiScreen(this.parent)));
        this.addButton(new Button(this.width / 2 - 100, 28 + QR_SIZE + 65, 200, 20, new TranslationTextComponent("eye.widget.login"), (button) -> login(), (button, matrixStack, mouseX, mouseY) -> func_243308_b(matrixStack, Collections.singletonList(LOGIN_TOOLTIP), mouseX, mouseY)));
    }
    
    private void login() {
        try {
            if (Eye.loginCookies.size() != 1) {
                Eye.username = BiliUtil.getUsername(Eye.loginCookies.get("FinalCookie"));
                Minecraft.getInstance().displayGuiScreen(this.parent);
            } else {
                informKey = Eye.loginCookies.get("inform");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int textWidth = this.font.getStringPropertyWidth(RESOURCE_PAGE);
        if (mouseX >= (float) (this.width - textWidth) / 2
                && mouseY >= 28 + QR_SIZE + 25
                && mouseX <= (float) (this.width + textWidth) / 2
                && mouseY <= 28 + QR_SIZE + 25 + this.font.FONT_HEIGHT) {
            this.handleComponentClicked(style);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 16777215);
        if (texture != null) {
            Minecraft instance = Minecraft.getInstance();
            TextureManager textureManager = instance.getTextureManager();
            ResourceLocation qrCache = new ResourceLocation(Eye.ID, "qr_cache");
            textureManager.loadTexture(qrCache, texture);
            textureManager.bindTexture(qrCache);
            if (texture.getTextureData() != null) {
                AbstractGui.blit(matrixStack,
                        (instance.getMainWindow().getScaledWidth() - QR_SIZE) / 2, 28,
                        0, 0, QR_SIZE, QR_SIZE, QR_SIZE, QR_SIZE);
            }
        }
        drawCenteredString(matrixStack, this.font, new TranslationTextComponent(informKey), this.width / 2, 28 + QR_SIZE + 10, 16777215);
        Style empty = Style.EMPTY;
        style = empty.setUnderlined(true).setBold(true).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Kagantuya/forge-mod-eye"));
        RESOURCE_PAGE.setStyle(style);
        drawCenteredString(matrixStack, this.font, RESOURCE_PAGE, this.width / 2, 28 + QR_SIZE + 25, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
}
