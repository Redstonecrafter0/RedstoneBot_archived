package net.redstonecraft.redstonebot.interfaces;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface Command {

    public MessageEmbed.Field help();

    public String usage();

}
