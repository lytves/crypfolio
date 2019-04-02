package tk.crypfolio.DAO;

import tk.crypfolio.model.CoinEntity;

import javax.persistence.EntityManager;
import java.util.List;

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

        if (getCoinBySlug(coin.getSlug()) == null){
            this.create(coin);
        }
    }

    @Override
    public CoinEntity getCoinBySlug(String coinSlug) {
        return this.findByUniqueStringColumn("slug", coinSlug);
    }

    @Override
    public List<CoinEntity> getAllCoins() {
        return this.findAll();
    }
}