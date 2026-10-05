package com.klyvren.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public final class KlyvrenAccountScreen extends Screen {
    private final Screen parent;
    private final List<String> profiles=new ArrayList<>(List.of("Default Profile"));
    private EditBox name;
    public KlyvrenAccountScreen(Screen p){super(Component.literal("Klyvren Accounts"));parent=p;}
    @Override protected void init(){
        int x=width/2-140;
        name=new EditBox(font,x,height/2+45,280,20,Component.literal("Profile name"));name.setMaxLength(24);addRenderableWidget(name);
        addRenderableWidget(Button.builder(Component.literal("ADD PROFILE"),b->{if(!name.getValue().isBlank())profiles.add(name.getValue().trim());}).bounds(x,height/2+72,134,22).build());
        addRenderableWidget(Button.builder(Component.literal("BACK"),b->onClose()).bounds(x+146,height/2+72,134,22).build());
    }
    @Override public void render(GuiGraphics g,int mx,int my,float d){
        g.fill(0,0,width,height,0xFF060606);g.drawCenteredString(font,"KLYVREN ACCOUNTS",width/2,height/2-90,0xFFFFFFFF);
        g.drawCenteredString(font,"Profiles only — Minecraft sign-in stays with the official launcher/account system.",width/2,height/2-70,0xFF888888);
        int y=height/2-35;for(String p:profiles){g.drawCenteredString(font,"• "+p,width/2,y,0xFFCCCCCC);y+=20;}super.render(g,mx,my,d);
    }
    @Override public void onClose(){minecraft.setScreen(parent);}
}