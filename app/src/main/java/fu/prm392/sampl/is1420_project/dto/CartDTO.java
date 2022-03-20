package fu.prm392.sampl.is1420_project.dto;

import java.io.Serializable;

public class CartDTO implements Serializable {
    private String cartID;
    private UserDTO userInfo;

    public CartDTO() {
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

}
