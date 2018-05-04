package tk.crypfolio.DAO;

import tk.crypfolio.model.CoinEntity;

public interface CoinDAO {

	public CoinEntity getCoinById(Long id);

	public void createCoin(CoinEntity coin);

    public CoinEntity getCoinByApiId(String coinApiId);

}
