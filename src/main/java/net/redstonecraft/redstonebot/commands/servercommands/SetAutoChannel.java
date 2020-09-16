package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.util.Objects;

public class SetAutoChannel implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length <= 0) {
                return false;
            }
            if (channel.getGuild().getVoiceChannelById(args[0]) == null) {
                return false;
            }
            Main.config.remove(Main.config.get("autochannel"));
            Main.config.put("autochannel", args[0]);
            Main.saveConfig();
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription("Der neue Autochannel channel ist " + Objects.requireNonNull(channel.getGuild().getVoiceChannelById(args[0])).getName() + ".");
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " setautochannel", "Setzt den Autochannel Channel.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + "setautochannel [voiceChannelId]";
    }
}
