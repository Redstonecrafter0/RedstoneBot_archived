package net.redstonecraft.redstonebot.defaultcommands.privatecommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.PrivateCommand;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PrivateHelp implements PrivateCommand {

    private final HashMap<String, PrivateCommand> hashMap;
    private final int page;
    private final Message helpMsg;
    private static final int maxPerPage = 5;

    public PrivateHelp(HashMap<String, PrivateCommand> hashMap, int page, Message helpMsg) {
        this.hashMap = hashMap;
        this.page = page;
        this.helpMsg = helpMsg;
    }

    @Override
    public boolean onCommand(PrivateChannel channel, User user, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix + " - Help - " + (page + 1));
        eb.setColor(Color.decode("#3498DB"));
        eb.addField(help());
        int c = 0;
        for (Map.Entry<String, PrivateCommand> i : hashMap.entrySet()) {
            if ((c >= (maxPerPage * page)) && (c < (maxPerPage * (page + 1)))) {
                eb.addField(i.getValue().help());
            }
            c++;
        }
        Message msg;
        if (helpMsg == null) {
            msg = channel.sendMessage(eb.build()).complete();
        } else {
            msg = helpMsg;
            msg.clearReactions().complete();
            msg.editMessage(eb.build()).complete();
        }
        if (page > 0) {
            msg.addReaction("⬅").complete();
        }
        if (hashMap.entrySet().size() > (maxPerPage * (page + 1))) {
            msg.addReaction("➡").queue();
        }
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
