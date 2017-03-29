package com.starlight.mobile.android.lib.album;

import java.io.File;
import java.util.Comparator;

/**
 * Created by Raleigh on 15/10/12.
 */
public class AlbumFileComparator implements Comparator<File> {
    @Override
    public int compare(File lhs, File rhs) {
        int result=0;
        if(lhs.lastModified() > rhs.lastModified()){
            result=-1;
        }else if(lhs.lastModified() < rhs.lastModified()){
            result=1;
        }
        return result;
    }
}
