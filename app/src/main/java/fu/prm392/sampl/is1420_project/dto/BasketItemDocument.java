package fu.prm392.sampl.is1420_project.dto;

public class BasketItemDocument {
    private BasketItemDTO basketItemsInfo;
    private BasketDTO basketsInfo;
    private FoodDTO foodsInfo;

    public BasketItemDTO getBasketItemsInfo() {
        return basketItemsInfo;
    }

    public void setBasketItemsInfo(BasketItemDTO basketItemsInfo) {
        this.basketItemsInfo = basketItemsInfo;
    }

    public BasketDTO getBasketsInfo() {
        return basketsInfo;
    }

    public void setBasketsInfo(BasketDTO basketsInfo) {
        this.basketsInfo = basketsInfo;
    }

    public FoodDTO getFoodsInfo() {
        return foodsInfo;
    }

    public void setFoodsInfo(FoodDTO foodsInfo) {
        this.foodsInfo = foodsInfo;
    }
}
