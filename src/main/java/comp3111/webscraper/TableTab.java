package comp3111.webscraper;

import javafx.application.HostServices;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * TableTab
 *
 * @author Bryan CHUN
 *
 * Fill in a table of searched products with their title, price, url, posted date and portal. Clicking on the URL will open the product link in the browser. Clicking on the column lebel will sort the products.
 */

class TableTab {

    private Controller controller;

    TableTab(Controller controller) {
        this.controller = controller;
    }

    /**
     * Called when Controller initialize, instead of when Table Tab is clicked.
     */
    void initTable(ObservableList<Item> currentProducts) {
        // Initialize mapping between table column text and Item attributes for setting list of Items to table
        controller.titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        controller.priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        controller.urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        controller.postedDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        controller.portalColumn.setCellValueFactory(new PropertyValueFactory<>("portal"));

        // Pop up a new windows/browser showing the item when the URL is clicked.
        controller.urlColumn.setCellFactory(newURLCellFactory());

        // Sort the result in ascending order on user clicking each column, and sort in descending order when user click again.
        // Note that after pressing twice, the third click does nothing and resets so that the next click gives ascending sort.
        controller.table.getColumns().forEach(
            column -> column.setSortable(true)
        );

        // Update table items by and on the change of data in observed list
        currentProducts.addListener((ListChangeListener<Item>) change -> {
            System.out.println("currentProducts updated");
            //System.out.println("Changed on " + change);
            controller.table.setItems(currentProducts);
        });

        // Make cells not editable
        controller.table.setEditable(false);
    }

    /**
     * Helper function for opening a URL from a browser
     * @param url
     */
    private void openURL(String url) {
        Stage stage = (Stage) controller.root.getScene().getWindow();
        HostServices hostServices = (HostServices) stage.getProperties().get("hostServices");
        hostServices.showDocument(url);
    }

    /**
     * Returns a callback function for URL onClick handling
     */
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
    }
}
