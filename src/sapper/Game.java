package sapper;

public class Game {

    private final Bomb bomb;
    private final Flag flag;
    private Box state;

    private GameState gameState;

    public GameState getGameState() {
        return gameState;
    }

    public Game(int cols, int rows, int bombs) {
        Ranges.setSize(new Coordinate(cols, rows));
        bomb = new Bomb(bombs);
        flag = new Flag();
    }

    public void start() {
        bomb.start();
        flag.start();
        gameState = GameState.PLAYED;
    }

    public Box getBox(Coordinate coordinate) {
        if (flag.get(coordinate) == Box.OPENED)
            return bomb.get(coordinate);
        else
            return flag.get(coordinate);
    }

    public void pressLeftButton(Coordinate coordinate) {
        if (gameOver()) return;
        openBox(coordinate);
        checkWinner();
    }

    private void checkWinner() {
        if (gameState == GameState.PLAYED) {
            if (flag.getCountOfClosedBoxes() == bomb.getTotalBombs()) {
                gameState = GameState.WINNER;
            }
        }
    }

    private void openBox(Coordinate coordinate) {
        switch (flag.get(coordinate)) {
            case OPENED:
                setOpenedToClosedBoxesAroundNumber(coordinate);
                return;
            case FLAGED:
                return;
            case CLOSED, INFORM:
                switch (bomb.get(coordinate)) {
                    case ZERO:
                        openBoxesAround(coordinate);
                        break;
                    case BOMB:
                        openBombs(coordinate);
                        break;
                    default:
                        flag.setOpenedToBox(coordinate);
                        break;
                }
        }
    }

    private void setOpenedToClosedBoxesAroundNumber(Coordinate coordinate) {
        if (bomb.get(coordinate) != Box.BOMB)
            if (flag.getCountOfFlagedBoxesAround(coordinate) == bomb.get(coordinate).getNumber())
                for (Coordinate around :
                        Ranges.getCoordinatesAround(coordinate)) {
                    if (flag.get(around) == Box.CLOSED)
                        openBox(around);
                }
    }

    private void openBombs(Coordinate bombed) {
        gameState = GameState.BOMBED;
        flag.setBombedToBox(bombed);
        for (Coordinate coordinate :
                Ranges.getAllCoordinates()) {
            if (bomb.get(coordinate) == Box.BOMB)
                flag.setOpenedToClosedBombBox(coordinate);
            else flag.setNobombToFlagedSafeBox(coordinate);
        }
    }

    private void openBoxesAround(Coordinate coordinate) {
        flag.setOpenedToBox(coordinate);
        for (Coordinate around : Ranges.getCoordinatesAround(coordinate)) {
            openBox(around);
        }
    }

    public void pressRightButton(Coordinate coordinate) {
        if (gameOver()) return;
        flag.toggleFlagedToBox(coordinate);
    }

    public void pointerOpen(Coordinate coordinate) {
        state = flag.get(coordinate);
        flag.setPointToBox(coordinate);
    }

    public void pointerClose(Coordinate coordinate) {
        if (flag.get(coordinate) == Box.INFORM) {
            switch (state) {
                case OPENED:
                    flag.setOpenedToBox(coordinate);
                    return;
                case FLAGED:
                    flag.setFlagedToBox(coordinate);
                    return;
                case CLOSED:
                    flag.setClosedToBox(coordinate);
                    return;
            }
        }
    }

    private boolean gameOver() {
        if (gameState == GameState.PLAYED)
            return false;
        start();
        return true;
    }
}
