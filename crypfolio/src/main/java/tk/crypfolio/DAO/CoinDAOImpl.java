package tk.crypfolio.DAO;

import tk.crypfolio.domain.CoinEntity;

import javax.persistence.EntityManager;

public class CoinDAOImpl extends DAOImpl<Long, CoinEntity> implements CoinDAO {

	protected CoinDAOImpl(EntityManager em) {
		super(em, CoinEntity.class);
	}

	@Override
	public CoinEntity getCoinById(Long id) {
		return this.getById(id);
	}

    @Override
    public void createCoin(CoinEntity coin) {

        if (getCoinByApiId(coin.getCoinApiId()) == null){
            this.create(coin);
        }
    }

    @Override
    public CoinEntity getCoinByApiId(String coinApiId) {
        return this.findByUniqueStringColumn("coinApiId", coinApiId);
    }

}
