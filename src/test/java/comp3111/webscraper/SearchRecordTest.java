package comp3111.webscraper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SearchRecordTest {

    @Test
    public void testSetKeyword() {
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.setKeyword("test");
        assertEquals(searchRecord.getKeyword(), "test");
    }

    @Test
    public void testItemsList() {
        SearchRecord searchRecord = new SearchRecord();

        List<Item> list = new ArrayList<>();
        Item itemTest = new Item();
        itemTest.setTitle("test");
        itemTest.setPrice(12.0);
        itemTest.setUrl("http://example.com");
        itemTest.setPortal("Example");
        itemTest.setCreatedAt(new Date());
        list.add(itemTest);

        searchRecord.getProducts().addAll(list);

        assertThat(searchRecord.getProducts(), is(list));
    }

    @Test
    public void testKeyword() {
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.setKeyword("test");
        assertEquals("test", searchRecord.getKeyword());
    }

    @Test
    public void testHistory() {
        SearchRecord.pushHistory(new SearchRecord("1", new ArrayList<>(), false));
        SearchRecord.pushHistory(new SearchRecord("2", new ArrayList<>(), false));
        SearchRecord.pushHistory(new SearchRecord("3", new ArrayList<>(), false));
        SearchRecord.pushHistory(new SearchRecord("4", new ArrayList<>(), false));
        SearchRecord.pushHistory(new SearchRecord("5", new ArrayList<>(), false));
        SearchRecord.pushHistory(new SearchRecord("6", new ArrayList<>(), false));

        assertFalse(SearchRecord.getAllSearchRecords().stream().anyMatch(item -> item.getKeyword().equals("1")));
    }
}
