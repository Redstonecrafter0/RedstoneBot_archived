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

public class Unmute implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (message.getMentionedMembers().size() == 0) {
                return false;
            }
            if (args.length < 1) {
                return false;
            }
            if (message.getMentionedMembers().get(0) == null) {
                return false;
            }
            try {
                if (!message.getMentionedMembers().get(0).getRoles().contains(channel.getGuild().getRoleById((String) Main.config.get("mutedRole")))) {
                    eb.setColor(Color.decode("#FF0000"));
                    eb.setDescription("Dieser Nutzer war nicht gemuted.");
                } else {
                    eb.setColor(Color.decode("#00FF00"));
                    channel.getGuild().removeRoleFromMember(message.getMentionedMembers().get(0), Objects.requireNonNull(channel.getGuild().getRoleById((String) Main.config.get("mutedRole")))).queue();
                    Main.sql.update("DELETE FROM muted WHERE dcId = '" + message.getMentionedMembers().get(0).getId() + "'");
                    eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " wurde verfrüht von " + member.getAsMention() + " entmuted.");
                }
            } catch (Exception ignored) {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Ein Fehler ist aufgetreten");
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
        return new MessageEmbed.Field(Main.commandPrefix + " unmute", "Entmuted einen Nutzer", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " unmute [user]";
    }
}
