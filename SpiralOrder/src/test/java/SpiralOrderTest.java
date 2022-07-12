import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpiralOrderTest {
    @Test
    void simulation_algorithm() {

        final String expected = "2, 3, 4, 8, 12, 10, 6, 0, 1, 5, 7, 9";

        int[][] matrix = {
                {2, 3, 4, 8},
                {5, 7, 9, 12},
                {1, 0, 6, 10}
        };

        var actual = spiralOrder(matrix);

        assertEquals(expected, actual);
    }

    /*
    Complexity Analysis:

    => Time Complexity: O(N), where N is the total number of elements in the input matrix.
        We add every element in the matrix to our final answer.

    => Auxiliary Space: O(N), the information stored in seen array and in result StringBuilder (space complexity is O(n)).
     */
    public static String spiralOrder(int[][] matrix) {
        if (matrix.length == 0)
            return "";

        var result = new StringBuilder();

        // get rows and columns lengths
        int rowsLen = matrix.length;
        int columnsLen = matrix[0].length;

        boolean[][] seen = new boolean[rowsLen][columnsLen];
        int[] directionRows = {0, 1, 0, -1};
        int[] directionColumns = {1, 0, -1, 0};
        int seenX = 0, seenY = 0, direction = 0;

        // iterate from 0 to Rows * Columns - 1
        int iterationLimit = rowsLen * columnsLen;
        for (int i = 0; i < iterationLimit; i++) {
            if (i == iterationLimit - 1) {
                result.append(matrix[seenX][seenY]);
            } else {
                result.append(matrix[seenX][seenY]).append(", ");
            }
            seen[seenX][seenY] = true;
            int currentRow = seenX + directionRows[direction];
            int currentColumn = seenY + directionColumns[direction];

            if (0 <= currentRow
                    && currentRow < rowsLen
                    && 0 <= currentColumn
                    && currentColumn < columnsLen
                    && !seen[currentRow][currentColumn]) {
                seenX = currentRow;
                seenY = currentColumn;
            } else {
                direction = (direction + 1) % 4;
                seenX += directionRows[direction];
                seenY += directionColumns[direction];
            }
        }

        return result.toString();
    }
}
