package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.redstonecraft.utils.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.util.Objects;

public class Twitch {

    public void run() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            while (true) {
                try {
                    Request request = new Request("https://api.twitch.tv/helix/streams?user_login=" + Main.config.get("twitchChannel"));
                    request.connect();
                    request.addHeader("client-id", (String) Main.config.get("twitchClientId"));
                    request.addHeader("Authorization", (String) Main.config.get("twitchBearer"));
                    try {
                        JSONObject root = (JSONObject) new JSONParser().parse(request.getResponse());
                        JSONArray data = (JSONArray) root.get("data");
                        if (data.size() > 0) {
                            JSONObject item = (JSONObject) data.get(0);
                            String id = (String) item.get("id");
                            if (!id.equals((String) Main.config.get("twitchLastStreamId"))) {
                                Main.config.remove("twitchLastStreamId");
                                Main.config.put("twitchLastStreamId", id);
                                Main.saveConfig();
                                String title = (String) item.get("title");
                                String channel = (String) item.get("user_name");
                                String thumbnailUrl = ((String) item.get("thumbnail_url")).replace("{width}", "1920").replace("{height}", "1080");
                                EmbedBuilder eb = new EmbedBuilder();
                                Request profileRequest = new Request("https://api.twitch.tv/helix/users?login=" + Main.config.get("twitchChannel"));
                                profileRequest.connect();
                                profileRequest.addHeader("client-id", (String) Main.config.get("twitchClientId"));
                                profileRequest.addHeader("Authorization", (String) Main.config.get("twitchBearer"));
                                JSONObject root2 = (JSONObject) new JSONParser().parse(profileRequest.getResponse());
                                JSONArray data2 = (JSONArray) root2.get("data");
                                JSONObject item2 = (JSONObject) data2.get(0);
                                String profileUrl = (String) item2.get("profile_image_url");
                                if (!profileUrl.equals("")) {
                                    eb.setAuthor(channel, "https://twitch.tv/" + Main.config.get("twitchChannel"), profileUrl);
                                } else {
                                    eb.setAuthor(channel, "https://twitch.tv/" + Main.config.get("twitchChannel"));
                                }
                                profileRequest.close();
                                eb.setTitle(title, "https://twitch.tv/" + Main.config.get("twitchChannel"));
                                eb.setColor(Color.decode("#FF0000"));
                                eb.setImage(thumbnailUrl);
                                eb.setDescription(channel + " hat ein Livestream gestartet. Sei dabei.\nhttps://twitch.tv/" + Main.config.get("twitchChannel"));
                                Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("announcementsChannel"))).sendMessage(eb.build()).queue();
                                Discord.INSTANCE.getJda().getPresence().setActivity(Activity.streaming((String) Main.config.get("twitchChannel"), "https://twitch.tv/" + ((String) Main.config.get("twitchChannel"))));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    request.close();
                    try {
                        Thread.sleep(100 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

}
