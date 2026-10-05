package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public final class KlyvrenScreen extends Screen {
    private final Screen parent;
    private int tab = 0;
    private static final String[] TABS = {"HUD", "PvP", "Visual", "Performance", "Recording", "Mods", "Settings"};
    private List<KlyvrenFeatures.Feature> visible = List.of();

    public KlyvrenScreen(Screen parent) { super(Component.literal("Klyvren Client")); this.parent = parent; }

    @Override protected void init() {
        int navX = 18;
        for (int i=0;i<TABS.length;i++) {
            int idx=i;
            addRenderableWidget(Button.builder(Component.literal(TABS[i]), b->{tab=idx;rebuild();}).bounds(navX,50,82,24).build());
            navX+=86;
        }
        if(tab==0) addRenderableWidget(Button.builder(Component.literal("HUD Editor"), b->minecraft.setScreen(new HudEditorScreen(this))).bounds(18,92,110,24).build());
        visible = new ArrayList<>();
        String cat=TABS[tab];
        for(var f:KlyvrenFeatures.ALL.values()) if(f.category().equals(cat)) visible.add(f);
        int x=18,y=130,col=0;
        for(var f:visible){
            int yy=y;
            addRenderableWidget(Button.builder(label(f), b->{KlyvrenFeatures.toggle(f.id()); rebuild();}).bounds(x,yy,220,24).build());
            col++;
            if(col%2==0){x=18;y+=30;}else x=246;
        }
    }
    private Component label(KlyvrenFeatures.Feature f){return Component.literal((KlyvrenFeatures.isEnabled(f.id())?"ON  ":"OFF ")+f.name());}
    private void rebuild(){clearWidgets();init();}
    @Override public void render(GuiGraphics g,int mx,int my,float d){
        renderBackground(g,mx,my,d);
        int l=Math.max(10,width/2-390),r=Math.min(width-10,width/2+390);
        g.fill(l,14,r,height-14,0xF00B0D12);
        g.fill(l,14,l+5,height-14,0xFF8B5CF6);
        g.drawString(font,"KLYVREN CLIENT",l+18,22,0xFFFFFFFF,false);
        g.drawString(font,"35-feature Fabric client",l+18,36,0xFF9A9EAA,false);
        g.drawString(font,"RIGHT SHIFT + 1",r-120,25,0xFFB9A5FF,false);
        g.fill(l+10,80,r-10,82,0xFF242833);
        g.drawString(font,TABS[tab],l+18,94,0xFFFFFFFF,false);
        if(tab==0) g.drawString(font,"Open HUD Editor to drag enabled modules and save their positions.",l+18,height-32,0xFFB9A5FF,false);
        super.render(g,mx,my,d);
    }
    @Override public void onClose(){KlyvrenHud.save();Minecraft.getInstance().setScreen(parent);}
}
