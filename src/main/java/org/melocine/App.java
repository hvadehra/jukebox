package org.melocine;

import com.google.common.base.Joiner;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.melocine.events.EventDispatcher;
import org.melocine.events.PlayAllEvent;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 7/27/14
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class App extends Application {

    private static File dir;
    private static int width = 80;
    private static int height = 50;

    @Override
    public void start(Stage stage) throws Exception {
        Collection<File> files = FileUtils.listFiles(
                dir,
                new RegexFileFilter(".*mp3$", IOCase.INSENSITIVE),
                DirectoryFileFilter.DIRECTORY
        );
        System.err.println("Found " + files.size() + " tracks.");
        Properties properties = new PropertiesService().properties();

        EventDispatcher eventDispatcher = new EventDispatcher();
        MetaDataStore metaDataStore = new MetaDataStore(eventDispatcher);
        new LastFM(eventDispatcher, "7eb89485dc9374c4ebbe506a18ff8f8b", "2e6628b43ae789c509ecb50c1437d5d8", properties.getProperty("lastfm.password"), properties.getProperty("lastfm.username"));
        new Player(eventDispatcher, metaDataStore);
        new KeyListener(eventDispatcher);
        new Display(eventDispatcher, metaDataStore, width, height);
        new URLReader(eventDispatcher);
        List<File> playList = buildPlayList(files);
        eventDispatcher.dispatch(new PlayAllEvent(playList));
    }

    private List<File> buildPlayList(Collection<File> files) {
        List<File> playList = new ArrayList<File>();
        playList.addAll(files);
        for (int i = 0; i < playList.size(); i++) {
            int random = i + new Random().nextInt(playList.size()-i);
            File element = playList.remove(random);
            playList.add(i, element);
        }
        return playList;
    }

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        if (args.length == 0){
            dir = new File(".");
        }
        else {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
            dir = new File(Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));
        }
        System.err.println("Starting in: " + dir.getAbsolutePath());
        launch(args);
    }
}
