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

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author kevinw
 *
 *
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

            // Updates isRefineDisabled
            isRefineDisabled.setValue(newValue.getHasSearchRefined());

            // Updates current products
            currentProducts.setAll(newValue.getProducts());
        });
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
        StringBuilder output = new StringBuilder();

        //TODO(mcreng): Update all tabs after refining search.
    }

    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
    	System.out.println("actionNew");
    }
}

