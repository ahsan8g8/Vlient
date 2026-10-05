package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private KlyvrenHud.Element selected,dragging,resizing;
    private double ox,oy;
    private int startW,startH;
    private EditBox clan;

    public HudEditorScreen(Screen p){super(Component.literal("Klyvren HUD Studio"));parent=p;}

    @Override protected void init(){
        addRenderableWidget(Button.builder(Component.literal("DONE"),b->onClose()).bounds(width-94,12,78,24).build());
        clan=new EditBox(font,16,height-36,170,20,Component.literal("Clan"));
        clan.setValue(KlyvrenHud.getClanName());clan.setMaxLength(24);addRenderableWidget(clan);
        addRenderableWidget(Button.builder(Component.literal("SAVE CLAN"),b->KlyvrenHud.setClanName(clan.getValue())).bounds(192,height-36,86,20).build());
        addRenderableWidget(Button.builder(Component.literal("COLOR"),b->{if(selected!=null)KlyvrenHud.cycleElementColor(selected);}).bounds(284,height-36,62,20).build());
        addRenderableWidget(Button.builder(Component.literal("ALPHA -"),b->KlyvrenHud.setOpacity(KlyvrenHud.getOpacity()-10)).bounds(352,height-36,58,20).build());
        addRenderableWidget(Button.builder(Component.literal("ALPHA +"),b->KlyvrenHud.setOpacity(KlyvrenHud.getOpacity()+10)).bounds(416,height-36,58,20).build());
        addRenderableWidget(Button.builder(Component.literal("HIDE/SHOW"),b->{if(selected!=null){selected.visible=!selected.visible;KlyvrenHud.save();}}).bounds(480,height-36,84,20).build());
        addRenderableWidget(Button.builder(Component.literal("RIM"),b->KlyvrenHud.cycleRimColor()).bounds(570,height-36,50,20).build());
    }

    @Override public void render(GuiGraphics g,int mx,int my,float d){
        g.fill(0,0,width,height,0xE9000000);
        g.fill(12,12,width-12,height-58,0xFF0A0C10);
        g.drawString(font,"KLYVREN HUD STUDIO",24,22,0xFFFFFFFF,false);
        g.drawString(font,"Drag modules • drag the bottom-right corner to resize • use the controls below",24,39,0xFF9B9FA8,false);
        KlyvrenHud.renderEditor(g);
        if(selected!=null){
            int x=selected.x,y=selected.y,w=selected.width,h=selected.height;
            g.fill(x-2,y-2,x+w+2,y+h+2,0xFFEEE6FF);
            g.fill(x+w-8,y+h-8,x+w+3,y+h+3,0xFF8B5CF6);
            g.drawString(font,selected.name+"  "+w+"x"+h,24,56,0xFFD8C7FF,false);
        }
        drawModuleList(g,mx,my);
        super.render(g,mx,my,d);
    }

    private void drawModuleList(GuiGraphics g,int mx,int my){
        int x=Math.max(20,width-190),y=72;
        g.fill(x-10,y-12,width-20,height-72,0xDD11141A);
        g.drawString(font,"MODULES",x,y,0xFFFFFFFF,false);
        int row=y+22;
        for(KlyvrenHud.Element e:KlyvrenHud.elements()){
            boolean hit=mx>=x&&mx<=width-30&&my>=row&&my<=row+20;
            g.fill(x,row,width-30,row+20,hit?0xFF252A33:0xFF191D24);
            g.drawString(font,e.name,x+7,row+6,e.visible?0xFFE7E9ED:0xFF777C85,false);
            g.drawString(font,e.visible?"ON":"OFF",width-70,row+6,e.visible?0xFFBFA7FF:0xFF777C85,false);
            row+=23;
            if(row>height-100)break;
        }
    }

    @Override public boolean mouseClicked(MouseButtonEvent e,boolean doubled){
        if(e.button()!=0)return super.mouseClicked(e,doubled);
        for(KlyvrenHud.Element h:KlyvrenHud.elements()){
            if(h.visible&&h.resizeHandle(e.x(),e.y())){selected=h;resizing=h;startW=h.width;startH=h.height;ox=e.x();oy=e.y();return true;}
        }
        int listX=Math.max(20,width-190),row=94;
        for(KlyvrenHud.Element h:KlyvrenHud.elements()){
            if(e.x()>=listX&&e.x()<=width-30&&e.y()>=row&&e.y()<=row+20){h.visible=!h.visible;selected=h;KlyvrenHud.save();return true;}
            row+=23;if(row>height-100)break;
        }
        for(KlyvrenHud.Element h:KlyvrenHud.elements())if(h.visible&&h.contains(e.x(),e.y())){
            selected=h;dragging=h;ox=e.x()-h.x;oy=e.y()-h.y;return true;
        }
        return super.mouseClicked(e,doubled);
    }

    @Override public boolean mouseDragged(MouseButtonEvent e,double dx,double dy){
        if(e.button()!=0)return super.mouseDragged(e,dx,dy);
        if(resizing!=null){
            int nw=(int)(startW+(e.x()-ox)),nh=(int)(startH+(e.y()-oy));
            KlyvrenHud.resize(resizing,nw,nh);return true;
        }
        if(dragging!=null){
            dragging.x=Math.max(12,Math.min(width-220-dragging.width,(int)(e.x()-ox)));
            dragging.y=Math.max(70,Math.min(height-90-dragging.height,(int)(e.y()-oy)));
            return true;
        }
        return super.mouseDragged(e,dx,dy);
    }

    @Override public boolean mouseReleased(MouseButtonEvent e){
        if(e.button()==0&&(dragging!=null||resizing!=null)){dragging=null;resizing=null;KlyvrenHud.save();return true;}
        return super.mouseReleased(e);
    }

    @Override public void onClose(){KlyvrenHud.save();Minecraft.getInstance().setScreen(parent);}
}