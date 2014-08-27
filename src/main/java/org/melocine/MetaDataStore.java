package org.melocine;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
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

    private static final Map<File, MetaData> cache = new ConcurrentHashMap<File, MetaData>();

    public static MetaData get(File file) {
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
