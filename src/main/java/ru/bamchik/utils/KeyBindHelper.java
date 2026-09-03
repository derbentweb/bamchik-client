package ru.bamchik.utils;

import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.Map;

public class KeyBindHelper {
    private static final Map<String, Integer> MAP = new HashMap<>();
    static {
        for (char c = 'A'; c <= 'Z'; c++) MAP.put(String.valueOf(c), GLFW.GLFW_KEY_A + (c - 'A'));
        for (int i = 0; i <= 9; i++) MAP.put(String.valueOf(i), GLFW.GLFW_KEY_0 + i);
        for (int i = 1; i <= 12; i++) MAP.put("F" + i, GLFW.GLFW_KEY_F1 + i - 1);
        MAP.put("ESCAPE", GLFW.GLFW_KEY_ESCAPE);
        MAP.put("ENTER", GLFW.GLFW_KEY_ENTER);
        MAP.put("SPACE", GLFW.GLFW_KEY_SPACE);
        MAP.put("TAB", GLFW.GLFW_KEY_TAB);
        MAP.put("SHIFT", GLFW.GLFW_KEY_LEFT_SHIFT);
        MAP.put("CONTROL", GLFW.GLFW_KEY_LEFT_CONTROL);
        MAP.put("ALT", GLFW.GLFW_KEY_LEFT_ALT);
        MAP.put("UP", GLFW.GLFW_KEY_UP);
        MAP.put("DOWN", GLFW.GLFW_KEY_DOWN);
        MAP.put("LEFT", GLFW.GLFW_KEY_LEFT);
        MAP.put("RIGHT", GLFW.GLFW_KEY_RIGHT);
    }

    public static int getKeyCode(String name) { return MAP.getOrDefault(name.toUpperCase(), -1); }
    public static String getKeyName(int code) {
        for (Map.Entry<String, Integer> e : MAP.entrySet()) if (e.getValue() == code) return e.getKey();
        return "Unknown";
    }
}