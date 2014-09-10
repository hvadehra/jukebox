package org.melocine;

import com.google.common.base.Joiner;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;
import org.melocine.events.EventDispatcher;
import org.melocine.events.ScrobbleNowPlayingSuccessEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/10/14
 * Time: 3:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class LyricsService {

    private final EventDispatcher eventDispatcher;
    private final URLReader urlReader;
    private final LastFMService lastFMService;

    public LyricsService(EventDispatcher eventDispatcher, URLReader urlReader, LastFMService lastFMService){
        this.eventDispatcher = eventDispatcher;
        this.urlReader = urlReader;
        this.lastFMService = lastFMService;
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(ScrobbleNowPlayingSuccessEvent.class, new EventDispatcher.Receiver<ScrobbleNowPlayingSuccessEvent>() {
            @Override
            public void receive(ScrobbleNowPlayingSuccessEvent event) {
                Track info = lastFMService.getTrackInfo(event.artist, event.track);
                System.err.println("track info: " + info.toString());
                String artist = info.getArtist() == null ? event.artist : info.getArtist();
                String track = info.getName() == null ? event.track : info.getName();
                String album = info.getAlbum() == null ? "" : info.getAlbum();
                System.err.println("img sizes: " + info.availableSizes());
                ImageSize imageSize = info.availableSizes().isEmpty() ? null : info.availableSizes().iterator().next();
                if (info.availableSizes().contains(ImageSize.LARGE))
                    imageSize = ImageSize.LARGE;
                if (info.availableSizes().contains(ImageSize.EXTRALARGE))
                    imageSize = ImageSize.EXTRALARGE;
                String imgUrl = imageSize == null ? "" : info.getImageURL(imageSize);
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    Path template = Paths.get("nowplaying.template");
                    String content = new String(Files.readAllBytes(template), charset);
                    System.err.println("Setting img to: " + imgUrl + ", size=" + imageSize);
                    content = content.replaceAll("TRACK_INFO_IMAGE_URL", imgUrl);
                    content = content.replaceAll("TRACK_INFO_ARTIST", artist);
                    content = content.replaceAll("TRACK_INFO_TITLE", track);
                    content = content.replaceAll("TRACK_INFO_ALBUM", album);

                    String lyrics = getLyrics(artist, track);
                    content = content.replaceAll("TRACK_INFO_LYRICS_HTML", lyrics);

                    Path out = Paths.get("index.html");
                    System.err.println("Writing to: " + out.toAbsolutePath());
                    Files.write(out, content.getBytes(charset));
                } catch (IOException e) {
                    System.err.println("Could not write now playing info: " + e + "\n" + Joiner.on("\n").join(e.getStackTrace()));
                }
            }
        });
//        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
//            @Override
//            public void receive(NowPlayingEvent event) {
//                String outfile = "nowplaying.lyrics";
//                String lyrics = getLyrics(event);
//                writeToFile(lyrics, outfile);
//            }
//        });
    }

    private String getLyrics(String artist, String title) {
        String lyrics = getFromAZLyrics(artist, title);
        if (lyrics.isEmpty())
            lyrics = getFromLyricsMania(artist, title);
        return lyrics;
    }

    private String getFromAZLyrics(String artist, String title) {
        String url = "http://www.azlyrics.com/lyrics/" + sanitizeForAZ(artist) + "/" + sanitizeForAZ(title) + ".html";
        String lyricsPage = urlReader.getUrl(url);
        int start = lyricsPage.indexOf("<!-- start of lyrics -->");
        int end = lyricsPage.indexOf("<!-- end of lyrics -->", start);
        return (start < 0 || end < 0) ? "" : lyricsPage.substring(start, end);
    }

    private String getFromLyricsMania(String artist, String title) {
        String url = "http://www.lyricsmania.com/" + sanitizeForLM(title) + "_lyrics_" + sanitizeForLM(artist) + ".html";
        String lyricsPage = urlReader.getUrl(url);
        if (lyricsPage.isEmpty()) return "";
        int start = lyricsPage.indexOf("/* LyricsMania.com - Above Lyrics */");
        if (start < 0) return "";
        start = lyricsPage.indexOf("<div class=\"lyrics-body\"", start);
        start = lyricsPage.indexOf("</strong>", start);
        start = lyricsPage.indexOf(">", start) + 1;
        int end = lyricsPage.indexOf("</div>", start);
        return (start < 0 || end < 0 || start > lyricsPage.length() || end > lyricsPage.length()) ? "" : lyricsPage.substring(start, end);
    }

    private void writeToFile(String data, String outfile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
            writer.write(data);
            writer.close();
            System.err.println("Wrote " + data.getBytes().length + " bytes to: " + outfile);
        } catch (IOException e) {
            System.err.println("Could not write data to: " + outfile + " : " + e.getMessage());
        }

    }

    private String sanitizeForAZ(String str) {
        if (str.isEmpty()) return str;
        return str.toLowerCase().replaceAll("[^a-z0-9]", "").replace("the", "");
    }

    private String sanitizeForLM(String str) {
        if (str.isEmpty()) return str;
        return str.toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9_]", "");
    }

}