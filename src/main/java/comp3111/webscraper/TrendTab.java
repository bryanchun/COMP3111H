package comp3111.webscraper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Displays an AreaChart and a combo box, and updates the chart when a searched keyword is selected in the combo box, the last 7 days of the average prices of the product listings created on that day, and updates the console when the user double clicks on a point on the chart, to show the products on that day.
 *
 * @author CHEUNG, Daniel
 */
class TrendTab {
    private static final int MAX_DAYS = 7;
    private static final String DATUM_ACTIVE_CLASS = "active-datum";
    private static final DateTimeFormatter LABEL_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private Controller controller;
    private Series<String, Double> series = new Series<>();
    private ObservableList<Data<String, Double>> data = series.getData();
    private List<List<Item>> productsInDaysUntil = new ArrayList<>();

    /**
     * Constructor that initializes the trend tab
     * @param controller Controller in use
     */
    TrendTab(Controller controller) {
        this.controller = controller;
        ObservableList<Series<String, Double>> seriesList = FXCollections.observableArrayList();
        seriesList.add(series);

        controller.trendSelected.addListener((o, oldValue, newValue) -> {
            final LocalDate today = LocalDate.now();
            productsInDaysUntil.clear();
            data.clear();

            IntStream.range(0, MAX_DAYS).forEach(i -> productsInDaysUntil.add(new ArrayList<>()));

            newValue.getProducts().forEach(item -> {
                int i = (int) DAYS.between(localDateFromDate(item.getCreatedAt()), today);
                if (i < MAX_DAYS && i >= 0) productsInDaysUntil.get(i).add(item);
            });

            IntStream.range(0, productsInDaysUntil.size()).map(i -> MAX_DAYS - i - 1).forEachOrdered(i -> productsInDaysUntil
                    .get(i)
                    .stream()
                    .filter(item -> item.getPrice() > 0.0)
                    .mapToDouble(Item::getPrice)
                    .average()
                    .ifPresent(avg -> {
                        Data<String, Double> datum = new Data<>(today.minusDays(i).format(LABEL_DATE_FORMATTER), avg);
                        data.add(datum);
                        datum.getNode().setOnMouseClicked(event -> {
                            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
                                selectDate(i, datum.getNode());
                        });
                    })
            );

            series.setName("The average selling price of the " + newValue.getKeyword());
        });

        controller.trendAreaChart.setData(seriesList);
        controller.trendCombo.setConverter(new StringConverter<SearchRecord>() {
            @Override
            public String toString(SearchRecord object) {
                return object.getKeyword();
            }

            @Override
            public SearchRecord fromString(String string) {
                return null;
            }
        });

        controller.trendCombo.setItems(SearchRecord.getAllSearchRecords());
        controller.trendCombo.valueProperty().addListener((o, oldValue, newValue) -> controller.trendSelected.set(newValue));
    }

    /**
     * Converts a java.util.Date into a LocalDate class
     * @param date a Date
     * @return a LocalDate
     */
    private static LocalDate localDateFromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Called when a point is selected This will update console to show listings created on the date described by i-th product array in productsInDaysUntil.
     * @param i i-th product array in productsInDaysUntil
     * @param node corresponding data point node that represents the average price of the day
     */
    private void selectDate(int i, Node node) {
        controller.consoleText.set(Controller.generateItemsConsoleOutput(productsInDaysUntil.get(i)));
        data.forEach(datum -> datum.getNode().getStyleClass().removeIf(className -> className.equals(DATUM_ACTIVE_CLASS)));
        node.getStyleClass().add(DATUM_ACTIVE_CLASS);
    }
}
