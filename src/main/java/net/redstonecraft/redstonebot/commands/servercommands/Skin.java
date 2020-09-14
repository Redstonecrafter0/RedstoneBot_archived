package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Skin implements ServerCommand {

    private long lastUsed = System.currentTimeMillis();
    private File file = new File("tmp2.png");

    public Skin() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        if (args.length == 0) {
            return false;
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (((System.currentTimeMillis() - lastUsed) > 60000) || member.hasPermission(Permission.ADMINISTRATOR)) {
            if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                lastUsed = System.currentTimeMillis();
            }
            try {
                BufferedImage skinImage = ImageIO.read(new URL("https://minotar.net/skin/" + args[0]));
                BufferedImage head = scaleImage(skinImage.getSubimage(8, 8, 8, 8));
                ImageIO.write(head, "png", file);
                Message msg = Objects.requireNonNull(Discord.INSTANCE.getManager().getUserById((String) Main.config.get("trashId"))).openPrivateChannel().complete().sendFile(file).complete();
                String headUrl = msg.getAttachments().get(0).getUrl();
                eb.setImage("https://minotar.net/skin/" + args[0]);
                eb.setThumbnail(headUrl);
                eb.setColor(Color.decode("#00FF00"));
                eb.setDescription("Der Skin von " + args[0] + " ist unter https://minotar.net/download/" + args[0] + " downloadbar.");
            } catch (IOException e) {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Dieser Spieler existiert nicht.");
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dieser Befehl darf nur einmal pro Minute verwendet werden.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " skin", "Gibt den Downloadlink zum angegebenen Spieler", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " skin [Username]";
    }

    private static BufferedImage scaleImage(BufferedImage original) {
        BufferedImage after = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                for (int w = 0; w < 64; w++) {
                    for (int h = 0; h < 64; h++) {
                        after.setRGB(x * 64 + w, y * 64 + h, original.getRGB(x, y));
                    }
                }
            }
        }
        return after;
    }
}
