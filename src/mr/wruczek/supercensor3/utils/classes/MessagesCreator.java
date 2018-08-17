package mr.wruczek.supercensor3.utils.classes;

import java.util.List;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The MIT License (MIT)
 * Created on 31/01/2016.
 * Copyright (c) 2016 Vinetos
 */
public class MessagesCreator {

    private JSONObject chatObject;

    public MessagesCreator(String text, Color color, List<ChatFormat> formats) {
        chatObject = new JSONObject();
        chatObject.put("text", text);
        if (color != null) {
            chatObject.put("color", color.getColorString());
        }
        if (formats != null) {
            for (ChatFormat format : formats) {
                chatObject.put(format.getFormatString(), true);
            }
        }
    }

    public MessagesCreator addExtra(ChatExtra extraObject) {
        if (!chatObject.containsKey("extra")) {
            chatObject.put("extra", new JSONArray());
        }
        JSONArray extra = (JSONArray) chatObject.get("extra");
        extra.add(extraObject.toJSON());
        chatObject.put("extra", extra);
        return this;
    }

    public String toString() {
        return chatObject.toJSONString();
    }

    public static String stripColor(String input) {
        if (input == null) {
            return null;
        }

        return ChatColor.stripColor(input);
    }

    public static class ChatExtra {
        private JSONObject chatExtra;

        public ChatExtra(String text, Color color, List<ChatFormat> formats) {
            chatExtra = new JSONObject();
            chatExtra.put("text", text);
            chatExtra.put("color", color.getColorString());
            if (formats != null) {
                for (ChatFormat format : formats) {
                    chatExtra.put(format.getFormatString(), true);
                }
            }
        }

        public ChatExtra setClickEvent(ClickEventType action, String value) {
            JSONObject clickEvent = new JSONObject();
            clickEvent.put("action", action.getTypeString());
            clickEvent.put("value", value);
            chatExtra.put("clickEvent", clickEvent);
            return this;
        }

        public ChatExtra setHoverEvent(HoverEventType action, String value) {
            JSONObject hoverEvent = new JSONObject();
            hoverEvent.put("action", action.getTypeString());
            hoverEvent.put("value", value);
            chatExtra.put("hoverEvent", hoverEvent);
            return this;
        }

        public JSONObject toJSON() {
            return chatExtra;
        }

        public ChatExtra build() {
            return this;
        }
    }

    public static enum Color {
        WHITE("white"),
        YELLOW("yellow"),
        LIGHT_PURPLE("light_purple"),
        RED("red"),
        AQUA("aqua"),
        GREEN("green"),
        BLUE("blue"),
        DARK_GRAY("dark_gray"),
        GRAY("gray"),
        GOLD("gold"),
        DARK_PURPLE("dark_purple"),
        DARK_RED("dark_red"),
        DARK_AQUA("dark_aqua"),
        DARK_GREEN("dark_green"),
        DARK_BLUE("dark_blue"),
        BLACK("black");
        private final String color;

        Color(String color) {
            this.color = color;
        }

        String getColorString() {
            return color;
        }
    }

    public static enum ClickEventType {
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
        OPEN_URL("open_url"),
        CHANGE_PAGE("change_page");

        private final String type;

        ClickEventType(String type) {
            this.type = type;
        }

        public String getTypeString() {
            return type;
        }
    }

    public static enum HoverEventType {
        SHOW_TEXT("show_text"),
        SHOW_ITEM("show_item"),
        SHOW_ACHIEVEMENT("show_achievement");
        private final String type;

        HoverEventType(String type) {
            this.type = type;
        }

        public String getTypeString() {
            return type;
        }
    }

    public static enum ChatFormat {
        BOLD("bold"),
        UNDERLINED("underlined"),
        STRIKETHROUGH("strikethrough"),
        ITALIC("italic"),
        OBFUSCATED("obfuscated");
        private final String format;

        ChatFormat(String format) {
            this.format = format;
        }

        public String getFormatString() {
            return format;
        }
    }

}
