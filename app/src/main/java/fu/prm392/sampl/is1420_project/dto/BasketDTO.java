package fu.prm392.sampl.is1420_project.dto;

import java.io.Serializable;

public class BasketDTO implements Serializable {
    private String basketID;
    private RestaurantDTO restaurantsInfo;
    private int basketQuantity;
    private double basketPrice;


    public BasketDTO() {
    }

    public String getBasketID() {
        return basketID;
    }

    public void setBasketID(String basketID) {
        this.basketID = basketID;
    }

    public RestaurantDTO getRestaurantsInfo() {
        return restaurantsInfo;
    }

    public void setRestaurantsInfo(RestaurantDTO restaurantsInfo) {
        this.restaurantsInfo = restaurantsInfo;
    }


    public int getBasketQuantity() {
        return basketQuantity;
    }

    public void setBasketQuantity(int basketQuantity) {
        this.basketQuantity = basketQuantity;
    }

    public double getBasketPrice() {
        return basketPrice;
    }

    public void setBasketPrice(double basketPrice) {
        this.basketPrice = basketPrice;
    }
}
