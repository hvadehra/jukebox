package org.melocine.services;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/19/14
 * Time: 3:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageService {

    private final URLReader urlReader;

    public ImageService(URLReader urlReader) {
        this.urlReader = urlReader;
    }

    public List<String> getImagesForArtist(String artist) {
        ArrayList<String> imageList = Lists.newArrayList();

        String url = "http://www.last.fm/music/" + artist.replace(" ", "+") + "/+images";
        String content = urlReader.getUrl(url);
        try{
            int start = content.indexOf("<ul id=\"pictures\"");
            int end = content.indexOf("</ul", start);
            String images = content.substring(start, end);
            for (String imgTag : images.split("</li")) {
                System.err.println("Image tag: " + imgTag);
                int beginIndex = 5 + imgTag.indexOf("src=");
                int endIndex = -2 + imgTag.indexOf("/>", beginIndex);
                try{
                    String imgUrl = imgTag.substring(beginIndex, endIndex);
//                    String fileType = imgUrl.substring(imgUrl.length()-4);
//                    imgUrl = imgUrl.replace("/126s/", "/_/");
//                    imgUrl = imgUrl.replace(fileType, "/" + sanitizeForImageUrl(artist) + fileType);
                    imageList.add(imgUrl);
                    System.err.println("Image url: " + imgUrl);
                }
                catch (Exception e){
                    System.err.println("ignoring image tag: " + imgTag);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Could not get artist images: " + Joiner.on("\n").join(e.getStackTrace()));
        }
        return imageList;
    }

    private String sanitizeForImageUrl(String artist) {
        return artist.replace(" ", "+").replace("^[a-zA-Z0-9]", "");
    }
}
