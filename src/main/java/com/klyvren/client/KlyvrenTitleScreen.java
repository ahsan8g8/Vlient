package com.klyvren.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

public final class KlyvrenTitleScreen extends Screen {
    public KlyvrenTitleScreen(){super(Component.literal("Klyvren Client"));}
    @Override protected void init(){
        int w=220,x=(width-w)/2,y=height/2-20;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("SINGLEPLAYER"),b->minecraft.setScreen(new SelectWorldScreen(this))).bounds(x,y,w,24).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("MULTIPLAYER"),b->minecraft.setScreen(new JoinMultiplayerScreen(this))).bounds(x,y+32,w,24).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("ACCOUNTS"),b->minecraft.setScreen(new KlyvrenAccountScreen(this))).bounds(x,y+64,w,24).build());
    }
    @Override public void render(GuiGraphics g,int mx,int my,float d){
        g.fill(0,0,width,height,0xFF050505);
        g.drawCenteredString(font,"KLYVREN",width/2,height/2-86,0xFFFFFFFF);
        g.drawCenteredString(font,"STUDIOS",width/2,height/2-70,0xFF777777);
        g.drawCenteredString(font,"CLIENT",width/2,height/2-50,0xFF444444);
        super.render(g,mx,my,d);
    }
}