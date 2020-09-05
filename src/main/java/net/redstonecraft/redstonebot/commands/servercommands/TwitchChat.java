package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import net.redstonecraft.utils.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class TwitchChat implements ServerCommand {

    private JSONArray mods = new JSONArray();
    private Thread t1 = null;
    private Thread t2 = null;

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length > 0) {
                run(args[0]);
                eb.setColor(Color.decode("#00ff00"));
                eb.setDescription("Twitch-Chat zu " + args[0] + " gesetzt.");
            } else {
                return false;
            }
        } else {
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " twitchchat", "Verbindet den Twitch Chat.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " twitchchat [channelId]";
    }

    public void run(String twitchChannel) {
        if (t1 != null) {
            t1.stop();
        }
        if (t2 != null) {
            t2.stop();
        }
        t1 = new Thread(() -> {
            while (true) {
                try {
                    Request request = new Request("https://tmi.twitch.tv/group/user/" + twitchChannel + "/chatters");
                    request.connect();
                    JSONObject root = (JSONObject) new JSONParser().parse(request.getResponse());
                    JSONObject chatters = (JSONObject) root.get("chatters");
                    mods = (JSONArray) chatters.get("moderators");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException ignored) {
                }
            }
        });
        t1.start();
        t2 = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            while (true) {
                try {
                    Socket socket = new Socket("irc.twitch.tv", 6667);
                    String login = "PASS " + Main.config.get("twitchPassword") + "\r\nNICK " + Main.config.get("twitchNick") + (new Random().nextInt((99999 - 10000) + 1) + 10000) + "\r\nJOIN #" + twitchChannel + "\r\n";
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    Scanner input = new Scanner(socket.getInputStream());
                    out.print(login);
                    out.flush();
                    System.out.println("Logged in to Twitch Chat.");
                    while (true) {
                        if (input.hasNextLine()) {
                            String line = input.nextLine();
                            if (line.contains("PRIVMSG")) {
                                int index2 = line.indexOf("#", 1);
                                if (line.substring(index2 + 1).split(" ")[0].equals((String) Main.config.get("twitchChannel"))) {
                                    String authorName = line.split("!")[0].substring(1);
                                    int index = line.indexOf(':', 1);
                                    String message = line.substring(index + 1);
                                    boolean moderator = false;
                                    if (mods.contains(authorName)) {
                                        moderator = true;
                                    }
                                    boolean owner = false;
                                    if (authorName.equals((String) Main.config.get("twitchChannel"))) {
                                        owner = true;
                                    }
                                    EmbedBuilder eb = new EmbedBuilder();
                                    if (owner) {
                                        eb.setTitle("Twitch Streamer");
                                    } else if (moderator) {
                                        eb.setTitle("Twitch Mod");
                                    } else {
                                        eb.setTitle("Twitch");
                                    }
                                    eb.setColor(Color.decode("#6441a5"));
                                    eb.setAuthor(authorName);
                                    eb.setDescription(message);
                                    Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("liveChatChannel"))).sendMessage(eb.build()).queue();
                                }
                            } else if (line.contains("PING")) {
                                out.print("PONG tmi.twitch.tv\r\n");
                                out.flush();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t2.start();
    }
}
