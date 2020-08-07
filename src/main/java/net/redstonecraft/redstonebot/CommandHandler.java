package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class CommandHandler extends ListenerAdapter {

    private final List<VoiceChannel> voiceChannels = new ArrayList<>();

    public static ListenerAdapter INSTANCE;

    public CommandHandler() {
        INSTANCE = this;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)  {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentDisplay().startsWith(Main.commandPrefix)) {
            return;
        }
        TextChannel channel = event.getTextChannel();
        Member member = event.getMember();
        Message message = event.getMessage();
        String msg = message.getContentDisplay();
        if (!msg.startsWith(Main.commandPrefix + " ") && !(msg.equals(Main.commandPrefix) || msg.equals(Main.commandPrefix + " "))) {
            msg = Main.commandPrefix + " " + msg.substring(Main.commandPrefix.length());
        }
        String[] oriArgs = msg.split(" ");
        String command = "help";
        String[] args = new String[]{};
        if (oriArgs.length == 0) {
            args = new String[]{};
        } else {
            if (oriArgs[0].equals(Main.commandPrefix)) {
                if (!(oriArgs.length == 1)) {
                    command = oriArgs[1];
                    if (!(oriArgs.length == 2)) {
                        args = Arrays.copyOfRange(oriArgs, 2, oriArgs.length);
                    }
                }
            }
        }
        Main.commandManager.performServerCommand(command, channel, member, message, args);
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!event.getChannelJoined().getId().equals((String) Main.config.get("autochannel"))) {
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
        if (event.getChannelJoined().getId().equals((String) Main.config.get("autochannel"))) {
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

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentDisplay().startsWith(Main.commandPrefix)) {
            return;
        }
        PrivateChannel channel = event.getChannel();
        User member = event.getAuthor();
        Message message = event.getMessage();
        String msg = message.getContentDisplay();
        if (!msg.startsWith(Main.commandPrefix + " ") && !(msg.equals(Main.commandPrefix) || msg.equals(Main.commandPrefix + " "))) {
            msg = Main.commandPrefix + " " + msg.substring(Main.commandPrefix.length());
        }
        String[] oriArgs = msg.split(" ");
        String command = "help";
        String[] args = new String[]{};
        if (oriArgs.length == 0) {
            args = new String[]{};
        } else {
            if (oriArgs[0].equals(Main.commandPrefix)) {
                if (!(oriArgs.length == 1)) {
                    command = oriArgs[1];
                    if (!(oriArgs.length == 2)) {
                        args = Arrays.copyOfRange(oriArgs, 2, oriArgs.length);
                    }
                }
            }
        }
        Main.commandManager.performPrivateCommand(command, channel, member, message, args);
    }
}
