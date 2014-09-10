package org.melocine;

import org.melocine.events.EventDispatcher;
import org.melocine.events.NowPlayingEvent;
import org.melocine.events.ScrobbleSuccessEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

    public LyricsService(EventDispatcher eventDispatcher, URLReader urlReader){
        this.eventDispatcher = eventDispatcher;
        this.urlReader = urlReader;
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(ScrobbleSuccessEvent.class, new EventDispatcher.Receiver<ScrobbleSuccessEvent>() {
            @Override
            public void receive(ScrobbleSuccessEvent event) {
                String url = "http://www.last.fm/user/" + event.user + "/now";
                String outfile = "nowplaying.lastfm";
                String lastfm = urlReader.getUrl(url);
                writeToFile(lastfm, outfile);
            }
        });
        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                String outfile = "nowplaying.lyrics";
                String lyrics = getFromAZLyrics(event.metaData.artist, event.metaData.title);
                if (lyrics.isEmpty())
                    lyrics = getFromLyricsMania(event.metaData.artist, event.metaData.title);
                writeToFile(lyrics, outfile);
            }
        });
    }

    private String getFromAZLyrics(String artist, String title) {
        String url = "http://www.azlyrics.com/lyrics/" + sanitizeForAZ(artist) + "/" + sanitizeForAZ(title) + ".html";
        String lyricsPage = urlReader.getUrl(url);
        int start = lyricsPage.indexOf("<!-- start of lyrics -->");
        int end = lyricsPage.indexOf("<!-- end of lyrics -->");
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
        int end = lyricsPage.indexOf("</div>");
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
        return str.toLowerCase().replaceAll("[^a-z]", "").replace("the", "");
    }

    private String sanitizeForLM(String str) {
        if (str.isEmpty()) return str;
        return str.toLowerCase().replace(" ", "_").replaceAll("[^a-z_]", "");
    }

}