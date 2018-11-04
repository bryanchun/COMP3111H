package comp3111.webscraper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class RefineSearchTest extends ApplicationTest {
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
}