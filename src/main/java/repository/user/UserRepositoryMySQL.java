package repository.user;

import model.User;
import model.Role;
import model.Right;
import model.builder.UserBuilder;
import model.validator.Notification;
import repository.security.RightsRolesRepository;


import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import static database.Constants.Tables.USER;

public class UserRepositoryMySQL implements UserRepository {

    private final Connection connection;
    private final RightsRolesRepository rightsRolesRepository;

    public UserRepositoryMySQL(Connection connection, RightsRolesRepository rightsRolesRepository) {
        this.connection = connection;
        this.rightsRolesRepository = rightsRolesRepository;
    }

    @Override
    public Notification<User> findByUserNameAndPassword(String username, String password) {
        Notification<User> notification = new Notification<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM `" + USER + "` WHERE `username`=? AND `password`=?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                Long userId = rs.getLong("id");

                User user = new UserBuilder()
                        .setId(userId)
                        .setUsername(rs.getString("username"))
                        .setPassword(rs.getString("password"))
                        .setRoles(rightsRolesRepository.findRolesForUser(userId))
                        .build();

                notification.setResult(user);
                System.out.println("DEBUG: User logged in with ID: " + userId);
            } else {
                notification.addError("Invalid username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            notification.addError("Database error!");
        }
        return notification;
    }

    @Override
    public boolean save(User user) {
        try {
            PreparedStatement insertUserStatement = connection
                    .prepareStatement("INSERT INTO user values (null, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertUserStatement.setString(1, user.getUsername());
            insertUserStatement.setString(2, user.getPassword());
            insertUserStatement.executeUpdate();

            ResultSet rs = insertUserStatement.getGeneratedKeys();
            rs.next();
            long userId = rs.getLong(1);
            user.setId(userId);

            rightsRolesRepository.addRolesToUser(user, user.getRoles());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void removeAll() {
        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE from user where id >= 0";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsByUsername(String email) {
        try {
            Statement statement = connection.createStatement();
            String fetchUserSql =
                    "Select * from `" + USER + "` where `username`=\"" + email + "\"";
            ResultSet userResultSet = statement.executeQuery(fetchUserSql);
            return userResultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM " + USER;
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                User user = new UserBuilder()
                        .setId(resultSet.getLong("id"))
                        .setUsername(resultSet.getString("username"))
                        .setPassword(resultSet.getString("password"))
                        .setRoles(rightsRolesRepository.findRolesForUser(resultSet.getLong("id")))
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean removeById(Long id) {
        try {
            PreparedStatement deleteUserRoleStatement = connection
                    .prepareStatement("DELETE FROM user_role WHERE user_id = ?");
            deleteUserRoleStatement.setLong(1, id);
            deleteUserRoleStatement.executeUpdate();

            PreparedStatement deleteUserStatement = connection
                    .prepareStatement("DELETE FROM " + USER + " WHERE id = ?");
            deleteUserStatement.setLong(1, id);
            int rowsAffected = deleteUserStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public User findById(Long id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + USER + " WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new UserBuilder()
                        .setId(resultSet.getLong("id"))
                        .setUsername(resultSet.getString("username"))
                        .setPassword(resultSet.getString("password"))
                        .setRoles(rightsRolesRepository.findRolesForUser(id))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean updateUserRoles(Long userId, List<Role> newRoles) {
        try {

            PreparedStatement deleteStatement = connection
                    .prepareStatement("DELETE FROM user_role WHERE user_id = ?");
            deleteStatement.setLong(1, userId);
            deleteStatement.executeUpdate();


            for (Role role : newRoles) {
                PreparedStatement insertStatement = connection
                        .prepareStatement("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)");
                insertStatement.setLong(1, userId);
                insertStatement.setLong(2, role.getId());
                insertStatement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
