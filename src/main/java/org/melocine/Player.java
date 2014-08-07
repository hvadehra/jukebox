package org.melocine;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 7/27/14
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player {

    private static final int PROGRESS_WIDTH = 100;
    private final List<File> nowPlaying;
    private File currentPlaying;
    private MediaPlayer mediaPlayer;
    private final LastFM lastFM;

    public Player(LastFM lastFM) {
        this.lastFM = lastFM;
        this.nowPlaying = new ArrayList<File>();
    }

    private void play(){
        String path = currentPlaying.toURI().toASCIIString();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                next();
            }
        });
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration duration = mediaPlayer.getMedia().getDuration();
                MetaData metaData = new MetaData(mediaPlayer.getMedia().getMetadata(), duration.toSeconds());
                System.out.println("\r" + (1 + nowPlaying.indexOf(currentPlaying)) + ". " + metaData.artist + " - " + metaData.title + "  [" + metaData.album + "] [" + formatTime(duration) + "]");
                lastFM.setNowPlaying(metaData);
            }
        });
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                Double oldValueSeconds = oldValue.toSeconds();
                Double newValueSeconds = newValue.toSeconds();
                if (newValueSeconds.intValue() != oldValueSeconds.intValue()){
                    Double duration = mediaPlayer.getMedia().getDuration().toSeconds();
                    String done = StringUtils.repeat("=", (int) ((newValueSeconds / duration) * PROGRESS_WIDTH));
                    String remaining = StringUtils.repeat("-", (int)(((duration- newValueSeconds)/duration) * PROGRESS_WIDTH));
                    System.out.print("\r[" + done + "[" + formatTime(newValue) + "]" + remaining + "]");
                    if (newValueSeconds.intValue() == duration.intValue()/2) {
                        lastFM.scrobble(new MetaData(mediaPlayer.getMedia().getMetadata(), duration));
                    }
                }
            }
        });
        mediaPlayer.play();
    }

    public void next() {
        System.err.println("Disposing");
        mediaPlayer.dispose();
        System.out.print("\r" + StringUtils.repeat(" ", 125));
        currentPlaying = nowPlaying.get(nowPlaying.indexOf(currentPlaying) + 1);
        play();
    }

    private String formatTime(Duration duration) {
        return formatDuration(Long.valueOf(String.valueOf(Double.valueOf(duration.toMillis()).intValue())), "mm:ss");
    }

    public void playAll(final Collection<File> files) {
        System.err.println("Playing " + files.size());
        nowPlaying.clear();
        nowPlaying.addAll(files);
        currentPlaying = nowPlaying.get(0);
        play();
    }

    public void togglePlayPause() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED)
            mediaPlayer.play();
        else
            mediaPlayer.pause();
    }
}
