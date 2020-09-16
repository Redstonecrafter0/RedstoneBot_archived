package net.redstonecraft.redstonebot.interfaces;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface Command {

    MessageEmbed.Field help();

    String usage();

}
