package comp3111.webscraper;

import org.junit.Test;

import java.util.List;

/**
 * This test may use ~10s to complete
 */
public class WebScraperTest {

    @Test
    public void testScraping() {
        WebScraper scraper = new WebScraper();
        List<Item> products = scraper.scrape("test");
    }

}
