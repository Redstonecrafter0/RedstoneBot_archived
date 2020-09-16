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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.Objects;

public class YoutubeChat implements ServerCommand {

    private boolean running = false;
    private static WebDriver driver;
    private final JavascriptExecutor js;
    private static Thread thread = null;

    public YoutubeChat() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
        js = (JavascriptExecutor) driver;
    }

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "disconnect":
                        stop();
                        eb.setColor(Color.decode("#ff0000"));
                        eb.setDescription("Disconnected the YouTube-Chat.");
                    case "connect":
                        if (args.length > 1) {
                            run(args[1]);
                            eb.setColor(Color.decode("#ff0000"));
                            eb.setDescription("Connected the YouTube-Chat.");
                        } else {
                            return false;
                        }
                }
            } else {
                return false;
            }
        } else {
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " youtubechat", "Verbindet den YouTube-Chat.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " youtubechat [ connect | disconnect ] <videoId>";
    }

    public static void stop() {
        try {
            thread.stop();
        } catch (Exception ignored) {
        }
        driver.quit();
    }

    public void run(String videoId) {
        thread = new Thread(() -> {
            while (true) {
                try {
                    if (!running) {
                        driver.navigate().to("https://www.youtube.com/live_chat?v=" + videoId);
                        running = true;
                        Thread.sleep(5000);
                        for (WebElement i : driver.findElements(By.tagName("yt-live-chat-text-message-renderer"))) {
                            js.executeScript("return document.getElementById(\"" + i.getAttribute("id") + "\").remove();");
                        }
                        System.out.println("Initialized YouTube Chat.");
                    }
                    for (WebElement i : driver.findElements(By.tagName("yt-live-chat-text-message-renderer"))) {
                        String profileUrl = i.findElements(By.tagName("yt-img-shadow")).get(0).findElements(By.tagName("img")).get(0).getAttribute("src");
                        WebElement content = i.findElement(By.id("content"));
                        String authorName = content.findElements(By.tagName("yt-live-chat-author-chip")).get(0).findElement(By.id("author-name")).getText().split("\"")[0];
                        boolean owner = false;
                        if (content.findElements(By.tagName("yt-live-chat-author-chip")).get(0).findElement(By.id("author-name")).getAttribute("class").contains("owner")) {
                            owner = true;
                        }
                        WebElement chatBadges = content.findElements(By.tagName("yt-live-chat-author-chip")).get(0).findElement(By.id("chat-badges"));
                        boolean moderator = false;
                        if (chatBadges.findElements(By.tagName("yt-live-chat-author-badge-renderer")).size() > 0) {
                            for (WebElement e : chatBadges.findElements(By.tagName("yt-live-chat-author-badge-renderer"))) {
                                if (e.getAttribute("type").equals("moderator")) {
                                    moderator = true;
                                }
                            }
                        }
                        String message = content.findElement(By.id("message")).getText();
                        try {
                            js.executeScript("return document.getElementById(\"" + i.getAttribute("id") + "\").remove();");
                            EmbedBuilder eb = new EmbedBuilder();
                            if (owner) {
                                eb.setTitle("YouTube Streamer");
                            } else if (moderator) {
                                eb.setTitle("YouTube Mod");
                            } else {
                                eb.setTitle("YouTube");
                            }
                            eb.setAuthor(authorName, profileUrl, profileUrl);
                            eb.setDescription(message);
                            eb.setColor(Color.decode("#FF0000"));
                            Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("liveChatChannel"))).sendMessage(eb.build()).queue();
                        } catch (Exception ignored) {
                        }
                    }
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        });
        thread.start();
    }
}
