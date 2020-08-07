package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

public class Ping implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        return false;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " ping", "Zeigt deinen Ping", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " ping";
    }
}
