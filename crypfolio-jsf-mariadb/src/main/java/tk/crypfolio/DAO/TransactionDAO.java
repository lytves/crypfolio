package tk.crypfolio.DAO;

import tk.crypfolio.model.TransactionEntity;

public interface TransactionDAO {

    public TransactionEntity getTransactionById(Long id);

    public void createTransaction(TransactionEntity p);
}
