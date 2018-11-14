package comp3111.webscraper;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class ControllerTest extends ApplicationTest {
    private Controller controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ui.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();
        stage.setAlwaysOnTop(true);
        controller = loader.getController();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void testGenerateItemsConsoleOutput() {
        Date today = new Date();
        List<Item> products = Arrays.asList(
                new Item(
                        "1",
                        1.0,
                        "http://example.com/",
                        today,
                        "Test"
                ),
                new Item(
                        "Free",
                        0.0,
                        "http://example.com/",
                        today,
                        "Test"
                )
        );
        assertEquals("1\t1.0\thttp://example.com/\nFree\t0.0\thttp://example.com/\n", Controller.generateItemsConsoleOutput(products));
    }

    @Test
    public void testDummy() throws Exception {
        Method actionNew = Controller.class.getDeclaredMethod("actionNew");
        actionNew.setAccessible(true);
        actionNew.invoke(controller);
    }

    @Test
    public void testIllegalSearch() throws Exception {
        SearchRecord.getAllSearchRecords().clear();

        //Blank searches are forbidden
        clickOn("#textFieldKeyword");
        type(KeyCode.SPACE);
        clickOn("#buttonSearch");
        assertEquals(0, SearchRecord.getAllSearchRecords().size());
    }

    @Test
    public void testRefineSearch() throws Exception {
        // Create a new search record
        SearchRecord searchRecord = new SearchRecord();

        // Using reflection to access the private attributes
        Field keyword = SearchRecord.class.getDeclaredField("keyword");
        keyword.setAccessible(true);
        Field hasSearchRefined = SearchRecord.class.getDeclaredField("hasSearchRefined");
        hasSearchRefined.setAccessible(true);
        Field products = SearchRecord.class.getDeclaredField("products");
        products.setAccessible(true);

        // Set keyword and hasSearchRecord
        keyword.set(searchRecord, "iPhone");
        hasSearchRefined.set(searchRecord, false);

        // Construct new Item to feed into products
        Item item = new Item();
        item.setTitle("iPhone 6 16GB");
        products.set(searchRecord, new ArrayList<Item>() {{
            add(item);
        }});

        // Push the created searchRecord
        Method pushHistory = SearchRecord.class.getDeclaredMethod("pushHistory", SearchRecord.class);
        pushHistory.setAccessible(true);
        pushHistory.invoke(null, searchRecord);

        // Assume "iPhone 6" is set for refine query
        TextField textFieldKeyword = lookup("#textFieldKeyword").query();
        textFieldKeyword.setText("iPhone 6");

        // Execute actionRefineSearch
        Method method = Controller.class.getDeclaredMethod("actionRefineSearch");
        method.setAccessible(true);
        method.invoke(controller);

        // The Item should remain
        assertEquals(SearchRecord.getLatestProperty().get().getProducts(), searchRecord.getProducts());

        // Assume "iPhone 7" is set for refine query
        textFieldKeyword.setText("iPhone 7");

        // Execute actionRefineSearch
        method.invoke(controller);

        // The Item should not remain
        assertTrue(SearchRecord.getLatestProperty().get().getProducts().isEmpty());
    }

    @Test
    public void testInitTable() throws Exception {
        // Get corresponding fields and elements through reflection
        Field currentProducts = Controller.class.getDeclaredField("currentProducts");
        currentProducts.setAccessible(true);
        TableView<Item> table = lookup("#table").query();
        TabPane tabpane = lookup("#tabpane").query();
        tabpane.getSelectionModel().select(2);

        // Check obResult has been initialized
        assertNotNull(currentProducts);

        // Setup search
        Method actionSearch = Controller.class.getDeclaredMethod("actionSearch");
        actionSearch.setAccessible(true);
        TextField textFieldKeyword = lookup("#textFieldKeyword").query();

        // Check table is filled in by checking the first row of all columns
        // "Denon" would return more than one search results by previous experimentation
        textFieldKeyword.setText("Denon");
        actionSearch.invoke(controller);
        String title1 = (String) table.getColumns().get(0).getCellObservableValue(0).getValue();
        assertNotNull(table.getColumns().get(0).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(1).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(2).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(3).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(4).getCellObservableValue(0).getValue());

        // Check table is refreshed on another search
        // "Canon would return more than one search results by previous experimentation
        textFieldKeyword.setText("Canon");
        actionSearch.invoke(controller);
        String title2 = (String) table.getColumns().get(0).getCellObservableValue(0).getValue();
        assertNotNull(table.getColumns().get(0).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(1).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(2).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(3).getCellObservableValue(0).getValue());
        assertNotNull(table.getColumns().get(4).getCellObservableValue(0).getValue());

        // Check that the two searches are different
        // By previous experimentation, the titles of first items of these two searches shall not match
        assertNotEquals(title1, title2);

        // Check cells in table are not editable
        assertFalse(table.isEditable());

        //TODO(bryannchun): click on TableCell to test openURL
        //TODO(bryannchun): click on TableColumn label to test sorting

    }

    @Test
    public void testSaveLoadRecord() throws Exception {
        SearchRecord.getAllSearchRecords().clear();
        // Create a new search record
        SearchRecord searchRecord = new SearchRecord();

        // Using reflection to access the private attributes
        Field keyword = SearchRecord.class.getDeclaredField("keyword");
        keyword.setAccessible(true);
        Field hasSearchRefined = SearchRecord.class.getDeclaredField("hasSearchRefined");
        hasSearchRefined.setAccessible(true);
        Field products = SearchRecord.class.getDeclaredField("products");
        products.setAccessible(true);

        // Set keyword and hasSearchRecord
        keyword.set(searchRecord, "iPhone");
        hasSearchRefined.set(searchRecord, false);

        // Construct new Item to feed into products
        Item item = new Item();
        item.setTitle("iPhone 6 16GB");
        products.set(searchRecord, Collections.singletonList(item));

        // Push the created searchRecord
        Method pushHistory = SearchRecord.class.getDeclaredMethod("pushHistory", SearchRecord.class);
        pushHistory.setAccessible(true);
        pushHistory.invoke(null, searchRecord);

        // Save the record
        String pwd = Paths.get(".").toAbsolutePath().normalize().toString();
        String filename = pwd + "/1.dat";
        SearchRecord.save(filename);

        // Create a new search record
        SearchRecord searchRecord2 = new SearchRecord();
        Item item2 = new Item();
        item2.setTitle("iPhone 7 32GB");
        products.set(searchRecord2, Collections.singletonList(item2));
        pushHistory.invoke(null, searchRecord2);

        // Check if we have two records
        Field allSearchRecords = SearchRecord.class.getDeclaredField("allSearchRecords");
        allSearchRecords.setAccessible(true);
        assertEquals(((List<?>) allSearchRecords.get(null)).size(), 2);

        // Load the saved record
        SearchRecord.load(filename);
        // Check if we have one records
        assertEquals(((List<?>) allSearchRecords.get(null)).size(), 1);
        // Check if it is the first item
        assertEquals(SearchRecord.getLatestProperty().get().getProducts().get(0).getTitle(), item.getTitle());

        // Delete the generated file
        File file = new File(filename);
        file.delete();
    }

    @Test
    public void testInitDistribution() throws Exception {

        final String BAR_DEFAULT_CLASS = "default-bar";
        final String BAR_ACTIVE_CLASS = "active-bar";

        SearchRecord.getAllSearchRecords().clear();

        List<Item> products2 = Arrays.asList(
                new Item("", 100.0,"",new Date(),"Test"),
                new Item("", 40.0,"",new Date(),"Test"),
                new Item("", 80.0,"",new Date(),"Test"),
                new Item("", 0.0,"",new Date(),"Test"),
                new Item("", 15.0,"",new Date(),"Test"),
                new Item("", 20.0,"",new Date(),"Test"),
                new Item("", 30.0,"",new Date(),"Test")
        );
        Map<String, Integer> labelledFrequencies = new HashMap<String, Integer>() {
            {
                put("0.0-10.0", 1);
                put("10.0-20.0", 2);
                put("20.0-30.0", 1);
                put("30.0-40.0", 1);
                put("40.0-50.0", 0);
                put("50.0-60.0", 0);
                put("60.0-70.0", 0);
                put("70.0-80.0", 1);
                put("80.0-90.0", 0);
                put("90.0-100.0", 1);
            }
        };

        TabPane tabpane = lookup("#tabpane").query();
        tabpane.getSelectionModel().select(3);
        BarChart barChart = lookup("#barChartHistogram").query();
        SearchRecord.pushHistory(new SearchRecord("test", products2, false));


        Platform.runLater(() -> {
            XYChart.Series<String, Integer> series = (XYChart.Series<String, Integer>) barChart.getData().get(0);

            // Check X, Y-axis labels
            assertEquals(barChart.getXAxis().getLabel(), "Price");
            assertEquals(barChart.getYAxis().getLabel(), "Frequency");


            // Check Legend
            assertEquals(series.getName(),
                    "The selling price of " + SearchRecord.getLatestProperty().get().getKeyword());

            // Check correctly filled in Distribution
            for (XYChart.Data<String, Integer> bin : series.getData()) {
                // Bug: bin.getYValue return Double instead of Integer
                assertEquals(Double.valueOf(labelledFrequencies.get(bin.getXValue())),
                        bin.getYValue());
            }

            // Check bar colour changes on double click
            for (Integer binIndex : Arrays.asList(0, 2, 5)) {
                Node binNode1 = series.getData().get(binIndex).getNode();
                DistributionTab.setBarColor(series.getData().get(binIndex), series);
                System.out.println(binNode1.getStyleClass());
                assertTrue(binNode1.getStyleClass().contains(BAR_ACTIVE_CLASS));;
                for (XYChart.Data otherBin : series.getData()) {
                    Node otherBinNode = otherBin.getNode();
                    if (otherBinNode != binNode1) {
                        assertTrue(otherBinNode.getStyleClass().contains(BAR_DEFAULT_CLASS));
                        assertFalse(otherBinNode.getStyleClass().contains(BAR_ACTIVE_CLASS));
                    }
                }
            }
        });

    }

    @Test
    public void testTrendTab() throws Exception {
        //Building props
        final LocalDate today = LocalDate.now();

        SearchRecord.getAllSearchRecords().clear();

        List<Item> products2 = Arrays.asList(
                new Item(
                        "1",
                        1.0,
                        "http://example.com/",
                        new Date(java.sql.Date.valueOf(today).getTime()),
                        "Test"
                ),
                new Item(
                        "Free",
                        0.0,
                        "http://example.com/",
                        new Date(java.sql.Date.valueOf(today).getTime()),
                        "Test"
                ),
                new Item(
                        "2",
                        2.0,
                        "http://example.com/",
                        new Date(java.sql.Date.valueOf(today.minusDays(1)).getTime()),
                        "Test"
                ),
                new Item(
                        "3",
                        3.0,
                        "http://example.com/",
                        new Date(java.sql.Date.valueOf(today.minusDays(10)).getTime()),
                        "Test"
                ),
                new Item(
                        "4",
                        3.0,
                        "http://example.com/",
                        new Date(java.sql.Date.valueOf(today.plusDays(10)).getTime()),
                        "Test"
                )
        );

        List<Item> todayList = products2.subList(0, 2);

        SearchRecord.pushHistory(new SearchRecord("test1", products2, false));
        SearchRecord.pushHistory(new SearchRecord("test0", new ArrayList<>(), false));

        //Starting test
        clickOn((Node) lookup("#tabpane > .tab-header-area > .headers-region > .tab").nth(4).query());
        clickOn("#trendCombo");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        sleep(1000);

        Node datum = lookup("#trendAreaChart .chart-area-symbol").nth(1).query();

        rightClickOn(datum);
        doubleClickOn(datum);

        assertEquals(Controller.generateItemsConsoleOutput(todayList), controller.consoleText.get());
    }
}