package net.redstonecraft.redstonebot.defaultcommands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ServerHelp implements ServerCommand {

    private HashMap<String, ServerCommand> hashMap;

    public ServerHelp(HashMap<String, ServerCommand> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        eb.setColor(Color.decode("#3498DB"));
        eb.addField(help());
        for (Map.Entry<String, ServerCommand> i : hashMap.entrySet()) {
            eb.addField(i.getValue().help());
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " help", "Zeigt diese Hilfe an.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + "help";
    }
}
