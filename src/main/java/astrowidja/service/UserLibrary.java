package astrowidja.service;

import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.Album;

import java.util.ArrayList;
import java.util.List;

public class UserLibrary {


    private final List<Album> lists = new ArrayList<>();


    public void indexWork(AstroWork work) {
        
    }

    public void registerAlbum(Album newList) {
        lists.add(newList);
    }

    public List<Album> getAllAlbums() {
        return lists;

    }
}
