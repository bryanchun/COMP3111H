package comp3111.webscraper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class DistributionTab {

    private Controller controller;
    private Integer numOfBins = 10;

    DistributionTab(Controller controller) {
        this.controller = controller;
    }

    void initDistribution(ObservableList<Item> currentProducts) {

        controller.barChartHistogram.setCategoryGap(0);
        controller.barChartHistogram.setBarGap(0);
        controller.barChartHistogram.getXAxis().setLabel("Price");
        controller.barChartHistogram.getYAxis().setLabel("Frequency");

        // Update Distribution on every new search
        currentProducts.addListener((ListChangeListener<Item>) change -> {

            // Reset all series
            controller.barChartHistogram.getData().retainAll();

            // Create list of prices
            List<Double> prices = currentProducts.stream().map(Item::getPrice).collect(Collectors.toList());

            // Create series container for data
            XYChart.Series<String, Integer> series = new XYChart.Series<>();

            // Apply bin label and frequency to each series datum
            for (Pair<Double, Double> priceRange : getPriceRanges(prices, numOfBins)) {
                series.getData().add(new XYChart.Data<>(
                        getBinLabel(priceRange),
                        getFrequency(prices, priceRange)
                ));
            }

            // Legend
            series.setName("The selling price of " + SearchRecord.getLatestProperty().get().getKeyword());

            // Add single new series
            controller.barChartHistogram.getData().addAll(series);
        });
    }

    /**
     * Extract the bins of price ranges
     * @param prices
     * @param numOfBins
     * @return priceRanges
     */
    private List<Pair<Double, Double>> getPriceRanges(List<Double> prices, Integer numOfBins) {
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
}
