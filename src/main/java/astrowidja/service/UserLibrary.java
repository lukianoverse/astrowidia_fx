package astrowidja.service;

import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.WorkList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserLibrary {


    private final List<WorkList> lists = new ArrayList<>();


    public List<WorkList> getAllLists() {
        return lists;
    }

    public void indexWork(AstroWork work) {
        
    }

    public void registerList(WorkList newList) {
        lists.add(newList);
    }
}
