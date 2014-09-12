package org.melocine.services;

import com.google.common.base.Joiner;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.MusicEntry;
import de.umass.lastfm.Track;
import org.melocine.events.EventDispatcher;
import org.melocine.events.ScrobbleNowPlayingSuccessEvent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/13/14
 * Time: 1:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class NowPlayingService {

    private final EventDispatcher eventDispatcher;
    private final LastFMService lastFMService;
    private final LyricsService lyricsService;

    public NowPlayingService(EventDispatcher eventDispatcher, LastFMService lastFMService, LyricsService lyricsService){
        this.eventDispatcher = eventDispatcher;
        this.lastFMService = lastFMService;
        this.lyricsService = lyricsService;
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(ScrobbleNowPlayingSuccessEvent.class, new EventDispatcher.Receiver<ScrobbleNowPlayingSuccessEvent>() {
            @Override
            public void receive(ScrobbleNowPlayingSuccessEvent event) {
                Track trackInfo = lastFMService.getTrackInfo(event.artist, event.track);
                System.err.println("track trackInfo: " + trackInfo.toString());
                String artist = getArtist(event.artist, trackInfo);
                String track = getTrackName(event.track, trackInfo);
                String album = getAlbum(trackInfo);
                String trackImageUrl = getImageUrl(trackInfo);
                String imgUrl = "".equals(trackImageUrl) ? getImageUrl(lastFMService.getArtistInfo(artist)) : trackImageUrl;
                String lyrics = lyricsService.getLyrics(artist, track);
                writeToFile(artist, track, album, imgUrl, lyrics);
            }
        });
    }

    private String getAlbum(Track info) {
        return info.getAlbum() == null ? "" : info.getAlbum();
    }

    private String getTrackName(String track, Track info) {
        return info.getName() == null ? track : info.getName();
    }

    private String getArtist(String artist, Track info) {
        return info.getArtist() == null ? artist : info.getArtist();
    }

    private String getImageUrl(MusicEntry info) {
        String imgUrl = "";
        System.err.println("img sizes: " + info.availableSizes());
        ImageSize imageSize = info.availableSizes().isEmpty() ? null : info.availableSizes().iterator().next();
        if (info.availableSizes().contains(ImageSize.LARGE))
            imageSize = ImageSize.LARGE;
        if (info.availableSizes().contains(ImageSize.EXTRALARGE))
            imageSize = ImageSize.EXTRALARGE;
        if (imageSize != null){
            imgUrl = info.getImageURL(imageSize);
            System.err.println("Setting img to: " + imgUrl + ", size=" + imageSize);
        }
        return imgUrl;
    }

    private void writeToFile(String artist, String track, String album, String imgUrl, String lyrics) {
        try {
            Charset charset = StandardCharsets.UTF_8;
            Path template = Paths.get("nowplaying.template");
            String content = new String(Files.readAllBytes(template), charset);
            content = content.replaceAll("TRACK_INFO_IMAGE_URL", imgUrl);
            content = content.replaceAll("TRACK_INFO_ARTIST", artist);
            content = content.replaceAll("TRACK_INFO_TITLE", track);
            content = content.replaceAll("TRACK_INFO_ALBUM", album);
            content = content.replaceAll("TRACK_INFO_LYRICS_HTML", lyrics);
            Path out = Paths.get("index.html");
            System.err.println("Writing to: " + out.toAbsolutePath());
            Files.write(out, content.getBytes(charset));
            System.err.println("Wrote " + content.getBytes().length + "bytes to: " + out.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Could not write now playing info: " + e + "\n" + Joiner.on("\n").join(e.getStackTrace()));
        }
    }
}
