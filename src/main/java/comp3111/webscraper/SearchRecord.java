package comp3111.webscraper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SearchRecord {
    private String keyword;
    private StringProperty refineKeyword = new SimpleStringProperty();
    private ObservableList<Item> Items = FXCollections.observableArrayList();

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getRefineKeyword() {
        return refineKeyword.get();
    }

    public void setRefineKeyword(String refineKeyword) {
        this.refineKeyword.setValue(refineKeyword);
    }

    public StringProperty getRefineKeywordProperty() {
        return this.refineKeyword;
    }

    public ObservableList<Item> getItems() {
        return Items;
    }
}
