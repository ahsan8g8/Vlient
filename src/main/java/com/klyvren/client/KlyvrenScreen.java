package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public final class KlyvrenScreen extends Screen {
 private final Screen parent;private int tab=0;private boolean featherStyle=false;private long openedAt;private List<KlyvrenFeatures.Feature> visible=List.of();
 private static final String[] TABS={"HUD","PvP","Visual","Performance","Recording","Mods","Settings"};
 public KlyvrenScreen(Screen p){super(Component.literal("Klyvren Client"));parent=p;}
 @Override protected void init(){openedAt=System.currentTimeMillis();rebuild();}
 private void rebuild(){visible=new ArrayList<>();for(var f:KlyvrenFeatures.ALL.values())if(f.category().equals(TABS[tab]))visible.add(f);}
 @Override public void render(GuiGraphics g,int mx,int my,float d){
  long age=System.currentTimeMillis()-openedAt;float t=Math.min(1f,age/220f),ease=1f-(1f-t)*(1f-t);
  g.fill(0,0,width,height,0xEA000000);int pw=Math.min(940,width-24),ph=Math.min(620,height-24),px=(width-pw)/2,py=(height-ph)/2;
  int y=height+20-(int)((ph+20)*ease);g.fill(px,y,px+pw,y+ph,0xFF090A0D);g.fill(px,y,px+3,y+ph,featherStyle?0xFF777777:0xFF8B5CF6);
  int side=170;g.fill(px+3,y,px+side,y+ph,0xFF0D0F13);g.drawString(font,featherStyle?"FEATHER-STYLE":"KLYVREN",px+22,y+22,0xFFFFFFFF,false);g.drawString(font,"CLIENT",px+22,y+38,0xFF8F96A3,false);
  for(int i=0;i<TABS.length;i++){int ty=y+72+i*34;boolean s=i==tab;g.fill(px+12,ty-4,px+side-12,ty+22,s?0xFF211735:0);g.drawString(font,TABS[i],px+26,ty+3,s?0xFFFFFFFF:0xFF8D939E,false);}
  int cx=px+side+28;g.drawString(font,TABS[tab],cx,y+25,0xFFFFFFFF,false);g.drawString(font,featherStyle?"Feather-style layout":"Klyvren layout",cx,y+42,0xFF737A86,false);
  if(tab==0)drawButton(g,cx,y+66,116,28,"HUD EDITOR",mx,my);
  if(tab==5)drawButton(g,cx+126,y+66,116,28,"MODS LIST",mx,my);
  int sy=y+108,cw=Math.min(285,(pw-side-68)/2);
  for(int i=0;i<visible.size();i++){var f=visible.get(i);int col=i%2,row=i/2,bx=cx+col*(cw+14),by=sy+row*42;if(by+34>y+ph-18)continue;boolean on=KlyvrenFeatures.isEnabled(f.id()),h=mx>=bx&&mx<=bx+cw&&my>=by&&my<=by+32;g.fill(bx,by,bx+cw,by+32,h?0xFF171A20:0xFF111318);g.drawString(font,f.name(),bx+12,by+6,0xFFE6E8EC,false);int sx=bx+cw-54;g.fill(sx,by+7,sx+40,by+25,on?0xFF5F3FA8:0xFF30343B);g.fill(on?sx+24:sx+4,by+10,on?sx+36:sx+16,by+22,0xFFFFFFFF);}
  g.drawString(font,"STYLE",cx,y+ph-58,0xFF777D88,false);drawButton(g,cx+52,y+ph-66,112,26,featherStyle?"FEATHER-STYLE":"KLYVREN",mx,my);
 }
 private void drawButton(GuiGraphics g,int x,int y,int w,int h,String s,int mx,int my){boolean h=mx>=x&&mx<=x+w&&my>=y&&my<=y+h;g.fill(x,y,x+w,y+h,h?0xFF2A2040:0xFF1A1722);g.drawString(font,s,x+10,y+8,0xFFD8C7FF,false);}
 @Override public boolean mouseClicked(MouseButtonEvent e,boolean dbl){
  if(e.button()!=GLFW.GLFW_MOUSE_BUTTON_LEFT)return super.mouseClicked(e,dbl);int pw=Math.min(940,width-24),ph=Math.min(620,height-24),px=(width-pw)/2,py=(height-ph)/2;if(System.currentTimeMillis()-openedAt<230)return true;
  int side=170;for(int i=0;i<TABS.length;i++){int ty=py+72+i*34;if(e.x()>=px+12&&e.x()<=px+side-12&&e.y()>=ty-4&&e.y()<=ty+22){tab=i;rebuild();return true;}}
  int cx=px+side+28;if(tab==0&&e.x()>=cx&&e.x()<=cx+116&&e.y()>=py+66&&e.y()<=py+94){minecraft.setScreen(new HudEditorScreen(this));return true;}
  if(e.x()>=cx+52&&e.x()<=cx+164&&e.y()>=py+ph-66&&e.y()<=py+ph-40){featherStyle=!featherStyle;return true;}
  int sy=py+108,cw=Math.min(285,(pw-side-68)/2);for(int i=0;i<visible.size();i++){var f=visible.get(i);int col=i%2,row=i/2,bx=cx+col*(cw+14),by=sy+row*42;if(e.x()>=bx&&e.x()<=bx+cw&&e.y()>=by&&e.y()<=by+32){KlyvrenFeatures.toggle(f.id());return true;}}return true;
 }
 @Override public boolean keyPressed(KeyEvent e){if(e.key()==GLFW.GLFW_KEY_ESCAPE){onClose();return true;}return super.keyPressed(e);}
 @Override public void onClose(){KlyvrenHud.save();Minecraft.getInstance().setScreen(parent);}
}