package tk.crypfolio.DAO;

import tk.crypfolio.model.PositionEntity;

public interface PositionDAO {

    public PositionEntity getPositionById(Long id);

    public void createPosition(PositionEntity p);
}
