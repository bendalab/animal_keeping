package animalkeeping.ui;

/**
 * Created by huben on 11.01.17.
 */
public interface TableFactory<T> {
    T factory(String identifier);
}
