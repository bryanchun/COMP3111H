package comp3111.webscraper;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DistributionTab
 *
 * @author Chun Hiu Sang
 *
 * Fill in a histogram of prices with the current products after a search. According to the range of the prices, 10 equal intervals are generated and products falling into respective price ranges will be allocated there. Double clicking one bar will change its color to blue, while all other bars restore to orange.
 */

class DistributionTab {

    private Controller controller;
    private Double numOfBins = 10.0;

    private static final String BAR_DEFAULT_CLASS = "default-bar";
    private static final String BAR_ACTIVE_CLASS = "active-bar";


    DistributionTab(Controller controller) {
        this.controller = controller;
    }

    /**
     * Called when initialize Distribution
     *
     * @param currentProducts list of searched Items
     */
    void initDistribution(ObservableList<Item> currentProducts) {

        // Set gaps and axes
        controller.barChartHistogram.setCategoryGap(0);
        controller.barChartHistogram.setBarGap(0);
        controller.barChartHistogram.getXAxis().setLabel("Price");
        controller.barChartHistogram.getYAxis().setLabel("Frequency");

        // Update Distribution on every new search
        currentProducts.addListener((ListChangeListener<Item>) change -> {

            Platform.runLater(() -> {
                // Create new series container for data
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                // Set Legend by search keyword
                series.setName("The selling price of " + SearchRecord.getLatestProperty().get().getKeyword());

                // Transform currentProducts to prices
                List<Double> prices = currentProducts.stream().map(Item::getPrice).collect(Collectors.toList());
                HashMap<XYChart.Data<String, Integer>, List<Item>> binnedProducts = new HashMap<>();
                List<Pair<Double, Double>>  priceRanges = getPriceRanges(prices, numOfBins);
                System.out.println("getPriceRanges count: " + priceRanges.size());

                // Add bins to barChart and keep mapping between XYChart.Data and list of products for this range
                priceRanges.forEach(priceRange -> {
                    // Create bin for each price range
                    XYChart.Data<String, Integer> bin = new XYChart.Data<>(
                            getBinLabel(priceRange),
                            getFrequency(prices, priceRange));

                    // Apply bin label and frequency for each priceRange to series
                    series.getData().add(bin);

                    // Store a map between bins and the list of items whose price falls within this priceRange
                    binnedProducts.put(
                            bin,
                            getProductsInPriceRange(currentProducts, priceRange)
                    );
                });

                // Reset all series
                controller.barChartHistogram.getData().clear();
                // Apply the new series
                controller.barChartHistogram.getData().add(series);

                // Set Double Click listeners for all nodes
                series.getData().forEach(bin -> {
                    bin.getNode().getStyleClass().add(BAR_DEFAULT_CLASS);
                    bin.getNode().setOnMouseClicked(event -> {
                        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                            System.out.println("Double clicked");

                            controller.consoleText.setValue(Controller.generateItemsConsoleOutput(binnedProducts.get(bin)));
                            setBarColor(bin, series);
                        }
                    });
                });
            });
        });
    }

    /**
     * Extract the bins of price ranges
     *
     * @param prices list of prices of searched Items
     * @param numOfBins number of bins for the histogram
     * @return priceRanges list of pairs representing list of start and end prices for a bin
     */
    private List<Pair<Double, Double>> getPriceRanges(List<Double> prices, Double numOfBins) {
        List<Pair<Double, Double>> priceRanges = new ArrayList<>();

        // If prices has one element only, add [0, price] and return
        if (prices.size() == 1) {
            priceRanges.add(new Pair<>(0.0, prices.get(0)));
        }
        // If prices has more than one element, calculate price ranges
        else if (prices.size() > 1) {
            Double range = Collections.max(prices) - Collections.min(prices);
            Double binWidth = range / numOfBins;
            IntStream.range(0, 10).forEach(i -> {
                if (i == 9) {
                    priceRanges.add(new Pair<>(i * binWidth, Collections.max(prices)));
                } else {
                    priceRanges.add(new Pair<>(i * binWidth, (i+1) * binWidth));
                }
            });
        }
        // else prices is empty, return empty priceRanges
        return priceRanges;
    }

    /**
     * Convert the Pair with its key representing startPrice and value representing endPrice to String bin label
     *
     * @param priceRange pair of start and end prices for a bin
     * @return String of "startPrice-endPrice" bin label
     */
    private String getBinLabel(Pair<Double, Double> priceRange) {
        return String.format("%.1f", priceRange.getKey()) + "-" + String.format("%.1f", priceRange.getValue());
    }

    /**
     * Compute the frequency of products in prices that falls under priceRange
     * Format: [key, value] is a priceRange
     *
     * @param prices list of prices of searched Items
     * @param priceRange pair of start and end prices for a bin
     * @return frequency of prices falling into this price range
     */
    private Integer getFrequency(List<Double> prices, Pair<Double, Double> priceRange) {
        // Keep all 0-priced in first bin
        if (priceRange.getKey() == 0.0) {
            return prices.stream().filter(price -> priceRange.getKey() <= price && price <= priceRange.getValue()).toArray().length;
        }
        // Break boundary points by a right-inclusive rule
        else {
            return prices.stream().filter(price -> priceRange.getKey() < price && price <= priceRange.getValue()).toArray().length;
        }
    }

    /**
     * Compute list of Item that is within the priceRange specified
     *
     * @param currentProducts list of searched Items
     * @param priceRange pair of start and end prices for a bin
     * @return list of Items that fall into this price range
     */
    private List<Item> getProductsInPriceRange(List<Item> currentProducts, Pair<Double, Double> priceRange) {
        // Keep all 0-priced in first bin
        if (priceRange.getKey() == 0.0) {
            return currentProducts.stream().filter(item -> priceRange.getKey() <= item.getPrice() && item.getPrice() <= priceRange.getValue()).collect(Collectors.toList());
        }
        // Break boundary points by a right-inclusive rule
        else {
            return currentProducts.stream().filter(item -> priceRange.getKey() < item.getPrice() && item.getPrice() <= priceRange.getValue()).collect(Collectors.toList());
        }
    }

    /**
     * Set bar color to highlight on double click and reset colors of other bars
     *
     * @param bin XYChart.Data for getting node
     * @param series series of Data for plotting onto histogram
     */
    static void setBarColor(XYChart.Data<String, Integer> bin, XYChart.Series<String, Integer> series) {
        // Apply CSS style
        if (!bin.getNode().getStyleClass().contains(BAR_ACTIVE_CLASS)) {
            bin.getNode().getStyleClass().add(BAR_ACTIVE_CLASS);
        }
        bin.getNode().getStyleClass().removeIf(className -> className.equals(BAR_DEFAULT_CLASS));

        series.getData().forEach(otherBin -> {
            if (otherBin != bin) {
                otherBin.getNode().getStyleClass().removeIf(className -> className.equals(BAR_ACTIVE_CLASS));
                if (!otherBin.getNode().getStyleClass().contains(BAR_DEFAULT_CLASS)) {
                    otherBin.getNode().getStyleClass().add(BAR_DEFAULT_CLASS);
                }
            }
        });
    }
}
