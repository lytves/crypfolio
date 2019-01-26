package tk.crypfolio.DAO;

import tk.crypfolio.model.UserEntity;

public interface UserDAO {

	UserEntity getUserById(Long id);

	UserEntity getUserByEmail(String usEmail);

	UserEntity getUserByEmailVerifCode (String usEmailVerifCode);

	UserEntity getUserByResetPasswordCode (String usResetPasswordCode);

	void createUser(UserEntity u);

	UserEntity updateUser(UserEntity u);
}
