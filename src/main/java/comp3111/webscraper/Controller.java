/**
 * 
 */
package comp3111.webscraper;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Date;
import java.util.List;


/**
 * 
 * @author kevinw
 * <p>
 * <p>
 * Controller class that manage GUI interaction. Please see document about JavaFX for details.
 * 
 */
public class Controller {

    @FXML
    private Label labelCount;

    @FXML
    private Label labelPrice;

    @FXML
    private Hyperlink labelMin;

    @FXML
    private Hyperlink labelLatest;

    @FXML
    private TextField textFieldKeyword;

    @FXML
    private TextArea textAreaConsole;

    @FXML
    private Button buttonRefine;

    private ObservableList<Item> currentProducts = FXCollections.observableArrayList();

    @Deprecated
    private List<Item> result;

    @Deprecated
    private List<Item> refineResult;

    // TableTab nodes
    @FXML
    public VBox root;
    @FXML
    public TableView<Item> table;
    @FXML
    public TableColumn<Item, String> titleColumn;
    @FXML
    public TableColumn<Item, Double> priceColumn;
    @FXML
    public TableColumn<Item, String> urlColumn;
    @FXML
    public TableColumn<Item, Date> postedDateColumn;
    @FXML
    public TableColumn<Item, String> portalColumn;

    /**
     * StringProperty storing text that is shown in the console TextArea
     */
    private StringProperty consoleText = new SimpleStringProperty();
    private BooleanProperty isRefineDisabled = new SimpleBooleanProperty(true);

    /**
     * Default controller
     */
    public Controller() {
    }

    /**
     * Default initializer. It is empty.
     */
    @FXML
    private void initialize() {
        textAreaConsole.textProperty().bind(consoleText);
        buttonRefine.disableProperty().bind(isRefineDisabled);

        // Listener is triggered when SearchRecord::latest is updated
        SearchRecord.getLatestProperty().addListener((o, oldValue, newValue) -> {
            // Updates consoleText
            StringBuilder consoleOutput = new StringBuilder();
            for (Item item : newValue.getProducts()) {
                consoleOutput.append(item.getTitle()).append("\t").append(item.getPrice()).append("\t").append(item.getUrl()).append("\n");
            }
            consoleText.setValue(consoleOutput.toString());

            // Updates current products
            currentProducts.setAll(newValue.getProducts());

            // Updates isRefineDisabled
            isRefineDisabled.setValue(newValue.getHasSearchRefined() || currentProducts.size() == 0);
        });

        // Initialize Table factories and listeners
        new TableTab(this).initTable(currentProducts);
    }

    /**
     * Called when the search button is pressed.
     */
    @FXML
    private void actionSearch() {
        System.out.println("actionSearch: " + textFieldKeyword.getText());
        SearchRecord.newSearch(textFieldKeyword.getText());
    }

    /**
     * Called when the refine search button is pressed.
     */
    @FXML
    private void actionRefineSearch() {
        String query = textFieldKeyword.getText();
        System.out.println("actionRefineSearch: " + query);
        SearchRecord.newRefineSearch(query);
    }

    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
        System.out.println("actionNew");
    }
}

