package com.fudansteam.screen;

import com.fudansteam.options.EyeOptions;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.client.gui.screen.SettingsScreen.func_243293_a;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-02-28 10:57:22
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class EyeScreen extends Screen {
    
    private final Screen parent;
    private OptionsRowList list;
    
    public EyeScreen(Screen parent) {
        super(new TranslationTextComponent("eye.title.main"));
        this.parent = parent;
    }
    
    @Override
    public void render(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        List<IReorderingProcessor> list = func_243293_a(this.list, mouseX, mouseY);
        if (list != null) {
            this.renderTooltip(matrices, list, mouseX, mouseY);
        }
    }
    
    @Override
    protected void init() {
        if (this.minecraft != null) {
            this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        }
        this.list.addOptions(new AbstractOption[]{EyeOptions.EYE_DISTANCE, EyeOptions.WARN_DISTANCE, EyeOptions.SUPER_EYE, EyeOptions.BLACK_BELT, EyeOptions.DAN_MU});
        this.children.add(this.list);
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, new TranslationTextComponent("eye.done"), (button) -> {
            if (this.minecraft != null) {
                this.minecraft.displayGuiScreen(this.parent);
            }
        }));
    }
    
}
