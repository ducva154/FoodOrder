package fu.prm392.sampl.is1420_project.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private String orderID;
    private UserDTO userInfo;
    private BasketDTO basketsInfo;
    private List<BasketItemDocument> listBasketItem;
    private Date orderTime;
    private String address;
    private String status;

    public OrderDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
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
