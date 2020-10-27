package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;

public class SetStatus implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length <= 0) {
                return false;
            }
            switch (args[0]) {
                case "online":
                    Discord.INSTANCE.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                    break;
                case "idle":
                    Discord.INSTANCE.getJda().getPresence().setStatus(OnlineStatus.IDLE);
                    break;
                case "invisible":
                    Discord.INSTANCE.getJda().getPresence().setStatus(OnlineStatus.INVISIBLE);
                    break;
                case "donotdisturb":
                    Discord.INSTANCE.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    break;
                default:
                    return false;
            }
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription("Botstatus zu " + args[0].toUpperCase() + " gesetzt.");
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " setstatus", "Setzt den Botstatus.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " setstatus [ online | idle | invisible | donotdisturb ]";
    }
}
