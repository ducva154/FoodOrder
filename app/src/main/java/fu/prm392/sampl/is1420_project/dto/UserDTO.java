package fu.prm392.sampl.is1420_project.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private String userID;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String role;
    private String status;
    private String photoUri;

    public UserDTO() {
    }

    public UserDTO(String userID, String email, String fullName, String phone, String role, String status, String photoUri) {
        this.userID = userID;
        this.email = email;
        this.name = fullName;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.photoUri = photoUri;
    }

    public UserDTO(String userID, String email, String name, String phone, String address, String role, String status, String photoUri) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.status = status;
        this.photoUri = photoUri;
    }

    public UserDTO(String userID, String email, String name, String role, String status, String phone) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }
}
