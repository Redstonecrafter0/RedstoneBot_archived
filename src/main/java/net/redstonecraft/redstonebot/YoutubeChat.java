package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.Objects;

public class YoutubeChat {

    private boolean running = false;
    private ChromeOptions chromeOptions = new ChromeOptions();
    private static WebDriver driver;
    private JavascriptExecutor js;
    private static Thread thread;

    public YoutubeChat() {
        System.setProperty("webdriver.chrome.driver", (String) Main.config.get("chromeDriverPath"));
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
        js = (JavascriptExecutor) driver;
    }

    public void run() {
        thread = new Thread(() -> {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ignored) {
            }
            while (true) {
                try {
                    if (!Youtube.currentLive.equals("")) {
                        if (!running) {
                            driver.navigate().to("https://www.youtube.com/live_chat?v=" + Youtube.currentLive);
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
                    } else {
                        if (running) {
                            driver.navigate().to("https://google.de");
                            running = false;
                        }
                    }
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        });
        thread.start();
    }

    public static void stop() {
        try {
            thread.stop();
        } catch (Exception ignored) {
        }
        driver.quit();
    }

}
