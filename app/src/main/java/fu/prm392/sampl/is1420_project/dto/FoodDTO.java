package fu.prm392.sampl.is1420_project.dto;

import java.io.Serializable;

public class FoodDTO implements Serializable {
    private String foodID;
    private String name;
    private double price;
    private String description;
    private String image;
    private String status;

    public FoodDTO() {
    }

    public FoodDTO(String foodID, String name, double price, String description, String image, String status) {
        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.status = status;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FoodDTO{" +
                "foodID='" + foodID + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", detail='" + description + '\'' +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
