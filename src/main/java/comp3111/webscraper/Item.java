package comp3111.webscraper;


import java.io.Serializable;
import java.util.Date;

/**
 * Represents a product scraped from an online portal
 */
public class Item implements Serializable {
    private String title;
    private double price;
    private String url;
    private Date createdAt;
    private String portal;

    /**
     * Default Constructor
     */
    public Item() {
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
    public Item(String title, double price, String url, Date createdAt, String portal) {
        this.title = title;
        this.price = price;
        this.url = url;
        this.createdAt = createdAt;
        this.portal = portal;
    }

    /**
     * Retrieves the title of the product
     * @return the title of the product
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the product
     * @param title the title of the product
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the price of the product
     * @return the price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the product
     * @param price the price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Retrieves the URL of the product
     * @return the URL of the product
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the product
     * @param url the URL of the product
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Retrieves the portal of where the product comes from
     * @return the portal of where the product comes from
     */
    public String getPortal() {
        return portal;
    }

    /**
     * Sets the portal of where the product comes from
     * @param portal the portal of where the product comes from
     */
    public void setPortal(String portal) {
        this.portal = portal;
    }

    /**
     * Retrieves when the product listing is created
     * @return Date when the product listing is created
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets when the product listing is created
     * @param createdAt Date when the product listing is created
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
