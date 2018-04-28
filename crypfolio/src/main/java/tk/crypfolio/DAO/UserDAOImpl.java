package tk.crypfolio.DAO;

import tk.crypfolio.domain.UserEntity;

import javax.persistence.EntityManager;

public class UserDAOImpl extends DAOImpl<Long, UserEntity> implements UserDAO {

    protected UserDAOImpl(EntityManager em) {
        super(em, UserEntity.class);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return this.getById(id);
    }

    @Override
    public UserEntity getUserByEmail(String usEmail) {
        return this.findByUniqueStringColumn("email", usEmail);
    }

    @Override
    public UserEntity getUserByEmailVerifCode(String usEmailVerifCode) {
        return this.findByUniqueStringColumn("emailVerifCode", usEmailVerifCode);
    }

    @Override
    public void createUser(UserEntity user) {

        if (getUserByEmail(user.getEmail()) == null){
            this.create(user);
        }
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        return this.update(user);
    }
}
