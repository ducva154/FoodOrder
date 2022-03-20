package fu.prm392.sampl.is1420_project.dto;


import java.util.List;

public class CartDocument {
    private CartDTO cartsInfo;
    private List<BasketDTO> basketsInfo;

    public CartDocument() {
    }

    public CartDTO getCartsInfo() {
        return cartsInfo;
    }

    public void setCartsInfo(CartDTO cartsInfo) {
        this.cartsInfo = cartsInfo;
    }

    public List<BasketDTO> getBasketsInfo() {
        return basketsInfo;
    }

    public void setBasketsInfo(List<BasketDTO> basketsInfo) {
        this.basketsInfo = basketsInfo;
    }
}
