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

public class SetVerify implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        if (args.length <= 1) {
            return false;
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            boolean error = false;
            String object = null;
            switch (args[0]) {
                case "setchannel":
                    if (channel.getGuild().getTextChannelById(args[1]) == null) {
                        error = true;
                    } else {
                        object = "verifyChannel";
                        eb.setDescription("Der neue Verifychannel ist " + Objects.requireNonNull(channel.getGuild().getTextChannelById(args[1])).getAsMention());
                    }
                case "setverifiedrole":
                    if (channel.getGuild().getRoleById(args[1]) == null) {
                        error = true;
                    } else {
                        object = "verifiedRole";
                        eb.setDescription("Die neue Verified-Rolle ist " + Objects.requireNonNull(channel.getGuild().getRoleById(args[1])).getAsMention());
                    }
                case "setunverifiedrole":
                    if (channel.getGuild().getRoleById(args[1]) == null) {
                        error = true;
                    } else {
                        object = "unverifiedRole";
                        eb.setDescription("Die neue Unverified-Rolle ist " + Objects.requireNonNull(channel.getGuild().getRoleById(args[1])).getAsMention());
                    }
                case "setwelcomechannel":
                    if (channel.getGuild().getTextChannelById(args[1]) == null) {
                        error = true;
                    } else {
                        object = "welcome";
                        eb.setDescription("Der neue Verifychannel ist " + Objects.requireNonNull(channel.getGuild().getTextChannelById(args[1])).getAsMention());
                    }
            }
            if (error) {
                eb.setDescription("Die gegebene Id ist ungültig.");
                eb.setColor(Color.decode("#FF0000"));
            } else {
                Main.config.remove(object);
                Main.config.put(object, args[1]);
                Main.saveConfig();
                eb.setColor(Color.decode("#00FF00"));
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " verify", "Setzt Verifizierungsvariablen.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " verify [ setchannel | setverifiedrole | setunverifiedrole | setwelcomechannel ] [id]";
    }
}
