package repository.user;

import model.User;
import model.validator.Notification;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    Notification<User> findByUserNameAndPassword(String username, String password);
    boolean save (User user);

    void removeAll();
    boolean existsByUsername(String username);
}
