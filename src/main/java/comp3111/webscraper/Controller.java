/**
 * 
 */
package comp3111.webscraper;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.HostServices;

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
    
    private WebScraper scraper;

    private List<Item> result;
    private List<Item> refineResult;
    private ObservableList<Item> obResult;

    @FXML
    private VBox root;
    @FXML
    private TableView<Item> table;

    /**
     * Default controller
     */
    public Controller() {
    	scraper = new WebScraper();
    }

    /**
     * Default initializer. It is empty.
     */
    @FXML
    private void initialize() {
        // Initialize and maintain a observable list
        this.obResult = FXCollections.observableArrayList();

        // Initialize Table factories and listeners
        initTable();
    }

     /**
     * Called when the search button is pressed.
     */
    @FXML
    private void actionSearch() {
    	System.out.println("actionSearch: " + textFieldKeyword.getText());
    	this.result = scraper.scrape(textFieldKeyword.getText());
    	StringBuilder output = new StringBuilder();
    	for (Item item : this.result) {
    		output.append(item.getTitle()).append("\t").append(item.getPrice()).append("\t").append(item.getUrl()).append("\n");
    	}
    	textAreaConsole.setText(output.toString());
    	buttonRefine.setDisable(false);

    	// Update obResult
        this.obResult.setAll(this.result);
    }

    /**
     * Called when the refine search button is pressed.
     */
    @FXML
    private void actionRefineSearch() {
        String query = textFieldKeyword.getText();
        System.out.println("actionRefineSearch: " + query);
        StringBuilder output = new StringBuilder();
        this.refineResult = new ArrayList<>();
        for (Item item : this.result) {
            if (item.getTitle().contains(query)) {
                this.refineResult.add(item);
                output.append(item.getTitle()).append("\t").append(item.getPrice()).append("\t").append(item.getUrl()).append("\n");
            }
        }

        textAreaConsole.setText(output.toString());
        //TODO(mcreng): Update all tabs after refining search.
        buttonRefine.setDisable(true);

        // Update obResult
        this.obResult.setAll(this.refineResult);
    }

    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew()
    {
    	System.out.println("actionNew");
    }

    /**
     * Called when Controller initialize, instead of when Table Tab is clicked.
     */
    private void initTable() {
        // Initialize mapping between table column text and Item attributes for setting list of Items to table
        ObservableList<TableColumn<Item, ?>> columns = this.table.getColumns();
        for (TableColumn column : columns) {
            switch (column.getText()) {
                case "Title":
                    // Create ValueFactory of Item attribute to map to respective column
                    column.setCellValueFactory(new PropertyValueFactory<TableRow, Item>("title"));
                    break;
                case "Price":
                    column.setCellValueFactory(new PropertyValueFactory("price"));
                    break;
                case "URL":
                    column.setCellValueFactory(new PropertyValueFactory("url"));

                    // Pop up a new windows/browser showing the item when the URL is clicked.
                    column.setCellFactory(new Callback<TableColumn, TableCell>() {
                        @Override
                        public TableCell call(TableColumn param) {

                            // Setup the cell that is will be handled
                            final TableCell cell = new TableCell() {
                                @Override
                                protected void updateItem(Object t, boolean bln) {
                                    super.updateItem(t, bln);
                                    if (t != null) {
                                        setText(t.toString());
                                    }
                                }
                            };

                            // Called when clicked a URL cell
                            cell.setOnMouseClicked(event ->
                                openURL(cell.getText())
                            );
                            return cell;
                        }
                    });
                    break;
                case "Posted Date":
                    column.setCellValueFactory(new PropertyValueFactory("createdAt"));
                    break;
                // TODO(bryanchun): Fill in Portal column
//                case "Portal":
//                    column.setCellFactory(new PropertyValueFactory("portal"));
                default:
                    break;
            }

            // Sort the result in ascending order on user clicking each column, and sort in descending order when user click again.
            // Note that after pressing twice, the third click does nothing and resets so that the next click gives ascending sort.
            column.setSortable(true);
        }

        // Update table items by and on the change of data in observed list
        this.obResult.addListener((ListChangeListener<Item>) change -> {
            System.out.println("obresults changed");
            //System.out.println("Changed on " + change);
            this.table.setItems(this.obResult);
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

