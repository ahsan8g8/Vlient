package com.klyvren.client;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class KlyvrenHud {
    public static final Identifier ID=Identifier.fromNamespaceAndPath(KlyvrenClient.MOD_ID,"hud");
    private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG=Minecraft.getInstance().gameDirectory.toPath().resolve("config/klyvren-hud.json");
    private static final DateTimeFormatter CLOCK=DateTimeFormatter.ofPattern("HH:mm:ss");
    private static String clanName="KLYVREN";
    private static int opacity=82, rimColor=0x8B5CF6;
    private static final Deque<Long> left=new ArrayDeque<>(),right=new ArrayDeque<>();
    private static boolean oldLeft,oldRight;

    public static final Element FPS=e("FPS",10,10,90,22,"fps"),CPS=e("CPS",10,36,100,22,"cps"),
      PING=e("PING",10,62,110,22,"ping"),XYZ=e("XYZ",10,88,180,22,"xyz"),
      KEYSTROKES=e("KEYSTROKES",10,114,140,42,"keystrokes"),CLAN=e("CLAN",10,166,150,22,"clan"),
      CLOCK_E=e("CLOCK",10,192,110,22,"clock"),SERVER=e("SERVER",10,218,180,22,"server"),
      ARMOR=e("ARMOR",10,244,150,22,"armor"),TARGET=e("TARGET",10,270,170,22,"target");
    private static final Element[] ELEMENTS={FPS,CPS,PING,XYZ,KEYSTROKES,CLAN,CLOCK_E,SERVER,ARMOR,TARGET};
    private KlyvrenHud(){}

    private static Element e(String n,int x,int y,int w,int h,String f){return new Element(n,x,y,w,h,f);}
    public static void tick(Minecraft mc){
        if(mc.player==null)return; long n=System.currentTimeMillis();trim(left,n);trim(right,n);
        long win=mc.getWindow().handle();
        boolean l=GLFW.glfwGetMouseButton(win,0)==GLFW.GLFW_PRESS,r=GLFW.glfwGetMouseButton(win,1)==GLFW.GLFW_PRESS;
        if(l&&!oldLeft)left.addLast(n);if(r&&!oldRight)right.addLast(n);oldLeft=l;oldRight=r;
        if(KlyvrenFeatures.isEnabled("fullbright"))try{mc.options.gamma().set(16.0);}catch(Exception ignored){}
    }
    private static void trim(Deque<Long>d,long n){while(!d.isEmpty()&&n-d.peekFirst()>1000)d.removeFirst();}
    public static void render(GuiGraphics g,net.minecraft.client.DeltaTracker d){Minecraft mc=Minecraft.getInstance();if(mc.player==null||mc.options.hideGui)return;for(Element e:ELEMENTS)if(e.visible&&KlyvrenFeatures.isEnabled(e.feature))draw(g,mc,e,false);}
    public static void renderEditor(GuiGraphics g){Minecraft mc=Minecraft.getInstance();if(mc.player==null)return;for(Element e:ELEMENTS)if(e.visible&&KlyvrenFeatures.isEnabled(e.feature))draw(g,mc,e,true);}
    private static void draw(GuiGraphics g,Minecraft mc,Element e,boolean editing){
        String s=value(mc,e);int w=Math.max(e.width,mc.font.width(s)+18);int a=Math.max(0,Math.min(255,opacity*255/100));
        int bg=(a<<24)|0x151820;int rim=((editing?210:a)<<24)|(rimColor&0xFFFFFF);
        g.fill(e.x-1,e.y-1,e.x+w+1,e.y+e.height+1,rim);g.fill(e.x,e.y,e.x+w,e.y+e.height,bg);
        g.drawString(mc.font,s,e.x+8,e.y+6,0xFFFFFFFF,false);
    }
    private static String value(Minecraft mc,Element e){LocalPlayer p=mc.player;return switch(e.feature){
      case "fps"->"FPS  "+mc.getFps();case "cps"->"CPS  "+left.size()+" / "+right.size();
      case "ping"->"PING  "+ping(mc)+" ms";case "xyz"->String.format("XYZ  %d %d %d",p.getBlockX(),p.getBlockY(),p.getBlockZ());
      case "keystrokes"->"W "+(mc.options.keyUp.isDown()?"▲":"·")+"  A "+(mc.options.keyLeft.isDown()?"◀":"·")+"  S "+(mc.options.keyDown.isDown()?"▼":"·")+"  D "+(mc.options.keyRight.isDown()?"▶":"·");
      case "clan"->"CLAN  "+clanName;case "clock"->"TIME  "+LocalTime.now().format(CLOCK);
      case "server"->"SERVER  "+(mc.getCurrentServer()==null?"singleplayer":mc.getCurrentServer().ip);
      case "armor"->"ARMOR  "+armor(p);case "target"->"TARGET  "+(mc.crosshairPickEntity==null?"none":mc.crosshairPickEntity.getName().getString());
      default->e.name;};}
    private static int armor(LocalPlayer p){int n=0;for(var s:net.minecraft.world.entity.EquipmentSlot.values())if(s.getType()==net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR&&!p.getItemBySlot(s).isEmpty())n++;return n;}
    private static int ping(Minecraft mc){if(mc.getConnection()==null||mc.player==null)return 0;var i=mc.getConnection().getPlayerInfo(mc.player.getUUID());return i==null?0:i.getLatency();}
    public static Element[] elements(){return ELEMENTS;} public static String getClanName(){return clanName;} public static void setClanName(String s){clanName=s==null||s.isBlank()?"KLYVREN":s.trim();save();}
    public static int getOpacity(){return opacity;} public static void setOpacity(int v){opacity=Math.max(0,Math.min(100,v));save();}
    public static int getRimColor(){return rimColor;} public static void cycleRimColor(){int[] c={0x8B5CF6,0x5B8CFF,0x22C55E,0xF59E0B,0xEF4444,0xFFFFFF};int i=0;for(int n=0;n<c.length;n++)if(c[n]==rimColor)i=n;rimColor=c[(i+1)%c.length];save();}
    public static void scale(Element e,int delta){e.width=Math.max(50,Math.min(500,e.width+delta));e.height=Math.max(18,Math.min(160,e.height+delta/2));save();}
    public static void load(){try{if(!Files.exists(CONFIG))return;JsonObject r=JsonParser.parseString(Files.readString(CONFIG)).getAsJsonObject();if(r.has("clan"))clanName=r.get("clan").getAsString();if(r.has("opacity"))opacity=r.get("opacity").getAsInt();if(r.has("rim"))rimColor=r.get("rim").getAsInt();for(Element e:ELEMENTS)if(r.has(e.name)){JsonObject o=r.getAsJsonObject(e.name);if(o.has("x"))e.x=o.get("x").getAsInt();if(o.has("y"))e.y=o.get("y").getAsInt();if(o.has("w"))e.width=o.get("w").getAsInt();if(o.has("h"))e.height=o.get("h").getAsInt();}}catch(Exception ignored){}}
    public static void save(){try{Files.createDirectories(CONFIG.getParent());JsonObject r=new JsonObject();r.addProperty("clan",clanName);r.addProperty("opacity",opacity);r.addProperty("rim",rimColor);for(Element e:ELEMENTS){JsonObject o=new JsonObject();o.addProperty("x",e.x);o.addProperty("y",e.y);o.addProperty("w",e.width);o.addProperty("h",e.height);r.add(e.name,o);}Files.writeString(CONFIG,GSON.toJson(r));}catch(IOException ignored){}}
    public static final class Element{public final String name,feature;public int x,y,width,height;public boolean visible=true;Element(String n,int x,int y,int w,int h,String f){name=n;this.x=x;this.y=y;width=w;height=h;feature=f;}public boolean contains(double mx,double my){return mx>=x&&mx<=x+width&&my>=y&&my<=y+height;}}
}