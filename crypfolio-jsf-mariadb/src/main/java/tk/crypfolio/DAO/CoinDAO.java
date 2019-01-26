package tk.crypfolio.DAO;

import tk.crypfolio.model.CoinEntity;

import java.util.List;

public interface CoinDAO {

	public CoinEntity getCoinById(Long id);

	public void createCoin(CoinEntity coin);

    public CoinEntity getCoinBySlug(String coinSlug);

    public List<CoinEntity> getAllCoins();
}
