package tk.crypfolio.DAO;

import tk.crypfolio.model.TransactionEntity;

import javax.persistence.EntityManager;

public class TransactionDAOImpl extends DAOImpl<Long, TransactionEntity> implements TransactionDAO {

	protected TransactionDAOImpl(EntityManager em) {
		super(em, TransactionEntity.class);
	}

	@Override
	public TransactionEntity getTransactionById(Long id) {
		return this.getById(id);
	}

	@Override
	public void createTransaction(TransactionEntity transaction) {
		this.create(transaction);
	}
}
