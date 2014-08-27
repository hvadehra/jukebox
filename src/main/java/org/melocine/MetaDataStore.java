package org.melocine;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.melocine.events.EventDispatcher;
import org.melocine.events.ShutdownEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/27/14
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetaDataStore {

    private Map<File, MetaData> cache = new ConcurrentHashMap<File, MetaData>();

    public MetaDataStore(EventDispatcher eventDispatcher){
        loadCacheFromDisk();
        registerForShutdownEvent(eventDispatcher);
    }

    private void loadCacheFromDisk() {
        ObjectOutputStream oos = null;
        try{
            FileOutputStream fout = new FileOutputStream("metadata.cache", true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(cache);
        } catch (Exception e) {
            System.err.println("Could not load metadata from disk: " + e.getMessage());
        }
        if(oos != null){
            try {
                oos.close();
            } catch (IOException e) {
                System.err.println("Could not close output stream: " + e.getMessage());
            }
        }
    }

    private void registerForShutdownEvent(EventDispatcher eventDispatcher) {
        eventDispatcher.register(ShutdownEvent.class, new EventDispatcher.Receiver<ShutdownEvent>() {
            @Override
            public void receive(ShutdownEvent event) {
                writeMetadataToDisk();
            }
        });
    }

    private void writeMetadataToDisk() {
        try {
            FileOutputStream fout = new FileOutputStream("metadata.cache");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cache);
        } catch (Exception e) {
            System.err.println("Could not write metadata cache to disk: " + e.getMessage());
        }
    }

    public MetaData get(File file) {
        if (!cache.containsKey(file)){
            try {
                AudioFile f = AudioFileIO.read(file);
                Tag tag = f.getTag();
                AudioHeader audioHeader = f.getAudioHeader();
                cache.put(file, new MetaData(
                                tag.getFirst(FieldKey.ARTIST),
                                tag.getFirst(FieldKey.ALBUM),
                                tag.getFirst(FieldKey.TITLE),
                                tag.getFirst(FieldKey.RATING),
                                audioHeader.getTrackLength()
                ));
            } catch (Exception e) {
                System.err.println("Could not read tags: " + e.getMessage());
                cache.put(file, new MetaData("", "", file.getAbsolutePath(), "0", 0));
            }
        }
        return cache.get(file);
    }
}
