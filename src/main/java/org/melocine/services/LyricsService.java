package org.melocine.services;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/10/14
 * Time: 3:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class LyricsService {

    private final URLReader urlReader;

    public LyricsService(URLReader urlReader) {
        this.urlReader = urlReader;
    }

    public String getLyrics(String artist, String title) {
        String lyrics;
            lyrics = getFromLyricsMania(artist, title);
        if (lyrics.isEmpty())
            lyrics = getFromAZLyrics(artist, title);
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

    private String sanitizeForAZ(String str) {
        if (str.isEmpty()) return str;
        return str.toLowerCase().replaceAll("[^a-z0-9]", "").replace("the", "");
    }

    private String sanitizeForLM(String str) {
        if (str.isEmpty()) return str;
        return str.toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9_]", "");
    }

}