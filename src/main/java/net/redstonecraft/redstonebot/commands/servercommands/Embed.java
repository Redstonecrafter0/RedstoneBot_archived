package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;

public class Embed implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            try {
                JSONObject root = (JSONObject) new JSONParser().parse(String.join(" ", args));
                if (root.containsKey("embed")) {
                    JSONObject embed = (JSONObject) root.get("embed");
                    if (embed.containsKey("title")) {
                        if (embed.containsKey("url")) {
                            eb.setTitle((String) embed.get("title"), (String) embed.get("url"));
                        } else {
                            eb.setTitle((String) embed.get("title"));
                        }
                    }
                    if (embed.containsKey("description")) {
                        eb.setDescription((String) embed.get("description"));
                    }
                    if (embed.containsKey("color")) {
                        if (embed.get("color") instanceof String) {
                            eb.setColor(Color.decode((String) embed.get("color")));
                        } else {
                            eb.setColor(Integer.parseInt(String.valueOf((long) embed.get("color"))));
                        }
                    }
                    if (embed.containsKey("timestamp")) {
                        eb.setTimestamp(new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toFormatter().parse((String) embed.get("timestamp")));
                    }
                    if (embed.containsKey("footer")) {
                        JSONObject footer = (JSONObject) embed.get("footer");
                        if (footer.containsKey("icon_url")) {
                            eb.setFooter((String) footer.get("text"), (String) footer.get("icon_url"));
                        } else {
                            eb.setFooter((String) footer.get("text"));
                        }
                    }
                    if (embed.containsKey("thumbnail")) {
                        eb.setThumbnail((String) ((JSONObject) embed.get("thumbnail")).get("url"));
                    }
                    if (embed.containsKey("image")) {
                        eb.setThumbnail((String) ((JSONObject) embed.get("image")).get("url"));
                    }
                    if (embed.containsKey("author")) {
                        JSONObject author = (JSONObject) embed.get("author");
                        if (author.containsKey("url")) {
                            if (author.containsKey("icon_url")) {
                                eb.setAuthor((String) author.get("name"), (String) author.get("url"), (String) author.get("icon_url"));
                            } else {
                                eb.setAuthor((String) author.get("name"), (String) author.get("url"));
                            }
                        } else {
                            eb.setAuthor((String) author.get("name"));
                        }
                    }
                    if (embed.containsKey("fields")) {
                        for (Object o : (JSONArray) embed.get("fields")) {
                            JSONObject i = (JSONObject) o;
                            if (i.containsKey("inline")) {
                                eb.addField((String) i.get("name"), (String) i.get("value"), (boolean) i.get("inline"));
                            } else {
                                eb.addField((String) i.get("name"), (String) i.get("value"), false);
                            }
                        }
                    }
                } else {
                    return false;
                }
            } catch (Exception ignored) {
                return false;
            }
            message.delete().queue();
        } else {
            eb.setTitle(Main.prefix);
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " embed", "Lässt den Bot eine Embed-Nachricht schreiben.\nJSON-Input: https://leovoel.github.io/embed-visualizer", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " embed [embed_as_json_string]";
    }

}
