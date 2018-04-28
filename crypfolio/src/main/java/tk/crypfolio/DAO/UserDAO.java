package tk.crypfolio.DAO;

import tk.crypfolio.domain.UserEntity;

public interface UserDAO {

	public UserEntity getUserById(Long id);

	public UserEntity getUserByEmail(String usEmail);

	public UserEntity getUserByEmailVerifCode (String usEmailVerifCode);

	public void createUser(UserEntity u);

    public UserEntity updateUser(UserEntity u);
}
