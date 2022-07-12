import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpiralOrderTest {
    public static Stream<Arguments> simulationApproachProvider() {
        return Stream.of(
                Arguments.of(new int[][]{{2, 3, 4, 8}, {5, 7, 9, 12}, {1, 0, 6, 10}}, "2, 3, 4, 8, 12, 10, 6, 0, 1, 5, 7, 9"),
                Arguments.of(new int[][]{{2}, {5}, {1}}, "2, 5, 1"),
                Arguments.of(new int[][]{{2, 3, 4, 8}}, "2, 3, 4, 8"),
                Arguments.of(new int[][]{{2}}, "2"));
    }

    /*
    Complexity Analysis:

    => Time Complexity: O(N), where N is the total number of elements in the input matrix.
        We add every element in the matrix to our final answer.

    => Auxiliary Space: O(N), the information stored in seen array and in result StringBuilder (space complexity is O(n)).
     */
    public static String clockwiseMatrix(int[][] matrix) {
        if (matrix.length == 0) return "";

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

            if (0 <= currentRow && currentRow < rowsLen && 0 <= currentColumn && currentColumn < columnsLen && !seen[currentRow][currentColumn]) {
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

    @DisplayName("Should build a string with the entries of given matrix appended in clockwise order")
    @ParameterizedTest(name = "{index} => matrix={0}, expected={1}")
    @MethodSource("simulationApproachProvider")
    void simulationApproach(int[][] matrix, String expected) {

        var actual = clockwiseMatrix(matrix);

        assertEquals(expected, actual);

        System.out.println(expected);
        System.out.println(actual);
        System.out.println("=======");
    }
}
