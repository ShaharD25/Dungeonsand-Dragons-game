package game.map;

/**
 * Represents a 2D position on the game board.
 * Each position contains a row (Y-axis) and column (X-axis).
 */
public class Position {
    private int row,col;
    /**
     * Constructs a new Position with a given row and column.
     *
     * @param row The row index (Y coordinate)
     * @param col The column index (X coordinate)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Getters
    public int getRow() { return row; }
    public int getCol() { return col; }

    /**
     * Calculates the Manhattan distance to another position.
     * This is used for visibility range, movement rules, etc.
     *
     * @param other The target position
     * @return The Manhattan distance
     */
    public int distanceTo(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    /**
     * Sets a new row if it's valid (non-negative).
     *
     * @param row The new row value
     * @return true if set successfully, false otherwise
     */
    public boolean setRow(int row) {
        if (row >= 0) {
            this.row = row;
            return true;
        }
        return false;
    }

    /**
     * Sets a new column if it's valid (non-negative).
     *
     * @param col The new column value
     * @return true if set successfully, false otherwise
     */
    public boolean setCol(int col) {
        if (col >= 0) {
            this.col = col;
            return true;
        }
        return false;
    }


    /**
     * Checks if another object is equal to this Position.
     * Equality is based on matching row and column values.
     *
     * @param o The object to compare
     * @return true if the positions are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    /**
     * Returns a human-readable string version of this position.
     *
     * @return A string like (row, col)
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
