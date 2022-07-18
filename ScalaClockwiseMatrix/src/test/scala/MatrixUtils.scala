package org.github.journeytobecomeepic

import scala.collection.mutable

object MatrixUtils {
  /*
  # Algorithm

  It holds a "visited matrix" to store visited information, so visited[index] denotes that the cell on the column was previously seen.

  Then it moves through the matrix and follows the "R>D>L>U" directions order, if the candidate is in the bounds of the matrix and unseen,
  it becomes the next position; otherwise, the next position is the one after performing a clockwise turn.

  # Complexity Analysis:
  => Time Complexity: O(N), where N is the total number of elements in the input matrix.
     To print matrix in clockwise order, every element in the matrix is added to the result builder.

  => Auxiliary Space: O(N),
     - the information stored in "visited" array
     - result StringBuilder to build the final answer (space complexity is O(n)).
  */

  // !!! This is my initial implementation, please see an IMPROVED version below.
  def printClockwiseInitial(matrix: Array[Array[Int]]): String = {
    val rowsLen = matrix.length
    if (rowsLen == 0) return ""
    val columnsLen = matrix(0).length
    if (columnsLen == 0) return ""

    val result = new mutable.StringBuilder

    val visited = Array.ofDim[Boolean](rowsLen, columnsLen)
    val directionRows = Array(0, 1, 0, -1)
    val directionColumns = Array(1, 0, -1, 0)

    var visitedX = 0
    var visitedY = 0
    var direction = 0

    val total = rowsLen * columnsLen

    for (i <- 0 until total) {
      result.append(matrix(visitedX)(visitedY))
      if (i != total - 1) {
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

  /*
  # Algorithm

  A cursor starts off at (0, -1), i.e. the position at '0', follows "R>D>L>U" directions order.
  It makes use of a direction matrix that records the offset for all directions, then an array of two elements
  that store the number of shifts for horizontal and vertical movements.
  This way it avoids having a "visited matrix", additional loops etc.

  # Complexity Analysis:
  => Time Complexity: O(N), where N is the total number of elements in the input matrix.
     To print matrix in clockwise order, every element in the matrix is added to the result builder.

  => Auxiliary Space: O(1),
     - result StringBuilder to build the final answer (space complexity is O(n)).
*/

  def printClockwiseImproved(matrix: Array[Array[Int]]): String = {
    val rowsLen = matrix.length
    if (rowsLen == 0) return ""
    val columnsLen = matrix(0).length
    if (columnsLen == 0) return ""

    val result = new mutable.StringBuilder
    val directions = Array(Array(0, 1), Array(1, 0), Array(0, -1), Array(-1, 0))
    val stepRanges = Array(columnsLen, rowsLen - 1)
    var currentDirectionIndex = 0 // 0: RIGHT, 1: DOWN, 2: L, 3: UP
    var row = 0
    var column = -1 // start position

    val total = rowsLen * columnsLen
    var counter = 0

    while (stepRanges(currentDirectionIndex % 2) != 0) {

      for (_ <- 0 until stepRanges(currentDirectionIndex % 2)) {
        row += directions(currentDirectionIndex)(0)
        column += directions(currentDirectionIndex)(1)

        result.append(matrix(row)(column))
        if (counter != total - 1) {
          result.append(", ")
        }

        counter = counter + 1
      }

      stepRanges(currentDirectionIndex % 2) -= 1
      currentDirectionIndex = (currentDirectionIndex + 1) % 4
    }

    result.toString
  }
}
