// view/model/UserDTO.java
package view.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserDTO {
    private final LongProperty id;
    private final StringProperty username;
    private final StringProperty roles;

    public UserDTO(Long id, String username, String roles) {
        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.roles = new SimpleStringProperty(roles);
    }

    // Getters for properties
    public Long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getRoles() {
        return roles.get();
    }

    public StringProperty rolesProperty() {
        return roles;
    }
}