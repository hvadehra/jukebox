package org.melocine;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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

    public Player() {
    }

    private void play(final File file, final int n, final Runnable endOfMediaCallBack){
        String path = file.toURI().toASCIIString();
        final Media media = new Media(path);
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                System.err.println("Disposing");
                mediaPlayer.dispose();
                System.err.println("Running callback");
                System.out.print("\r" + StringUtils.repeat(" ", 125));
                endOfMediaCallBack.run();
            }
        });
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                ObservableMap<String,Object> metadata = mediaPlayer.getMedia().getMetadata();
                System.out.println("\r" + n + ". " + metadata.get("artist") + " - " + metadata.get("title") + "  [" + metadata.get("album") + "] [" + formatTime(mediaPlayer.getMedia().getDuration()) + "]");
            }
        });
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                Double oldValueSeconds = oldValue.toSeconds();
                Double newValueSeconds = newValue.toSeconds();
                if (newValueSeconds.intValue() != oldValueSeconds.intValue()){
                    Double duration = media.getDuration().toSeconds();
                    String done = StringUtils.repeat("=", (int) ((newValueSeconds / duration) * PROGRESS_WIDTH));
                    String remaining = StringUtils.repeat("-", (int)(((duration-newValueSeconds)/duration) * PROGRESS_WIDTH));
                    System.out.print("\r[" + done + "[" + formatTime(newValue) + "]" + remaining + "]");
                }
            }
        });
        mediaPlayer.play();
    }

    private String formatTime(Duration duration) {
        return formatDuration(Long.valueOf(String.valueOf(Double.valueOf(duration.toMillis()).intValue())), "mm:ss");
    }

    private void playAll(final List<File> files, final int n) {
        System.err.println("Playing " + files.size());
        if (files.isEmpty()) {
            System.exit(1);
        }
        File file = files.get(0);
        play(file, n, new Runnable() {
            @Override
            public void run() {
                System.err.println("Starting callback");
                playAll(files.subList(1, files.size()), n+1);
                System.err.println("Done with callback");
            }
        });
        System.err.println("Queued " + files.size());
    }

    public void playAll(List<File> playList) {
        playAll(playList, 1);
    }
}
