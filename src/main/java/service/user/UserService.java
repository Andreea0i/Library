package service.user;

import model.User;
import model.validator.Notification;
import java.util.List;

//creier pt useri
public interface UserService {
    Notification<Boolean> createUser(String username, String password, String role);
    Notification<Boolean> deleteUser(Long id);
    List<User> findAllUsers();
    User findById(Long id);
    Notification<Boolean> updateUserRoles(Long userId, List<String> roleNames);
}
