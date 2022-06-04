package services;

import entities.User;
import interfaces.UserDAO;

public class UserService {
    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getUser(String name) {
        return userDAO.findByName(name);
    }

    public void createUser(String name) {
        User user = new User(0, name);
        userDAO.save(user);
    }

}
