package net.redstonecraft.redstonebot.interfaces;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public interface PrivateCommand extends Command {

    boolean onCommand(PrivateChannel channel, User user, Message message, String[] args);

}
