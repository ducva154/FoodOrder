package fu.prm392.sampl.is1420_project.dto;

import java.util.List;

public class UserDocument {
    private UserDTO userInfo;
    private List<RestaurantDTO> restaurantsInfo;
    private List<String> tokens;

    public UserDocument() {
    }

    public UserDocument(UserDTO userInfo, List<RestaurantDTO> restaurantsInfo, List<String> tokens) {
        this.userInfo = userInfo;
        this.restaurantsInfo = restaurantsInfo;
        this.tokens = tokens;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

    public List<RestaurantDTO> getRestaurantsInfo() {
        return restaurantsInfo;
    }

    public void setRestaurantsInfo(List<RestaurantDTO> restaurantsInfo) {
        this.restaurantsInfo = restaurantsInfo;
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
                ", restaurantsInfo=" + restaurantsInfo +
                ", tokens=" + tokens +
                '}';
    }
}
