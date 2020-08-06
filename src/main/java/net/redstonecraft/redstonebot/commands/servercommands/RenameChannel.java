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
import java.util.Arrays;

public class RenameChannel implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            if (args.length <= 1) {
                return false;
            }
            if (channel.getGuild().getGuildChannelById(args[0]) != null) {
                channel.getGuild().getGuildChannelById(args[0]).getManager().setName(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                eb.setColor(Color.decode("#00FF00"));
                eb.setDescription("Channel zu " + String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + " umbenannt.");
            } else {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Channel nicht gefunden.");
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Manage Channel");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " renamechannel", "Benennt einen Channel um.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " renamechannel [id] [name]";
    }
}
