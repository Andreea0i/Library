package service.user;

import model.User;
import model.Role;
import model.builder.UserBuilder;
import model.validator.Notification;
import model.validator.UserValidator;
import repository.user.UserRepository;
import repository.security.RightsRolesRepository;
import repository.user.UserRepositoryMySQL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RightsRolesRepository rightsRolesRepository;
    private final service.user.AuthenticationService authenticationService;

    public UserServiceImpl(UserRepository userRepository,
                           RightsRolesRepository rightsRolesRepository,
                           service.user.AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.rightsRolesRepository = rightsRolesRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public Notification<Boolean> createUser(String username, String password, String roleName) {
        Notification<Boolean> notification = new Notification<>();


        if (username == null || username.trim().isEmpty()) {
            notification.addError("Username cannot be empty!");
            notification.setResult(false);
            return notification;
        }

        if (password == null || password.trim().isEmpty()) {
            notification.addError("Password cannot be empty!");
            notification.setResult(false);
            return notification;
        }

        if (userRepository.existsByUsername(username)) {
            notification.addError("Username already exists!");
            notification.setResult(false);
            return notification;
        }

        Role role = rightsRolesRepository.findRoleByTitle(roleName);
        if (role == null) {
            notification.addError("Invalid role: " + roleName);
            notification.setResult(false);
            return notification;
        }

        

        Notification<Boolean> registerNotification = authenticationService.register(username, password);

        if (registerNotification.hasErrors()) {
            registerNotification.getFormattedErrors()
                    .lines()
                    .forEach(notification::addError);
            notification.setResult(false);
            return notification;
        }

        try {

            List<User> allUsers = userRepository.findAll();
            User newUser = allUsers.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            if (newUser != null) {

                List<Role> roles = new ArrayList<>();
                roles.add(role);
                newUser.setRoles(roles);

                ((UserRepositoryMySQL) userRepository).updateUserRoles(newUser.getId(), roles);
            }

            notification.setResult(true);
        } catch (Exception e) {
            notification.addError("Error setting user role: " + e.getMessage());
            notification.setResult(false);
        }

        return notification;
    }

    @Override
    public Notification<Boolean> deleteUser(Long id) {
        Notification<Boolean> notification = new Notification<>();

        try {
            // Nu permite ștergerea utilizatorului cu ID 1 (presupunând că este admin principal)
            if (id == 1) {
                notification.addError("Cannot delete primary administrator!");
                notification.setResult(false);
                return notification;
            }

            boolean deleted = ((UserRepositoryMySQL) userRepository).removeById(id);
            notification.setResult(deleted);

            if (!deleted) {
                notification.addError("User could not be deleted!");
            }
        } catch (Exception e) {
            notification.addError("Error deleting user: " + e.getMessage());
            notification.setResult(false);
        }

        return notification;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return ((UserRepositoryMySQL) userRepository).findById(id);
    }

    @Override
    public Notification<Boolean> updateUserRoles(Long userId, List<String> roleNames) {
        Notification<Boolean> notification = new Notification<>();

        try {
            List<Role> roles = new ArrayList<>();
            for (String roleName : roleNames) {
                Role role = rightsRolesRepository.findRoleByTitle(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }

            if (roles.isEmpty()) {
                notification.addError("No valid roles provided!");
                notification.setResult(false);
                return notification;
            }

            boolean updated = ((UserRepositoryMySQL) userRepository).updateUserRoles(userId, roles);
            notification.setResult(updated);

            if (!updated) {
                notification.addError("Failed to update user roles!");
            }
        } catch (Exception e) {
            notification.addError("Error updating user roles: " + e.getMessage());
            notification.setResult(false);
        }

        return notification;
    }
}
