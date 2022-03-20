package fu.prm392.sampl.is1420_project.dto;

import java.io.Serializable;

public class BasketItemDTO implements Serializable {
    private String basketItemID;
    private int quantity;
    private double price;

    public BasketItemDTO() {
    }

    public String getBasketItemID() {
        return basketItemID;
    }

    public void setBasketItemID(String basketItemID) {
        this.basketItemID = basketItemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
