package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.redstonecraft.utils.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.util.Objects;

public class MinecraftPatches {

    public void run() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(180 * 1000);
                    Request request = new Request("https://launchermeta.mojang.com/mc/game/version_manifest.json");
                    request.connect();
                    JSONObject root = (JSONObject) new JSONParser().parse(request.getResponse());
                    JSONArray versions = (JSONArray) root.get("versions");
                    JSONObject version = (JSONObject) versions.get(0);
                    String id = (String) version.get("id");
                    if (!id.equals((String) Main.config.get("latestMcVersion"))) {
                        String type = (String) version.get("type");
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle(Main.prefix);
                        eb.setColor(Color.decode("#3498DB"));
                        if (type.equals("release")) {
                            eb.setDescription("Es gibt eine neue Version im Minecraftlauncher.");
                        } else if (type.equals("snapshot")) {
                            eb.setDescription("Es gibt einen neuen Snapshot im Minecraftlauncher.");
                        }
                        eb.addField(type.toUpperCase(), id, false);
                        Main.config.remove("latestMcVersion");
                        Main.config.put("latestMcVersion", id);
                        Main.saveConfig();
                        Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("patchchannel"))).sendMessage(eb.build()).queue();
                    }
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

}
