package fu.prm392.sampl.is1420_project.dto;

import java.util.List;

public class BasketDocument {
    private List<BasketItemDTO> basketItemsInfo;
    private BasketDTO basketsInfo;
    private CartDTO cartsInfo;

    public CartDTO getCartsInfo() {
        return cartsInfo;
    }

    public void setCartsInfo(CartDTO cartsInfo) {
        this.cartsInfo = cartsInfo;
    }

    public List<BasketItemDTO> getBasketItemsInfo() {
        return basketItemsInfo;
    }

    public void setBasketItemsInfo(List<BasketItemDTO> basketItemsInfo) {
        this.basketItemsInfo = basketItemsInfo;
    }

    public BasketDTO getBasketsInfo() {
        return basketsInfo;
    }

    public void setBasketsInfo(BasketDTO basketsInfo) {
        this.basketsInfo = basketsInfo;
    }
}
