package com.fudansteam.screen;

import com.fudansteam.config.EyeConfig;
import com.fudansteam.config.EyeDistributor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.client.gui.screen.SettingsScreen.func_243293_a;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-19 10:48:40
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class DanMuScreen extends Screen {
    
    private final Screen parent;
    private OptionsRowList list;
    private TextFieldWidget roomId;
    private String tmpRoomId;
    
    private static final ITextComponent DAN_MU_OPEN = new TranslationTextComponent("eye.options.dan_mu.open");
    private static final ITextComponent DAN_MU_OFF = new TranslationTextComponent("eye.options.dan_mu.off");
    
    private final IteratableOption danMuOption = new IteratableOption("eye.options.dan_mu_option",
            (gameOptions, integer) -> {
                EyeConfig.danMu = !EyeConfig.danMu;
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.ingameGUI.sendChatMessage(ChatType.SYSTEM, EyeConfig.danMu ?
                            new TranslationTextComponent("eye.inform.dan_mu.open") :
                            new TranslationTextComponent("eye.inform.dan_mu.off"), this.minecraft.player.getUniqueID());
                }
                EyeDistributor.save();
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(this.font.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.dan_mu_option"), 200));
                return EyeConfig.danMu ? DAN_MU_OPEN : DAN_MU_OFF;
            });
    
    public DanMuScreen(Screen parent) {
        super(new TranslationTextComponent("eye.title.dan_mu"));
        this.parent = parent;
    }
    
    @Override
    public void render(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        this.roomId.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 8, 16777215);
        drawString(matrices, this.font, new TranslationTextComponent("eye.widget.room_id"), this.width / 2 - 100, 30, -6250336);
        super.render(matrices, mouseX, mouseY, delta);
        List<IReorderingProcessor> list = func_243293_a(this.list, mouseX, mouseY);
        if (list != null) {
            this.renderTooltip(matrices, list, mouseX, mouseY);
        }
    }
    
    @Override
    protected void init() {
        if (this.minecraft != null) {
            this.list = new OptionsRowList(this.minecraft, this.width, this.height, 72, this.height - 32, 25);
        }
        this.roomId = new TextFieldWidget(this.font, this.width / 2 - 100, 42, 200, 20, this.roomId, new TranslationTextComponent("eye.widget.room_id"));
        this.roomId.setResponder((value) -> this.tmpRoomId = value);
        this.roomId.setText(EyeConfig.roomId);
        this.children.add(this.roomId);
        this.list.addOptions(new AbstractOption[]{danMuOption});
        this.children.add(this.list);
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, new TranslationTextComponent("eye.done"), (button) -> {
            if (this.minecraft != null) {
                this.minecraft.displayGuiScreen(this.parent);
            }
            if (!this.tmpRoomId.equals(EyeConfig.roomId)) {
                EyeConfig.roomId = this.tmpRoomId;
                EyeDistributor.save();
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.ingameGUI.sendChatMessage(ChatType.SYSTEM, new TranslationTextComponent("eye.inform.change_live_room", tmpRoomId), this.minecraft.player.getUniqueID());
                }
            }
        }));
        this.setFocusedDefault(this.roomId);
    }
    
}
