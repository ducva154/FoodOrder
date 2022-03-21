package fu.prm392.sampl.is1420_project.dto;

import java.util.List;

public class OrderDTO {
    private String orderID;
    private UserDTO userInfo;
    private BasketDTO basketsInfo;
    private List<BasketItemDocument> listBasketItem;

    public OrderDTO() {
    }

    public List<BasketItemDocument> getListBasketItem() {
        return listBasketItem;
    }

    public void setListBasketItem(List<BasketItemDocument> listBasketItem) {
        this.listBasketItem = listBasketItem;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

    public BasketDTO getBasketsInfo() {
        return basketsInfo;
    }

    public void setBasketsInfo(BasketDTO basketsInfo) {
        this.basketsInfo = basketsInfo;
    }
}
