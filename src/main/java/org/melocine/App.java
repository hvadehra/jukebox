package org.melocine;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

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

    @Override
    public void start(Stage stage) throws Exception {
        Collection<File> files = FileUtils.listFiles(
                dir,
                new RegexFileFilter(".*mp3$", IOCase.INSENSITIVE),
                DirectoryFileFilter.DIRECTORY
        );
        System.err.println("Found " + files.size() + " tracks.");
        Properties properties = new PropertiesService().properties();
        LastFM lastFM = new LastFM("7eb89485dc9374c4ebbe506a18ff8f8b", "2e6628b43ae789c509ecb50c1437d5d8", properties.getProperty("lastfm.password"), properties.getProperty("lastfm.username"));
        final Player player = new Player(lastFM);
        List<File> playList = buildPlayList(files);
        player.playAll(playList);
        new KeyListener(player);

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
        else{
            dir = new File(args[0]);
        }
        System.err.println("Starting in: " + dir.getAbsolutePath());
        launch(args);
    }
}
