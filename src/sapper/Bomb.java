package sapper;

class Bomb {
    private Matrix bombMap;
    private int totalBombs;

    public Bomb(int totalBombs) {
        this.totalBombs = totalBombs;
        fixBombsCount();
    }

    void start() {
        bombMap = new Matrix(Box.ZERO);
        for (int i = 0; i < totalBombs; i++) {
            placeBomb();
        }
    }

    Box get(Coordinate coordinate) {
        return bombMap.get(coordinate);
    }

    private void fixBombsCount() {
        int maxBombs = Ranges.getSize().x * Ranges.getSize().y / 2;
        if (totalBombs > maxBombs)
            totalBombs = maxBombs;
    }

    private void placeBomb() {
        while (true) {
            Coordinate coordinate = Ranges.getRandomCoordinate();
            if (Box.BOMB == bombMap.get(coordinate))
                continue;
            bombMap.set(coordinate, Box.BOMB);
            incNumberAroundBomb(coordinate);
            break;
        }
    }

    private void incNumberAroundBomb(Coordinate coordinate) {
        for (Coordinate around :
                Ranges.getCoordinatesAround(coordinate)) {
            if (Box.BOMB != bombMap.get(around))
                bombMap.set(around, bombMap.get(around).getNextNumberBox());
        }
    }

    int getTotalBombs() {
        return totalBombs;
    }
}
