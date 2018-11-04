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
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.HostServices;

import java.util.Date;
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

    @FXML
    private VBox root;
    @FXML
    private TableView<Item> table;
    @FXML
    private TableColumn<Item, String> titleColumn;
    @FXML
    private TableColumn<Item, Double> priceColumn;
    @FXML
    private TableColumn<Item, String> urlColumn;
    @FXML
    private TableColumn<Item, Date> postedDateColumn;
    @FXML
    private TableColumn<Item, String> portalColumn;

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
        initTable();
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

    private <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> newURLCellFactory() {
        return param -> new TableCell<S,T>(){
            {
                this.setOnMouseClicked(event -> openURL(this.getText()));
            }

            @Override
            protected void updateItem(T t, boolean bln) {
                super.updateItem(t, bln);
                setText(t!= null ? t.toString() : "");
            }
        };
    };

    /**
     * Called when Controller initialize, instead of when Table Tab is clicked.
     */
    private void initTable() {
        // Initialize mapping between table column text and Item attributes for setting list of Items to table
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        postedDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        portalColumn.setCellValueFactory(new PropertyValueFactory<>("portal"));

        // Pop up a new windows/browser showing the item when the URL is clicked.
        urlColumn.setCellFactory(newURLCellFactory());

        // Sort the result in ascending order on user clicking each column, and sort in descending order when user click again.
        // Note that after pressing twice, the third click does nothing and resets so that the next click gives ascending sort.
        this.table.getColumns().forEach(
            column -> column.setSortable(true)
        );

        // Update table items by and on the change of data in observed list
        currentProducts.addListener((ListChangeListener<Item>) change -> {
            System.out.println("currentProducts updated");
            //System.out.println("Changed on " + change);
            this.table.setItems(currentProducts);
        });

        // Make cells not editable
        this.table.setEditable(false);
    }

    /**
     * Helper function for opening a URL from a browser
     * @param url
     */
    private void openURL(String url) {
        Stage stage = (Stage) root.getScene().getWindow();
        HostServices hostServices = (HostServices) stage.getProperties().get("hostServices");
        hostServices.showDocument(url);
    }
}

