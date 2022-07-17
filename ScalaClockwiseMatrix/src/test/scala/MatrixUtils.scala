package org.github.journeytobecomeepic

import scala.collection.mutable

object MatrixUtils {
  /*
      Complexity Analysis:
      => Time Complexity: O(N), where N is the total number of elements in the input matrix.
      To print matrix in clockwise order, every element in the matrix is added to the result builder.

      => Auxiliary Space: O(N),
        - the information stored in "visited" array
        - result StringBuilder to build the final answer (space complexity is O(n)).
  */

  def printClockwise(matrix: Array[Array[Int]]): String = {
    if (matrix.length == 0) return ""
    val result = new mutable.StringBuilder

    // get rows and columns lengths
    val rowsLen = matrix.length

    val columnsLen = matrix(0).length
    val visited = Array.ofDim[Boolean](rowsLen, columnsLen)
    val directionRows = Array(0, 1, 0, -1)
    val directionColumns = Array(1, 0, -1, 0)

    var visitedX = 0
    var visitedY = 0
    var direction = 0

    // iterate from 0 to Rows * Columns - 1
    val iterationLimit = rowsLen * columnsLen

    for (i <- 0 until iterationLimit) {
      result.append(matrix(visitedX)(visitedY))
      if (i != iterationLimit - 1) {
        result.append(", ")
      }

      visited(visitedX)(visitedY) = true

      val currentRow = visitedX + directionRows(direction)
      val currentColumn = visitedY + directionColumns(direction)
      if (0 <= currentRow
        && currentRow < rowsLen
        && 0 <= currentColumn
        && currentColumn < columnsLen
        && !visited(currentRow)(currentColumn)) {
        visitedX = currentRow
        visitedY = currentColumn
      } else {
        direction = (direction + 1) % 4
        visitedX += directionRows(direction)
        visitedY += directionColumns(direction)
      }
    }

    result.toString
  }
}
