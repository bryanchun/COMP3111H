package comp3111.webscraper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * SearchRecord stores a search that contains the keyword used for the search and the results of the search.
 *
 * It also provides a save and load function to read and write history from and into files.
 */
public class SearchRecord {
    private static final ObservableList<SearchRecord> allSearchRecords = FXCollections.observableArrayList();

    private final StringProperty keyword = new SimpleStringProperty();
    private final ObservableList<Item> products = FXCollections.observableArrayList();
    private final BooleanProperty hasSearchRefined = new SimpleBooleanProperty(false);

    /**
     * Saves all SearchRecords
     *
     * @param path The path where the save file is written
     */
    public static void save(String path) {
        // Todo implement save
    }

    /**
     * Load all SearchRecords
     *
     * @param path The path where the save file is loaded
     */
    public static void load(String path) {
        // Todo implement load
    }

    /**
     * Default Constructor
     */
    public SearchRecord() {}

    /**
     * Constructs a SearchRecord with supplied keyword. Then the constructed instance would initialize a search
     * immediately.
     *
     * @param keyword The search keyword
     */
    public SearchRecord(String keyword) {
        this.search(keyword);
    }

    /**
     * Returns the search keyword
     * @return The search keyword
     */
    public String getKeyword() {
        return keyword.get();
    }

    /**
     * Manually sets the search keyword, for when the search is refined.
     * @param keyword The new search keyword
     */
    public void setKeyword(String keyword) {
        this.keyword.set(keyword);
    }

    /**
     * Initialize a search on the target websites.
     * @param keyword The search keyword
     */
    public void search(String keyword) {
        this.keyword.set(keyword);
        products.setAll((new WebScraper()).scrape(keyword));
    }

    /**
     * Refines the search in this SearchRecord by using a new keyword as a filter.
     * @param refineKeyword The additional search keyword to refine the search results.
     */
    public void refine(String refineKeyword) {
        hasSearchRefined.set(false);
        // Todo implement refine
    }

    /**
     * Returns whether the SearchRecord has already refined the search results.
     * @return A boolean value indicating if the search results is refined.
     */
    public BooleanProperty getHasSearchRefined() {
        return hasSearchRefined;
    }

    /**
     * Returns an ObservableList of the Items search results.
     * @return Observable products list
     */
    public ObservableList<Item> getProducts() {
        return products;
    }

}
