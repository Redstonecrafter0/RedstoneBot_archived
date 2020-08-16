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

public class SetPatchChannel implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length < 1) {
                return false;
            }
            if (channel.getGuild().getTextChannelById(args[0]) != null) {
                Main.config.remove("patchchannel");
                Main.config.put("patchchannel", args[0]);
                eb.setDescription("Der neue Patch Channel ist " + Objects.requireNonNull(channel.getGuild().getTextChannelById(args[0])).getAsMention());
                eb.setColor(Color.decode("#00FF00"));
            } else {
                eb.setDescription("Es gab einen Fehler.");
                eb.setColor(Color.decode("#FF0000"));
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " setpatchchannel", "Setzt den Patchchannel", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " setpatchchannel [id]";
    }
}
