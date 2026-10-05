package com.klyvren.client;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

public final class KlyvrenTitleScreen extends Screen {
    private long openedAt;
    public KlyvrenTitleScreen(){super(Component.literal("Klyvren Client"));}
    @Override protected void init(){openedAt=System.currentTimeMillis();int w=220,x=(width-w)/2,y=height/2-20;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("SINGLEPLAYER"),b->minecraft.setScreen(new SelectWorldScreen(this))).bounds(x,y,w,24).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("MULTIPLAYER"),b->minecraft.setScreen(new JoinMultiplayerScreen(this))).bounds(x,y+32,w,24).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("ACCOUNTS"),b->minecraft.setScreen(new KlyvrenAccountScreen(this))).bounds(x,y+64,w,24).build());
    }
    @Override public void render(GuiGraphics g,int mx,int my,float d){
        g.fill(0,0,width,height,0xFF050505);
        float t=Math.min(1f,(System.currentTimeMillis()-openedAt)/650f),e=1f-(1f-t)*(1f-t);
        drawTilted(g,"KLYVREN",width/2,height/2-86+(int)(80*(1-e)),180f,0xFFFFFFFF);
        drawTilted(g,"STUDIOS",width/2,height/2-70+(int)(70*(1-e)),176f,0xFF777777);
        drawTilted(g,"CLIENT",width/2,height/2-50+(int)(60*(1-e)),184f,0xFF444444);
        super.render(g,mx,my,d);
    }
    private void drawTilted(GuiGraphics g,String text,float x,float y,float angle,int color){
        g.pose().pushMatrix();g.pose().translate(x,y);g.pose().rotate((float)Math.toRadians(angle));g.drawCenteredString(font,text,0,0,color);g.pose().popMatrix();
    }
}