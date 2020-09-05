package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Youtube {

    private static WebDriver driver;
    private static Thread thread = null;

    public Youtube() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
        startThread();
    }

    public static void reloadYoutube() {
        try {
            thread.stop();
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    thread.start();
                } catch (Exception ignored) {
                }
            }).start();
        } catch (Exception ignored) {
        }
    }

    private void startThread() {
        thread = new Thread(() -> {
            while (true) {
                driver.navigate().to("https://www.youtube.com/c/redstonecrafter0/videos");
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    List<WebElement> videos = driver.findElements(By.tagName("ytd-grid-video-renderer"));
                    if (videos.size() != 0) {
                        WebElement latestVideo = videos.get(0);
                        String title = latestVideo.findElement(By.id("dismissable")).findElement(By.id("details")).findElement(By.id("meta")).findElements(By.tagName("h3")).get(0).findElement(By.id("video-title")).getText();
                        String thumbnailUrl = latestVideo.findElement(By.id("dismissable")).findElements(By.tagName("ytd-thumbnail")).get(0).findElement(By.id("thumbnail")).findElements(By.tagName("yt-img-shadow")).get(0).findElement(By.id("img")).getAttribute("src").split("\\?")[0];
                        boolean isLive = false;
                        try {
                            if (latestVideo.findElement(By.id("dismissable")).findElements(By.tagName("ytd-thumbnail")).get(0).findElement(By.id("thumbnail")).findElement(By.id("overlays")).findElements(By.tagName("ytd-thumbnail-overlay-time-status-renderer")).get(0).getAttribute("overlay-style").toUpperCase().equals("LIVE")) {
                                isLive = true;
                            }
                        } catch (Exception ignored) {
                        }
                        String avatarUrl = driver.findElement(By.id("avatar")).findElement(By.id("img")).getAttribute("src");
                        String videoId = latestVideo.findElement(By.id("dismissable")).findElements(By.tagName("ytd-thumbnail")).get(0).findElement(By.id("thumbnail")).getAttribute("href").substring(32);
                        String channel = driver.findElement(By.id("channel-name")).findElement(By.id("container")).findElement(By.id("text-container")).findElement(By.id("text")).getText();
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle(title, "https://www.youtube.com/watch?v=" + videoId);
                        eb.setAuthor(channel, "https://youtube.com/channel/" + Main.config.get("ytChannelId"), avatarUrl);
                        eb.setColor(Color.decode("#FF0000"));
                        eb.setImage(thumbnailUrl);
                        if (isLive) {
                            eb.setDescription(channel + " hat ein Livestream gestartet. Sei dabei.\nhttps://www.youtube.com/watch?v=" + videoId);
                        } else {
                            eb.setDescription(channel + " hat ein neues Video hochgeladen. Schau es dir gerne an.\nhttps://www.youtube.com/watch?v=" + videoId);
                            Main.config.remove("ytLastVidId");
                            Main.config.put("ytLastVidId", videoId);
                            Main.saveConfig();
                        }
                        Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById((String) Main.config.get("announcementsChannel"))).sendMessage(eb.build()).queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
