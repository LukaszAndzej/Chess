package lib.interfaces;

import java.util.List;
import java.util.Map;

import lib.logic.Position;

public interface MoveLogic {
    public void move(int newX, int newY);
    public List<Position> getPossibleMoves(final Map<Position, Figure> figures);
}
