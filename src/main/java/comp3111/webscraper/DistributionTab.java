package comp3111.webscraper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

class DistributionTab {

    private Controller controller;
    private Double numOfBins = 10.0;

    private final String BAR_DEFAULT_CLASS = "default-bar";
    private final String BAR_ACTIVE_CLASS = "active-bar";


    DistributionTab(Controller controller) {
        this.controller = controller;
    }

    /**
     * Call when initialize Distribution
     * @param currentProducts
     */
    void initDistribution(ObservableList<Item> currentProducts) {

        // Set gaps and axes
        controller.barChartHistogram.setCategoryGap(0);
        controller.barChartHistogram.setBarGap(0);
        controller.barChartHistogram.getXAxis().setLabel("Price");
        controller.barChartHistogram.getYAxis().setLabel("Frequency");

        // Update Distribution on every new search
        currentProducts.addListener((ListChangeListener<Item>) change -> {

            // Reset all series
            controller.barChartHistogram.getData().retainAll();
            // Create new series container for data
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            // Set Legend by search keyword
            series.setName("The selling price of " + SearchRecord.getLatestProperty().get().getKeyword());

            // Transform currentProducts to prices
            List<Double> prices = currentProducts.stream().map(Item::getPrice).collect(Collectors.toList());
            HashMap< XYChart.Data<String, Integer>, List<Item> > binnedProducts = new HashMap<>();
            System.out.println("getPriceRanges count: " + getPriceRanges(prices, numOfBins).size());

            // Add bins to barChart and keep mapping between XYChart.Data and list of products for this range
            getPriceRanges(prices, numOfBins).forEach(priceRange -> {
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

            // Apply the new series
            controller.barChartHistogram.getData().addAll(series);

            // Set Double Click listeners for all nodes
            series.getData().forEach(bin -> {
                bin.getNode().setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        System.out.println("Double clicked");

                        // Flush Console and fill Console
                        controller.consoleText.setValue(Controller.generateItemsConsoleOutput(binnedProducts.get(bin)));

                        // Apply CSS style
                        bin.getNode().getStyleClass().add(BAR_ACTIVE_CLASS);
                        series.getData().forEach(otherBin -> {
                            if (otherBin != bin) {
                                otherBin.getNode().getStyleClass().removeIf(className -> className.equals(BAR_ACTIVE_CLASS));
                                otherBin.getNode().getStyleClass().add(BAR_DEFAULT_CLASS);
                            }
                        });
                    }
                });
            });
        });
    }

    /**
     * Extract the bins of price ranges
     * @param prices
     * @param numOfBins
     * @return priceRanges
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
            for (Double x = 0.0; x < range; x += binWidth) {
                priceRanges.add(new Pair<>(x, x + binWidth));
            }
        }
        // else prices is empty, return empty priceRanges
        return priceRanges;
    }

    /**
     * Convert the Pair with its key representing startPrice and value representing endPrice to String bin label
     * @param priceRange
     * @return String of "startPrice-endPrice" bin label
     */
    private String getBinLabel(Pair<Double, Double> priceRange) {
        return String.format("%.1f", priceRange.getKey()) + "-" + String.format("%.1f", priceRange.getValue());
    }

    /**
     * Compute the frequency of products in prices that falls under priceRange
     * [key, value] is a priceRange
     * @param prices
     * @param priceRange
     * @return frequency
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
     * @param currentProducts
     * @param priceRange
     * @return
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
}
