package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

public class SetStatus implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        return false;
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
