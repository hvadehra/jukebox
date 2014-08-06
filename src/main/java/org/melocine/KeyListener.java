package org.melocine;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/7/14
 * Time: 2:39 AM
 * To change this template use File | Settings | File Templates.
 */

public class KeyListener{

    public KeyListener(final Player player){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int tmp = System.in.read();
                        switch (tmp){
                            case 3:
                                System.exit(1);
                                break;
                            case 110:
                                player.next();
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }).start();
    }
}