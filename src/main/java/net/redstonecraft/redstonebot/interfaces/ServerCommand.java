package net.redstonecraft.redstonebot.interfaces;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface ServerCommand extends Command {

    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args);

}
