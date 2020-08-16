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
import net.redstonecraft.utils.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Objects;

public class NameHistory implements ServerCommand {

    private long lastUsed = System.currentTimeMillis();
    private final File file = new File("tmp2.png");

    public NameHistory() {
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
            Request request = new Request("https://api.mojang.com/users/profiles/minecraft/" + args[0]);
            request.connect();
            String answer = request.getResponse();
            if (!answer.equals("")) {
                try {
                    JSONObject root = (JSONObject) new JSONParser().parse(answer);
                    String uuid = (String) root.get("id");
                    Request request2 = new Request("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                    request2.connect();
                    JSONObject root2 = (JSONObject) new JSONParser().parse(request2.getResponse());
                    JSONArray properties = (JSONArray) root2.get("properties");
                    JSONObject item = (JSONObject) properties.get(0);
                    String base64 = (String) item.get("value");
                    byte[] bytes = Base64.getDecoder().decode(base64);
                    String skinObject = new String(bytes);
                    JSONObject root3 = (JSONObject) new JSONParser().parse(skinObject);
                    JSONObject textures = (JSONObject) root3.get("textures");
                    JSONObject skin = (JSONObject) textures.get("SKIN");
                    String skinUrl = (String) skin.get("url");
                    BufferedImage skinImage = ImageIO.read(new URL(skinUrl));
                    BufferedImage head = scaleImage(skinImage.getSubimage(8, 8, 8, 8));
                    ImageIO.write(head, "png", file);
                    Message msg = Objects.requireNonNull(Discord.INSTANCE.getManager().getUserById((String) Main.config.get("trashId"))).openPrivateChannel().complete().sendFile(file).complete();
                    String headUrl = msg.getAttachments().get(0).getUrl();
                    eb.setThumbnail(headUrl);
                    Request request3 = new Request("https://api.mojang.com/user/profiles/" + uuid + "/names");
                    request3.connect();
                    JSONArray root4 = (JSONArray) new JSONParser().parse(request3.getResponse());
                    for (Object o : root4) {
                        JSONObject i = (JSONObject) o;
                        if (i.get("changedToAt") != null) {
                            eb.addField(((String) i.get("name")).replaceAll("_", "\\_"), "Geändert am " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(i.get("changedToAt")), false);
                        } else {
                            eb.addField((String) i.get("name"), "Original", false);
                        }
                    }
                    eb.setColor(Color.decode("#00FF00"));
                    eb.setDescription(args[0] + "'s Namen waren:");
                    request3.close();
                    request2.close();
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Dieser Spieler existiert nicht.");
            }
            request.close();
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dieser Befehl darf nur einmal pro Minute verwendet werden.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " namehistory", "Zeigt eine Liste aller Namen die ein Minecraftaccount bisher hatte.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " namehistory [Username]";
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
