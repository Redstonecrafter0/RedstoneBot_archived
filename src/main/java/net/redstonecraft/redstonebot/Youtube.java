package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.redstonecraft.utils.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Youtube {

    private final String[] keys;
    private int currentKey = 0;
    public static String currentLive = "";

    public Youtube() {
        JSONArray array = (JSONArray) Main.config.get("ytApiKeys");
        List<String> list = new ArrayList<>();
        for (Object object : array.toArray()) {
            list.add((String) object);
        }
        keys = list.toArray(new String[0]);
    }

    public void run() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            while (true) {
                Request request = new Request("https://www.googleapis.com/youtube/v3/search?key=" + keys[currentKey] + "&channelId=" + Main.config.get("ytChannelId") + "&part=snippet,id&order=date&maxResults=1");
                request.connect();
                currentKey++;
                if (currentKey >= 10) {
                    currentKey = 0;
                }
                try {
                    JSONObject response = (JSONObject) new JSONParser().parse(request.getResponse());
                    JSONArray items = (JSONArray) response.get("items");
                    if (items.size() != 0) {
                        JSONObject item = (JSONObject) items.get(0);
                        JSONObject idObj = (JSONObject) item.get("id");
                        String id = (String) idObj.get("videoId");
                        JSONObject snippet = (JSONObject) item.get("snippet");
                        String liveBroadcastContent = (String) (((JSONObject) item.get("snippet")).get("liveBroadcastContent"));
                        if (!id.equals((String) Main.config.get("ytLastVidId")) || !liveBroadcastContent.equals("")) {
                            JSONObject thumbnails = (JSONObject) snippet.get("thumbnails");
                            JSONObject thumbnail = (JSONObject) thumbnails.get("default");
                            String thumbnailUrl = (String) thumbnail.get("url");
                            String title = (String) snippet.get("title");
                            String channel = (String) snippet.get("channelTitle");
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle(title, "https://www.youtube.com/watch?v=" + id);
                            Request iconRequest = new Request("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=" + Main.config.get("ytChannelId") + "&fields=items%2Fsnippet%2Fthumbnails&key=" + keys[currentKey]);
                            iconRequest.connect();
                            JSONObject root = (JSONObject) new JSONParser().parse(iconRequest.getResponse());
                            iconRequest.close();
                            JSONArray items2 = (JSONArray) root.get("items");
                            JSONObject item2 = (JSONObject) items2.get(0);
                            JSONObject snippet2 = (JSONObject) item2.get("snippet");
                            JSONObject thumbnails2 = (JSONObject) snippet2.get("thumbnails");
                            JSONObject high = (JSONObject) thumbnails2.get("high");
                            String profileUrl = (String) high.get("url");
                            eb.setAuthor(channel, "https://youtube.com/channel/" + Main.config.get("ytChannelId"), profileUrl);
                            eb.setColor(Color.decode("#FF0000"));
                            eb.setImage(thumbnailUrl);
                            if (!currentLive.equals(id)) {
                                if (!liveBroadcastContent.equals("none") && !currentLive.equals(id)) {
                                    eb.setDescription(channel + " hat ein Livestream gestartet. Sei dabei.\nhttps://www.youtube.com/watch?v=" + id);
                                    currentLive = id;
                                    Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("announcementsChannel"))).sendMessage(eb.build()).queue();
                                } else {
                                    if (!id.equals((String) Main.config.get("ytLastVidId"))) {
                                        eb.setDescription(channel + " hat ein neues Video hochgeladen. Schau es dir gerne an.\nhttps://www.youtube.com/watch?v=" + id);
                                        currentLive = "";
                                        Main.config.remove("ytLastVidId");
                                        Main.config.put("ytLastVidId", id);
                                        Main.saveConfig();
                                        Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("announcementsChannel"))).sendMessage(eb.build()).queue();
                                    }
                                }
                            }
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
            }
        }).start();
    }

}
