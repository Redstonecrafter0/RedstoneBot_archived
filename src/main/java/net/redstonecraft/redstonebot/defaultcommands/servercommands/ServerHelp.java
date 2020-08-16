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

    private final HashMap<String, ServerCommand> hashMap;
    private final int page;
    private final Message helpMsg;
    private static final int maxPerPage = 5;

    public ServerHelp(HashMap<String, ServerCommand> hashMap, int page, Message helpMsg) {
        this.hashMap = hashMap;
        this.page = page;
        this.helpMsg = helpMsg;
    }

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix + " - Help - " + (page + 1));
        eb.setColor(Color.decode("#3498DB"));
        eb.addField(help());
        int c = 0;
        for (Map.Entry<String, ServerCommand> i : hashMap.entrySet()) {
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
