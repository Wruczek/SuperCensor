package mr.wruczek.supercensor3.utils.classes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import mr.wruczek.supercensor3.utils.LoggerUtils;

/**
 * The MIT License (MIT)
 * Created on 27/01/2016.
 * Updated on 15/02/2016
 * Copyright (c) 2016 Vinetos
 */
public class Reflection {

    public static Class<?> getClass(String classname) {
        try {
            String version = getNmsVersion();
            String path = classname.replace("{nms}", "net.minecraft.server." + version)
                    .replace("{nm}", "net.minecraft." + version)
                    .replace("{cb}", "org.bukkit.craftbukkit.." + version);
            return Class.forName(path);
        } catch (Exception e) {
            LoggerUtils.handleException(e);
            return null;
        }
    }

    public static Class<?> getNmsClass(String className) {
        try {
            String version = getNmsVersion();
            String path = "net.minecraft.server." + version + "." + className;
            return Class.forName(path);
        } catch (Exception e) {
            LoggerUtils.handleException(e);
            return null;
        }
    }

    public static Class[] getArrayClass(String classname, int arraySize) {
        try {
            String version = getNmsVersion();
            String path = classname.replace("{nms}", "net.minecraft.server." + version)
                    .replace("{nm}", "net.minecraft." + version)
                    .replace("{cb}", "org.bukkit.craftbukkit.." + version);
            return new Class[]{Array.newInstance(getClass(classname), arraySize).getClass()};
        } catch (Exception e) {
            LoggerUtils.handleException(e);
            return null;
        }
    }

    public static String getNmsVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static Object getNmsPlayer(Player p) throws Exception {
        Method getHandle = p.getClass().getMethod("getHandle");
        return getHandle.invoke(p);
    }

    public static Object getNmsScoreboard(Scoreboard s) throws Exception {
        Method getHandle = s.getClass().getMethod("getHandle");
        return getHandle.invoke(s);
    }

    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static Object getFieldValueFromSuperClass(Class<?> superClass, Object instance, String fieldName) throws Exception {
        Field field = superClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static ArrayList<Field> getFields(Object instance, Class<?> fieldType) throws Exception {
        Field[] fields = instance.getClass().getDeclaredFields();
        ArrayList<Field> fieldArrayList = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getType() == fieldType) {
                field.setAccessible(true);
                fieldArrayList.add(field);
            }
        }

        return fieldArrayList;
    }

    public static ArrayList<Field> getArraysFields(Object instance, Class<?> fieldType) throws Exception {
        String[] values = fieldType.toString().split(" ");
        String fieldName = values[values.length - 1];
        Field[] fields = instance.getClass().getDeclaredFields();
        ArrayList<Field> fieldArrayList = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getType().isArray()) {
                if (field.getType().toString().contains(fieldName)) {
                    // System.out.println("FOund !");
                    field.setAccessible(true);
                    fieldArrayList.add(field);
                } else {
                    // System.out.println("Nop: " + field.getType().toString() + " |> " + fieldName);
                }
            }
        }
        return fieldArrayList;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Exception e) {
            LoggerUtils.handleException(e);
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static void setValue(Object instance, String field, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            LoggerUtils.handleException(e);
        }
    }

    public static void sendAllPacket(Object packet) throws Exception {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Object nmsPlayer = getNmsPlayer(p);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            connection.getClass().getMethod("sendPacket", Reflection.getClass("{nms}.Packet")).invoke(connection, packet);
        }
    }

    public static void sendListPacket(List<String> players, Object packet) {
        try {
            for (String name : players) {
                Object nmsPlayer = getNmsPlayer(Bukkit.getPlayer(name));
                Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
                connection.getClass().getMethod("sendPacket", Reflection.getClass("{nms}.Packet")).invoke(connection, packet);
            }
        } catch (Exception e) {
            LoggerUtils.handleException(e);
        }
    }

    public static void sendPlayerPacket(Player p, Object packet) throws Exception {
        Object nmsPlayer = getNmsPlayer(p);
        Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        connection.getClass().getMethod("sendPacket", Reflection.getClass("{nms}.Packet")).invoke(connection, packet);
    }

    public static void sendMessage(Player p, Object message) throws Exception {
        Object nmsPlayer = getNmsPlayer(p);
        nmsPlayer.getClass().getMethod("sendMessage", getClass("{nms}.IChatBaseComponent")).invoke(nmsPlayer, message);

    }

    public static void sendMessage(Player p, String message) throws Exception {
        Object chat = getNmsClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, message);
        sendMessage(p, chat);
    }

    public static void sendMessages(Player p, Object message) throws Exception {
        Object nmsPlayer = getNmsPlayer(p);
        Class<?> c = Reflection.getClass("{nms}.IChatBaseComponent");
        Method m = nmsPlayer.getClass().getMethod("sendMessage", new Class[]{Array.newInstance(c, 4).getClass()});
        m.invoke(nmsPlayer, new Object[]{message});

    }

    public static int ping(Player p) throws Exception {
        Object nmsPlayer = Reflection.getNmsPlayer(p);
        return Integer.valueOf(getFieldValue(nmsPlayer, "ping").toString());
    }
}
