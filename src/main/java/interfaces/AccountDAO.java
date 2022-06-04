package interfaces;

import entities.Account;

import java.util.List;

public interface AccountDAO {
    void save(Account account);

    void update(Account account);

    Account findById(int id);

    List<Account> findByUserId(int userId);

}
