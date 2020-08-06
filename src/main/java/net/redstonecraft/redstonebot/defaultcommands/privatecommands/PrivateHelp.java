package net.redstonecraft.redstonebot.defaultcommands.privatecommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.PrivateCommand;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PrivateHelp implements PrivateCommand {

    private HashMap<String, PrivateCommand> hashMap;

    public PrivateHelp(HashMap<String, PrivateCommand> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public boolean onCommand(PrivateChannel channel, User user, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        eb.setColor(Color.decode("#3498DB"));
        eb.addField(help());
        for (Map.Entry<String, PrivateCommand> i : hashMap.entrySet()) {
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
