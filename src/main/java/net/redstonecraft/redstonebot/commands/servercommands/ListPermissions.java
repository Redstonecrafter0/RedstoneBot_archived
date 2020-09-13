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

public class ListPermissions implements ServerCommand {

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            eb.setColor(Color.decode("#00ff00"));
            eb.setDescription("Hier sind alle Permissions:");
            for (Permission i : Permission.values()) {
                if (!i.equals(Permission.UNKNOWN)) {
                    eb.addField(i.getName(), "Der Wert ist\n" + i.name(), true);
                }
            }
        } else {
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Du brauchst Administrator-Rechte für die Commands die diese Info benutzt.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " listpermissions", "Listet alle möglichen Permissions auf.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " listpermissions";
    }
}
