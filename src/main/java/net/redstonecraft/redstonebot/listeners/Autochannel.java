package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Autochannel extends ListenerAdapter {

    private final List<VoiceChannel> voiceChannels = new ArrayList<>();

    public void onEnable() {
        List<VoiceChannel> list = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getVoiceChannelById((String) Main.config.get("autochannel"))).getParent()).getVoiceChannels();
        for (VoiceChannel i : list) {
            if (!Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getVoiceChannelById((String) Main.config.get("autochannel"))).equals(i)) {
                if (i.getMembers().size() == 0) {
                    i.delete().queue();
                } else {
                    voiceChannels.add(i);
                }
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!event.getChannelJoined().getId().equals(Main.config.get("autochannel"))) {
            return;
        }
        Category category = event.getChannelJoined().getParent();
        VoiceChannel voiceChannel = Objects.requireNonNull(category).createVoiceChannel("TALK " + event.getMember().getEffectiveName()).setParent(category).complete();
        voiceChannel.getManager().sync().complete();
        event.getChannelJoined().getGuild().moveVoiceMember(event.getMember(), voiceChannel).complete();
        voiceChannels.add(voiceChannel);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!voiceChannels.contains(event.getChannelLeft())) {
            return;
        }
        if (!(event.getChannelLeft().getMembers().size() == 0)) {
            return;
        }
        voiceChannels.remove(event.getChannelLeft());
        event.getChannelLeft().delete().queue();
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (event.getChannelJoined().getId().equals(Main.config.get("autochannel"))) {
            Category category = event.getChannelJoined().getParent();
            VoiceChannel voiceChannel = Objects.requireNonNull(category).createVoiceChannel("TALK " + event.getMember().getEffectiveName()).setParent(category).complete();
            voiceChannel.getManager().sync().complete();
            event.getChannelJoined().getGuild().moveVoiceMember(event.getMember(), voiceChannel).complete();
            voiceChannels.add(voiceChannel);
        }
        if (!voiceChannels.contains(event.getChannelLeft())) {
            return;
        }
        if (!(event.getChannelLeft().getMembers().size() == 0)) {
            return;
        }
        voiceChannels.remove(event.getChannelLeft());
        event.getChannelLeft().delete().queue();
    }

}
