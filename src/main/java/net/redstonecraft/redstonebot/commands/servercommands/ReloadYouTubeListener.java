package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.Youtube;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;

public class ReloadYouTubeListener implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            Youtube.reloadYoutube();
            eb.setColor(Color.decode("#00ff00"));
            eb.setDescription("Reloaded YouTube-Listener.");
        } else {
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " reloadyoutubelistener", "Lädt den YouTube-Listener neu.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " reloadyoutubelistener";
    }
}
