package com.bakeryinventory.service;

import com.bakeryinventory.dao.UserDao;
import com.bakeryinventory.model.User;
import com.bakeryinventory.util.Validation;
import com.bakeryinventory.util.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String username, String password) throws SQLException, ValidationException {
        Validation.requireUsername(username);
        Validation.requirePassword(password);
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new ValidationException("Invalid username or password."));
        if (!PasswordService.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            throw new ValidationException("Invalid username or password.");
        }
        return user;
    }

    public void createUser(User currentUser, String username, String password) throws SQLException, ValidationException {
        if (currentUser == null || !currentUser.isManager()) {
            throw new ValidationException("Only the manager can create user accounts.");
        }
        Validation.requireUsername(username);
        Validation.requirePassword(password);
        if (userDao.usernameExists(username)) {
            throw new ValidationException("This username is already in use.");
        }
        PasswordService.HashResult hash = PasswordService.hashPassword(password);
        userDao.createUser(username, hash.hash(), hash.salt(), "USER");
    }

    public List<User> listUsers() throws SQLException {
        return userDao.findAll();
    }

    public void deleteUser(User currentUser, int userId) throws SQLException, ValidationException {
        if (currentUser == null || !currentUser.isManager()) {
            throw new ValidationException("Only the manager can delete user accounts.");
        }
        if (currentUser.getId() == userId) {
            throw new ValidationException("The manager account cannot be deleted.");
        }
        userDao.deleteUser(userId);
    }
}
