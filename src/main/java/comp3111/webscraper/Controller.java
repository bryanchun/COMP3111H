/**
 *
 */
package comp3111.webscraper;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author kevinw
 * <p>
 * <p>
 * Controller class that manage GUI interaction. Please see document about JavaFX for details.
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

    // TableTab elements
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

    // DistributionTab elements
    @FXML
    public BarChart<String, Integer> barChartHistogram;

    // TrendTab elements
    @FXML
    public ComboBox<SearchRecord> trendCombo;
    @FXML
    public AreaChart<String, Double> trendAreaChart;

    public ObjectProperty<SearchRecord> trendSelected = new SimpleObjectProperty<>();

    /**
     * StringProperty storing text that is shown in the console TextArea
     */
    public StringProperty consoleText = new SimpleStringProperty();
    private BooleanProperty isRefineDisabled = new SimpleBooleanProperty(true);
    private Boolean loadingFile = false;
    private String loadingFilename = "";


    /**
     * Generates a formatted string describing the items
     * @param products An Item list of products
     * @return Formatted string describing the items
     */
    public static String generateItemsConsoleOutput(List<Item> products) {
        return products.stream().map(item -> item.getTitle() + "\t" + item.getPrice() + "\t" + item.getUrl() + "\n").collect(Collectors.joining());
    }

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
            if (newValue != null) {
                // Updates consoleText
                consoleText.setValue((loadingFile ? "--Data Loading from " + loadingFilename + "--\n" : "") + generateItemsConsoleOutput(newValue.getProducts()));
                loadingFile = false;
                // Updates current products
                currentProducts.setAll(newValue.getProducts());

                // Updates isRefineDisabled
                isRefineDisabled.setValue(newValue.getHasSearchRefined() || currentProducts.size() == 0);
            }
        });

        // Initialize Table factories and listeners
        new TableTab(this).initTable(currentProducts);

        // Initialize Distribution
        new DistributionTab(this).initDistribution(currentProducts);

        // Initializes TrendTab
        new TrendTab(this);
    }

    /**
     * Called when the search button is pressed.
     */
    @FXML
    private void actionSearch() {
        String query = textFieldKeyword.getText().trim();
        if (!query.isEmpty()) {
            System.out.println("actionSearch: " + query);
            SearchRecord.newSearch(query);
        }
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

    @FXML
    private void actionLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter("Data", "*.dat");
        fileChooser.getExtensionFilters().add(fileExtensions);
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        loadingFile = true;
        loadingFilename = file.getPath();
        try {
            SearchRecord.load(file.getPath());
        } catch (Exception e) {
            loadingFile = false;
            e.printStackTrace();
        }
    }

    /**
     * Called when save button is pressed, prompts a file chooser and saves the file.
     */
    @FXML
    private void actionSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Resource File");
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter("Data", "*.dat");
        fileChooser.setInitialFileName("record.dat");
        fileChooser.getExtensionFilters().add(fileExtensions);
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        try {
            SearchRecord.save(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
        System.out.println("actionNew");
    }
}

