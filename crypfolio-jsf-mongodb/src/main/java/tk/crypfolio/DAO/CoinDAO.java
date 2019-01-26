package tk.crypfolio.DAO;

import tk.crypfolio.model.CoinEntity;

import java.util.List;

public interface CoinDAO {

	CoinEntity getCoinById(Long id);

	void createCoin(CoinEntity coin);

    CoinEntity getCoinBySlug(String coinSlug);

    List<CoinEntity> getAllCoins();
}