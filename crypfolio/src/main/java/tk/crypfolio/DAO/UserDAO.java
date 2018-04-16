package tk.crypfolio.DAO;

import tk.crypfolio.domain.UserEntity;

public interface UserDAO {

	public UserEntity getUserById(Long id);

	public UserEntity getUserByUsEmail(String usEmail);

}
