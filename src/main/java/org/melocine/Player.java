package org.melocine;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.melocine.events.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 7/27/14
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player {

    private final List<File> nowPlaying;
    private File currentPlaying;
    private MediaPlayer mediaPlayer;
    private final EventDispatcher eventDispatcher;
    private final MetaDataStore metaDataStore;
    private double volume = 1.0D;

    public Player(EventDispatcher eventDispatcher, MetaDataStore metaDataStore) {
        this.eventDispatcher = eventDispatcher;
        this.metaDataStore = metaDataStore;
        this.nowPlaying = new ArrayList<File>();
        registerForTrackEvents();
    }

    private void registerForTrackEvents() {
        eventDispatcher.register(NextTrackEvent.class, new EventDispatcher.Receiver<NextTrackEvent>() {
            @Override
            public void receive(NextTrackEvent event) {
                next();
            }
        });
        eventDispatcher.register(TogglePlayPauseEvent.class, new EventDispatcher.Receiver<TogglePlayPauseEvent>() {
            @Override
            public void receive(TogglePlayPauseEvent event) {
                togglePlayPause();
            }
        });
        eventDispatcher.register(PreviousTrackEvent.class, new EventDispatcher.Receiver<PreviousTrackEvent>() {
            @Override
            public void receive(PreviousTrackEvent event) {
                previous();
            }
        });

        eventDispatcher.register(PlayAllEvent.class, new EventDispatcher.Receiver<PlayAllEvent>() {
            @Override
            public void receive(PlayAllEvent event) {
                playAll(event.files);
            }
        });

        eventDispatcher.register(PlaySelectedTrackEvent.class, new EventDispatcher.Receiver<PlaySelectedTrackEvent>() {
            @Override
            public void receive(PlaySelectedTrackEvent event) {
                playSelectedTrack(event.currentSelectedIndex);
            }
        });

        eventDispatcher.register(VolumeDownEvent.class, new EventDispatcher.Receiver<VolumeDownEvent>() {
            @Override
            public void receive(VolumeDownEvent event) {
                if (volume <= 0.1){
                    volume = 0;
                }
                else{
                    volume -= 0.1;
                }
                mediaPlayer.setVolume(volume);
                eventDispatcher.dispatch(new VolumeChangedEvent(volume));
            }
        });

        eventDispatcher.register(VolumeUpEvent.class, new EventDispatcher.Receiver<VolumeUpEvent>() {
            @Override
            public void receive(VolumeUpEvent event) {
                if (volume >= 0.9){
                    volume = 1;
                }
                else{
                    volume += 0.1;
                }
                mediaPlayer.setVolume(volume);
                eventDispatcher.dispatch(new VolumeChangedEvent(volume));
            }
        });

        eventDispatcher.register(RandomizePlaylistAfterCurrent.class, new EventDispatcher.Receiver<RandomizePlaylistAfterCurrent>() {
            @Override
            public void receive(RandomizePlaylistAfterCurrent event) {
                randomizeListAfter(nowPlaying.indexOf(currentPlaying));
                eventDispatcher.dispatch(new PlayListChangedEvent(nowPlaying));
            }
        });
    }

    private void playSelectedTrack(int currentSelectedIndex) {
        currentPlaying = nowPlaying.get(currentSelectedIndex);
        disposeMediaPlayer();
        play();
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
                MetaData metaData = metaDataStore.get(currentPlaying.getAbsolutePath());
                eventDispatcher.dispatch(new NowPlayingEvent(nowPlaying.indexOf(currentPlaying), metaData, duration, nowPlaying));
            }
        });
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                Double oldValueSeconds = oldValue.toSeconds();
                Double newValueSeconds = newValue.toSeconds();
                if (newValueSeconds.intValue() != oldValueSeconds.intValue()) {
                    Double duration = mediaPlayer.getMedia().getDuration().toSeconds();
                    eventDispatcher.dispatch(new PlayTimeChangedEvent(duration, newValueSeconds));
                    if (newValueSeconds.intValue() == duration.intValue() / 2) {
                        eventDispatcher.dispatch(new ScrobbleTrackEvent(metaDataStore.get(currentPlaying.getAbsolutePath())));
                    }
                }
            }
        });
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    }

    public void playAll(final Collection<File> files) {
        System.err.println("Playing " + files.size());
        nowPlaying.clear();
        nowPlaying.addAll(files);
        currentPlaying = nowPlaying.get(0);
        play();
//        triggerBGMetaCacheBuilding(files);
    }

    private void triggerBGMetaCacheBuilding(final Collection<File> files) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (File file : files) {
                    metaDataStore.get(file.getAbsolutePath());
                    Thread.yield();
                }
            }
        }).start();
    }

    public void next() {
        currentPlaying = nowPlaying.get(nowPlaying.indexOf(currentPlaying) + 1);
        disposeMediaPlayer();
        play();
    }

    public void previous() {
        if (mediaPlayer.currentTimeProperty().getValue().toSeconds() > 10){
            mediaPlayer.seek(new Duration(0));
            return;
        }
        currentPlaying = nowPlaying.get(nowPlaying.indexOf(currentPlaying) - 1);
        disposeMediaPlayer();
        System.out.print("\r" + StringUtils.repeat(" ", 125));
        play();
    }

    private void disposeMediaPlayer() {
        System.err.println("Disposing");
        mediaPlayer.dispose();
    }

    public void togglePlayPause() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED)
            mediaPlayer.play();
        else
            mediaPlayer.pause();
    }

    private void randomizeListAfter(int after) {
        if (after >= nowPlaying.size() - 2) return;

        int begin = after + 1;
        int end = nowPlaying.size();
        int random = begin + new Random().nextInt(end - begin);
        File track = nowPlaying.remove(random);
        nowPlaying.add(begin, track);
        randomizeListAfter(after+1);
    }

}
