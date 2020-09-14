package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Leveling extends ListenerAdapter {

    private List<Member> inChannel = new ArrayList<>();

    public Leveling() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            inChannel = new ArrayList<>();
            for (VoiceChannel i : Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getVoiceChannels()) {
                if (!i.equals(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getAfkChannel())) {
                    for (Member m : i.getMembers()) {
                        if (!m.getUser().isBot()) {
                            inChannel.add(m);
                        }
                    }
                }
            }
            try {
                Thread.sleep(600);
            } catch (InterruptedException ignored) {
            }
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1200);
                        for (Member i : inChannel) {
                            if (Objects.requireNonNull(i).getRoles().contains(Discord.INSTANCE.getManager().getRoleById((String) Main.config.get("verifiedRole")))) {
                                ResultSet rs = Main.sql.query("SELECT * FROM leveling WHERE dcId = '" + i.getId() + "'");
                                long x = (long) (1 + (Math.random() * 1));
                                if (rs.isClosed()) {
                                    Main.sql.update("INSERT INTO leveling VALUES ('" + i.getId() + "', '" + x + "')");
                                } else {
                                    Main.sql.update("UPDATE leveling SET xp = '" + (Long.parseLong(rs.getString("xp")) + x) + "' WHERE dcId = '" +  i.getId() + "'");
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }).start();
        }).start();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getMessage().getContentDisplay().startsWith(Main.commandPrefix)) {
            return;
        }
        if (Objects.requireNonNull(event.getMember()).getRoles().contains(Discord.INSTANCE.getManager().getRoleById((String) Main.config.get("verifiedRole")))) {
            ResultSet rs = Main.sql.query("SELECT * FROM leveling WHERE dcId = '" + event.getMember().getId() + "'");
            try {
                long x = (long) (1 + (Math.random() * 4));
                if (rs.isClosed()) {
                    Main.sql.update("INSERT INTO leveling VALUES ('" + event.getMember().getId() + "', '" + x + "')");
                } else {
                    Main.sql.update("UPDATE leveling SET xp = '" + (Long.parseLong(rs.getString("xp")) + x) + "' WHERE dcId = '" +  event.getMember().getId() + "'");
                }
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!event.getMember().getUser().isBot()) {
            if (!Objects.equals(Objects.requireNonNull(event.getMember().getVoiceState()).getChannel(), Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getAfkChannel())) {
                inChannel.add(event.getMember());
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        inChannel.remove(event.getMember());
    }
}
