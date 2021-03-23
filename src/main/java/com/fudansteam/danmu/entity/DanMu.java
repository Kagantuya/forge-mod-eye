package com.fudansteam.danmu.entity;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-23 10:56:00
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class DanMu {
    
    private String text;
    private int x;
    private int layer;
    
    public int getLayer() {
        return layer;
    }
    
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
}
