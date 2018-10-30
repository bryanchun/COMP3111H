/**
 * 
 */
package comp3111.webscraper;


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
    
    private WebScraper scraper;

    private List<Item> result;
    private List<Item> refineResult;

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
    }

    /**
     * Called when the new button is pressed. Very dummy action - print something in the command prompt.
     */
    @FXML
    private void actionNew() {
    	System.out.println("actionNew");
    }
}

