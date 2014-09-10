package org.melocine;

import org.melocine.events.EventDispatcher;
import org.melocine.events.NowPlayingEvent;
import org.melocine.events.ScrobbleSuccessEvent;

import java.io.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/10/14
 * Time: 3:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class URLReader {

    private final EventDispatcher eventDispatcher;

    public URLReader(EventDispatcher eventDispatcher){
        this.eventDispatcher = eventDispatcher;
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(ScrobbleSuccessEvent.class, new EventDispatcher.Receiver<ScrobbleSuccessEvent>() {
            @Override
            public void receive(ScrobbleSuccessEvent event) {
                String url = "http://www.last.fm/user/" + event.user + "/now";
                String outfile = "nowplaying.lastfm";
                String lastfm = getUrl(url);
                writeToFile(lastfm, outfile);
            }
        });
        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                String artist = sanitize(event.metaData.artist);
                String title = sanitize(event.metaData.title);
                String url = "http://www.azlyrics.com/lyrics/" + artist + "/" + title + ".html";
                String lyrics = getUrl(url);
                processLyrics(lyrics);
            }
        });
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

    private void processLyrics(String lyricsPage) {
        String outfile = "nowplaying.lyrics";
        int start = lyricsPage.indexOf("<!-- start of lyrics -->");
        int end = lyricsPage.indexOf("<!-- end of lyrics -->");
        String lyrics = (start < 0 || end < 0) ? "" : lyricsPage.substring(start, end);
        writeToFile(lyrics, outfile);
    }

    private String sanitize(String str) {
        if (str.isEmpty()) return str;
        return str.toLowerCase().replaceAll("[^a-z]", "").replace("the", "");
    }

    public String getUrl(String url){
        System.err.println("Fetching from: " + url);
        String output = "";
        try{
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                output = output + inputLine;
            }
            in.close();
        }
        catch(Exception e){
            System.err.println("Could not get url: " + e.getMessage());
        }
        return output;
    }

    public static void main(String[] args) throws Exception {
    }
}