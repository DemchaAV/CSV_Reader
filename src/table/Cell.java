package table;

public class Cell {
    private String value;
    private int posX;
    private int posY;

    public Cell(String value, int posX, int posY) {
        this.value = value;
        this.posX = posX;
        this.posY = posY;
    }

    public String toUpperCase() {
        value = value.toUpperCase();
        return value;

    }

    public String wrap(String wrap) {
        if (wrap == null) {
            return value;
        }
        value = wrap + value + wrap;
        return value;
    }

    public String setCapitalLater() {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String leter = String.valueOf(value.charAt(0)).toUpperCase();
        value = leter + value.substring(1, value.length()).toLowerCase();
        return value;

    }

    @Override
    public String toString() {
        return value;
    }
}
