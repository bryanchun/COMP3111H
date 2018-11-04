package comp3111.webscraper;


import java.util.Date;

/**
 * Represents a product scraped from an online portal
 */
public class Item {
    private String title;
    private double price;
    private String url;
    private Date createdAt;
    private String portal;

    /**
     * Default Constructor
     */
    Item() {
    }

    /**
     * Constructs Item with all fields
     *
     * @param title     The product title
     * @param price     The product price
     * @param url       The product details URL
     * @param createdAt The product creation date
     * @param portal    The name of the portal the product comes from
     */
    Item(String title, double price, String url, Date createdAt, String portal) {
        this.title = title;
        this.price = price;
        this.url = url;
        this.createdAt = createdAt;
        this.portal = portal;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    double getPrice() {
        return price;
    }

    void setPrice(double price) {
        this.price = price;
    }

    String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
