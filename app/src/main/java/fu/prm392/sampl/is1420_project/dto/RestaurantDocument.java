package fu.prm392.sampl.is1420_project.dto;

import java.util.List;

public class RestaurantDocument {
    private RestaurantDTO restaurantDTO;
    private List<FoodDTO> foodsInfo;

    public RestaurantDocument() {
    }

    public RestaurantDocument(RestaurantDTO restaurantDTO, List<FoodDTO> foodsInfo) {
        this.restaurantDTO = restaurantDTO;
        this.foodsInfo = foodsInfo;
    }

    public RestaurantDTO getRestaurantDTO() {
        return restaurantDTO;
    }

    public void setRestaurantDTO(RestaurantDTO restaurantDTO) {
        this.restaurantDTO = restaurantDTO;
    }

    public List<FoodDTO> getFoodsInfo() {
        return foodsInfo;
    }

    public void setFoodsInfo(List<FoodDTO> foodsInfo) {
        this.foodsInfo = foodsInfo;
    }

    @Override
    public String toString() {
        return "RestaurantDocument{" +
                "restaurantDTO=" + restaurantDTO +
                ", foodsInfo=" + foodsInfo +
                '}';
    }
}
