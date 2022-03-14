package fu.prm392.sampl.is1420_project.dto;

import java.util.List;

public class UserDocument {
    private UserDTO userInfo;
    private List<RestaurantDTO> restaurentsInfo;
    private List<String> tokens;

    public UserDocument() {
    }

    public UserDocument(UserDTO userInfo, List<RestaurantDTO> restaurentsInfo, List<String> tokens) {
        this.userInfo = userInfo;
        this.restaurentsInfo = restaurentsInfo;
        this.tokens = tokens;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

    public List<RestaurantDTO> getRestaurentsInfo() {
        return restaurentsInfo;
    }

    public void setRestaurentsInfo(List<RestaurantDTO> restaurentsInfo) {
        this.restaurentsInfo = restaurentsInfo;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return "UserDocument{" +
                "userInfo=" + userInfo +
                ", restaurentsInfo=" + restaurentsInfo +
                ", tokens=" + tokens +
                '}';
    }
}
