package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class HudEditorScreen extends Screen {
    private final Screen parent; private KlyvrenHud.Element selected,dragging; private double ox,oy; private EditBox clan;
    public HudEditorScreen(Screen p){super(Component.literal("Klyvren HUD Editor"));parent=p;}
    @Override protected void init(){
        addRenderableWidget(Button.builder(Component.literal("DONE"),b->onClose()).bounds(width-94,12,78,24).build());
        clan=new EditBox(font,16,height-36,170,20,Component.literal("Clan"));clan.setValue(KlyvrenHud.getClanName());clan.setMaxLength(24);addRenderableWidget(clan);
        addRenderableWidget(Button.builder(Component.literal("SAVE"),b->KlyvrenHud.setClanName(clan.getValue())).bounds(192,height-36,60,20).build());
        addRenderableWidget(Button.builder(Component.literal("SIZE -"),b->{if(selected!=null)KlyvrenHud.scale(selected,-8);}).bounds(258,height-36,65,20).build());
        addRenderableWidget(Button.builder(Component.literal("SIZE +"),b->{if(selected!=null)KlyvrenHud.scale(selected,8);}).bounds(329,height-36,65,20).build());
        addRenderableWidget(Button.builder(Component.literal("TRANSPARENCY -"),b->KlyvrenHud.setOpacity(KlyvrenHud.getOpacity()-10)).bounds(400,height-36,105,20).build());
        addRenderableWidget(Button.builder(Component.literal("TRANSPARENCY +"),b->KlyvrenHud.setOpacity(KlyvrenHud.getOpacity()+10)).bounds(511,height-36,105,20).build());
        addRenderableWidget(Button.builder(Component.literal("RIM COLOR"),b->KlyvrenHud.cycleRimColor()).bounds(622,height-36,92,20).build());
    }
    @Override public void render(GuiGraphics g,int mx,int my,float d){
        g.fill(0,0,width,height,0xAA000000);g.drawString(font,"KLYVREN HUD EDITOR",16,16,0xFFFFFFFF,false);
        g.drawString(font,"Click a module to select it, drag to move, then use SIZE +/-.",16,31,0xFFB9A5FF,false);
        g.drawString(font,"Transparency "+KlyvrenHud.getOpacity()+"%   Rim #"+String.format("%06X",KlyvrenHud.getRimColor()),16,47,0xFF9AA0AA,false);
        KlyvrenHud.renderEditor(g);if(selected!=null)g.drawString(font,"Selected: "+selected.name,16,62,0xFFFFFFFF,false);super.render(g,mx,my,d);
    }
    @Override public boolean mouseClicked(MouseButtonEvent e,boolean doubled){
        if(e.button()==0)for(KlyvrenHud.Element h:KlyvrenHud.elements())if(h.visible&&h.contains(e.x(),e.y())){selected=h;dragging=h;ox=e.x()-h.x;oy=e.y()-h.y;return true;}
        return super.mouseClicked(e,doubled);
    }
    @Override public boolean mouseDragged(MouseButtonEvent e,double dx,double dy){if(e.button()==0&&dragging!=null){dragging.x=Math.max(0,Math.min(width-dragging.width,(int)(e.x()-ox)));dragging.y=Math.max(70,Math.min(height-70-dragging.height,(int)(e.y()-oy)));return true;}return super.mouseDragged(e,dx,dy);}
    @Override public boolean mouseReleased(MouseButtonEvent e){if(e.button()==0&&dragging!=null){dragging=null;KlyvrenHud.save();return true;}return super.mouseReleased(e);}
    @Override public void onClose(){KlyvrenHud.save();Minecraft.getInstance().setScreen(parent);}
}