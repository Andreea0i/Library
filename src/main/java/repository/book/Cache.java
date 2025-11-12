package repository.book;

//generic classes

import java.util.List;

public class Cache<T>{
    public List<T> storage;
    public List<T> load(){
        return storage;
    }

    public void save(List<T> list){
        this.storage = storage;
    }

    public boolean hasResult() {
        return storage != null;
    }

    public void invalidateCache() {
        storage = null;
    }
}
