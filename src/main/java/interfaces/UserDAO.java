package interfaces;

import entities.User;

public interface UserDAO {
    User findByName(String name);

    void save(User user);
}
