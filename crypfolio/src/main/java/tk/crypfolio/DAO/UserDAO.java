package tk.crypfolio.DAO;

import tk.crypfolio.domain.UserEntity;

public interface UserDAO {

	public UserEntity getUserById(Long id);

	public UserEntity getUserByEmail(String usEmail);

	public void createUser(UserEntity u);

    public UserEntity updateUser(UserEntity u);
}
