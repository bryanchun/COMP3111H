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
    public void testSetRefineKeyword() {
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.setRefineKeyword("test");
        assertEquals(searchRecord.getRefineKeyword(), "test");
        assertEquals(searchRecord.getRefineKeywordProperty().getValue(), "test");
    }

    @Test
    public void testItemsList() {
        SearchRecord searchRecord = new SearchRecord();

        List<Item> list = new ArrayList<>();
        Item itemTest = new Item();
        itemTest.setTitle("test");
        itemTest.setPrice(12.0);
        itemTest.setUrl("http://example.com");
        itemTest.setCreatedAt(new Date());
        list.add(itemTest);

        searchRecord.getItems().addAll(list);

        assertThat(searchRecord.getItems(), is(list));
    }
}
