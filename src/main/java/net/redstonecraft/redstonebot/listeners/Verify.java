package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Verify extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (Main.config.get("unverifiedRole") == null) {
            return;
        }
        if (!event.getMember().getUser().isBot()) {
            if (event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")) != null) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(Main.prefix);
                eb.setColor(Color.decode("#FF0000"));
                event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")))).queue();
                String verifyId = Main.randomString("QWERTZIUOPASDFGHJKLYXCVBNMqwertzuioplkjhgfdsayxcvbnm0123456789", 32);
                ResultSet rs = Main.sql.query("SELECT * FROM members WHERE verifyId = '" + verifyId + "'");
                boolean c = true;
                try {
                    rs.getString("verifyId");
                } catch (SQLException ignored) {
                    c = false;
                }
                while (c) {
                    verifyId = Main.randomString("QWERTZIUOPASDFGHJKLYXCVBNMqwertzuioplkjhgfdsayxcvbnm0123456789", 32);
                    rs = Main.sql.query("SELECT * FROM members WHERE verifyId = '" + verifyId + "'");
                    try {
                        rs.getString("verifyId");
                    } catch (SQLException ignored) {
                        c = false;
                    }
                }
                Main.sql.update("INSERT INTO members VALUES ('" + event.getMember().getId() + "', '" + verifyId + "', 0)");
                eb.setDescription("Verifiziere dich in dem du in den Verifychannel von " + event.getGuild().getName() + " den Verifizierungscode schreibst.\nDamit akzeptierst du die Serverregeln.");
                eb.addField("Verifizierungscode", verifyId, false);
                event.getMember().getUser().openPrivateChannel().complete().sendMessage(eb.build()).queue();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (Main.config.get("unverifiedRole") == null) {
            return;
        }
        try {
            ResultSet rs = Main.sql.query("SELECT * FROM members WHERE dcId = '" + event.getUser().getId() + "'");
            if (rs.isClosed()) {
                return;
            }
            if (rs.getString("dcId") != null) {
                Main.sql.update("DELETE FROM members WHERE dcId = '" + event.getUser().getId() + "'");
            }
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getChannel().getId().equals((String) Main.config.get("verifyChannel"))) {
            return;
        }
        try {
            ResultSet rs = Main.sql.query("SELECT * FROM members WHERE dcId = '" + Objects.requireNonNull(event.getMember()).getId() + "'");
            if (event.getMessage().getContentDisplay().equals(rs.getString("verifyId"))) {
                event.getGuild().removeRoleFromMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")))).queue();
                event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("verifiedRole")))).queue();
                Main.sql.update("UPDATE members SET verified = '1' WHERE dcId = '" + event.getMember().getId() + "'");
                File file = new File("tmp.png");
                BufferedImage bufferedImage = new BufferedImage(1000, 500, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = bufferedImage.createGraphics();
                graphics.setColor(Color.decode("#FFFFFF"));
                Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("font.ttf")).deriveFont(50F);
                graphics.setFont(font);
                FontMetrics metrics = graphics.getFontMetrics();
                graphics.drawString("Willkommen", 500 - (metrics.stringWidth("Willkommen") / 2), 400);
                graphics.drawString(event.getMember().getUser().getAsTag(), 500 - (metrics.stringWidth(event.getMember().getUser().getAsTag()) / 2), 460);
                HttpURLConnection connection;
                if (event.getMember().getUser().getAvatarUrl() != null) {
                    connection = (HttpURLConnection) new URL(Objects.requireNonNull(event.getMember().getUser().getAvatarUrl())).openConnection();
                } else {
                    connection = (HttpURLConnection) new URL(Objects.requireNonNull(event.getMember().getUser().getDefaultAvatarUrl())).openConnection();
                }
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
                graphics.setClip(new Ellipse2D.Float(350, 30, 300, 300));
                graphics.drawImage(ImageIO.read(connection.getInputStream()), 350, 30, 300, 300, null);
                if (!file.exists()) {
                    file.createNewFile();
                }
                ImageIO.write(bufferedImage, "png", file);
                Objects.requireNonNull(event.getGuild().getTextChannelById((String) Main.config.get("welcome"))).sendFile(file).append("Willkommen auf dem Server ").append(event.getMember().getAsMention()).queue();
            }
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        event.getMessage().delete().queue();
    }
}
