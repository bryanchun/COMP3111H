package comp3111.webscraper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchRecord stores a search that contains the keyword used for the search and the results of the search.
 * It also provides a save and load function to read and write history from and into files.
 */
public class SearchRecord {
    private static final int MAX_HISTORY_SIZE = 5;
    private static final ObservableList<SearchRecord> allSearchRecords = FXCollections.observableArrayList();
    private static final ObjectProperty<SearchRecord> latest = new SimpleObjectProperty<>();
    private static final WebScraper webScraper = new WebScraper();

    static {
        //Adds a static listener when the class is first used
        allSearchRecords.addListener((ListChangeListener<SearchRecord>) listener -> {
            if (listener.next() && listener.wasAdded() && allSearchRecords.size() > MAX_HISTORY_SIZE) {
                //Remove old history
                allSearchRecords.remove(MAX_HISTORY_SIZE - 1, allSearchRecords.size());
            }
            latest.set(allSearchRecords.get(0));
        });
    }

    private String keyword = "";
    private List<Item> products = new ArrayList<>();
    private boolean hasSearchRefined = false;

    /**
     * Default Constructor
     */
    SearchRecord() {
    }

    /**
     * Constructs a SearchRecord with supplied keyword. Then the constructed instance would initialize a search
     * immediately.
     *
     * @param keyword The search keyword
     */
    private SearchRecord(String keyword) {
        this.search(keyword);
    }

    /**
     * Constructs a SearchRecord with supplied refine keyword. The new instance would then have the refined records.
     *
     * @param record  The record the refine search is based on
     * @param keyword The refine keyword
     */
    private SearchRecord(SearchRecord record, String keyword) {
        this.keyword = keyword;
        this.hasSearchRefined = true;
        this.products = record.products.stream().filter(item -> item.getTitle().contains(keyword)).collect(Collectors.toList());
    }

    /**
     * Adds a new search
     *
     * @param keyword A search keyword
     */
    static void newSearch(String keyword) {
        pushHistory(new SearchRecord(keyword));
    }

    /**
     * Pushes a new SearchRecord to allSearchRecords so it would become the latest SearchRecord
     *
     * @param searchRecord A new SearchRecord
     */
    private static void pushHistory(SearchRecord searchRecord) {
        allSearchRecords.add(0, searchRecord);
    }

    /**
     * Initialize a search on the target websites.
     *
     * @param keyword The search keyword
     */
    private void search(String keyword) {
        this.keyword = keyword;
        products = webScraper.scrape(keyword);
    }

    /**
     * Adds a new refine search
     *
     * @param keyword Refine keyword
     */
    static void newRefineSearch(String keyword) {
        SearchRecord latest = SearchRecord.getLatestProperty().get();
        pushHistory(new SearchRecord(latest, keyword));
    }

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
     * Retrieves the latest added SearchRecord in history
     *
     * @return The latest SearchRecord
     */
    static ObjectProperty<SearchRecord> getLatestProperty() {
        return latest;
    }

    /**
     * Returns the search keyword
     *
     * @return The search keyword
     */
    String getKeyword() {
        return keyword;
    }

    /**
     * Manually sets the search keyword, for when the search is refined.
     *
     * @param keyword The new search keyword
     */
    void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    /**
     * Returns whether the SearchRecord has already refined the search results.
     *
     * @return A boolean value indicating if the search results is refined.
     */
    boolean getHasSearchRefined() {
        return hasSearchRefined;
    }

    /**
     * Returns an ObservableList of the Items search results.
     *
     * @return Observable products list
     */
    List<Item> getProducts() {
        return products;
    }

}
