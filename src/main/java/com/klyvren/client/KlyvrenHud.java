package com.klyvren.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;

public final class KlyvrenHud {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(KlyvrenClient.MOD_ID, "hud");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG = Minecraft.getInstance().gameDirectory.toPath().resolve("config/klyvren-hud.json");
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static String clanName = "KLYVREN";
    private static final Deque<Long> leftClicks = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();
    private static boolean previousLeft, previousRight;

    public static final Element FPS=e("FPS",10,10,90,22,"fps"), CPS=e("CPS",10,36,100,22,"cps"),
      PING=e("PING",10,62,110,22,"ping"), XYZ=e("XYZ",10,88,180,22,"xyz"),
      KEYSTROKES=e("KEYSTROKES",10,114,140,42,"keystrokes"), CLAN=e("CLAN",10,166,150,22,"clan"),
      CLOCK=e("CLOCK",10,192,110,22,"clock"), SERVER=e("SERVER",10,218,180,22,"server"),
      ARMOR=e("ARMOR",10,244,150,22,"armor"), TARGET=e("TARGET",10,270,170,22,"target");
    private static final Element[] ELEMENTS={FPS,CPS,PING,XYZ,KEYSTROKES,CLAN,CLOCK,SERVER,ARMOR,TARGET};

    private KlyvrenHud() {}
    private static Element e(String n,int x,int y,int w,int h,String f){return new Element(n,x,y,w,h,f);}

    public static void tick(Minecraft client){
        if(client.player==null)return;
        long now=System.currentTimeMillis(); trim(leftClicks,now); trim(rightClicks,now);
        long window=client.getWindow().getWindow();
        boolean left=GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_LEFT)==GLFW.GLFW_PRESS;
        boolean right=GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_RIGHT)==GLFW.GLFW_PRESS;
        if(left&&!previousLeft)leftClicks.addLast(now);
        if(right&&!previousRight)rightClicks.addLast(now);
        previousLeft=left; previousRight=right;
        if(KlyvrenFeatures.isEnabled("fullbright")){try{client.options.gamma().set(16.0);}catch(Exception ignored){}}
    }
    private static void trim(Deque<Long>d,long n){while(!d.isEmpty()&&n-d.peekFirst()>1000)d.removeFirst();}
    public static void render(GuiGraphics g, net.minecraft.client.DeltaTracker delta){
        Minecraft mc=Minecraft.getInstance(); if(mc.player==null||mc.options.hideGui)return;
        for(Element e:ELEMENTS)if(e.visible&&KlyvrenFeatures.isEnabled(e.feature))drawElement(g,mc,e,false);
    }
    public static void renderEditor(GuiGraphics g){
        Minecraft mc=Minecraft.getInstance(); if(mc.player==null)return;
        for(Element e:ELEMENTS)if(KlyvrenFeatures.isEnabled(e.feature))drawElement(g,mc,e,true);
    }
    private static void drawElement(GuiGraphics g,Minecraft mc,Element e,boolean editing){
        String text=value(mc,e); int w=Math.max(e.width,mc.font.width(text)+18);
        g.fill(e.x-1,e.y-1,e.x+w+1,e.y+e.height+1,editing?0xAA8B5CF6:0x990B0D12);
        g.fill(e.x,e.y,e.x+w,e.y+e.height,0xCC151820);
        g.drawString(mc.font,text,e.x+8,e.y+6,0xFFFFFFFF,false);
    }
    private static String value(Minecraft mc,Element e){
        LocalPlayer p=mc.player;
        return switch(e.feature){
          case "fps"->"FPS  "+mc.getFps();
          case "cps"->"CPS  "+leftClicks.size()+" / "+rightClicks.size();
          case "ping"->"PING  "+ping(mc)+" ms";
          case "xyz"->String.format("XYZ  %d %d %d",p.getBlockX(),p.getBlockY(),p.getBlockZ());
          case "keystrokes"->"W "+(mc.options.keyUp.isDown()?"▲":"·")+"  A "+(mc.options.keyLeft.isDown()?"◀":"·")+"  S "+(mc.options.keyDown.isDown()?"▼":"·")+"  D "+(mc.options.keyRight.isDown()?"▶":"·");
          case "clan"->"CLAN  "+clanName;
          case "clock"->"TIME  "+LocalTime.now().format(CLOCK);
          case "server"->"SERVER  "+(mc.getCurrentServer()==null?"singleplayer":mc.getCurrentServer().ip);
          case "armor"->"ARMOR  "+armor(p);
          case "target"->"TARGET  "+(mc.crosshairPickEntity==null?"none":mc.crosshairPickEntity.getName().getString());
          default->e.name;
        };
    }
    private static int armor(LocalPlayer p){int n=0;for(var a:p.getArmorSlots())if(!a.isEmpty())n++;return n;}
    private static int ping(Minecraft mc){if(mc.getConnection()==null||mc.player==null)return 0;var i=mc.getConnection().getPlayerInfo(mc.player.getUUID());return i==null?0:i.getLatency();}
    public static Element[] elements(){return ELEMENTS;}
    public static String getClanName(){return clanName;}
    public static void setClanName(String v){clanName=v==null||v.isBlank()?"KLYVREN":v.trim();save();}
    public static void load(){try{if(!Files.exists(CONFIG))return;JsonObject r=JsonParser.parseString(Files.readString(CONFIG)).getAsJsonObject();if(r.has("clan"))clanName=r.get("clan").getAsString();for(Element e:ELEMENTS)if(r.has(e.name)){JsonObject o=r.getAsJsonObject(e.name);if(o.has("x"))e.x=o.get("x").getAsInt();if(o.has("y"))e.y=o.get("y").getAsInt();}}catch(Exception ignored){}}
    public static void save(){try{Files.createDirectories(CONFIG.getParent());JsonObject r=new JsonObject();r.addProperty("clan",clanName);for(Element e:ELEMENTS){JsonObject o=new JsonObject();o.addProperty("x",e.x);o.addProperty("y",e.y);r.add(e.name,o);}Files.writeString(CONFIG,GSON.toJson(r));}catch(IOException ignored){}}
    public static final class Element{
      public final String name,feature; public int x,y,width,height; public boolean visible=true;
      public Element(String n,int x,int y,int w,int h,String f){name=n;this.x=x;this.y=y;width=w;height=h;feature=f;}
      public boolean contains(double mx,double my){return mx>=x&&mx<=x+width&&my>=y&&my<=y+height;}
    }
}