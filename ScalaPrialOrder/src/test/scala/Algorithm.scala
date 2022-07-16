package org.github.journeytobecomeepic

import scala.collection.mutable

object Algorithm {
  /*
      Complexity Analysis:
      => Time Complexity: O(N), where N is the total number of elements in the input matrix.
         We add every element in the matrix to our final answer.

      => Auxiliary Space: O(N), the information stored in seen array and in result StringBuilder (space complexity is O(n)).
  */

  /*
  Solution should handle 1x1 matrix as well.
   */
  def clockwiseMatrix(matrix: Array[Array[Int]]): String = {
    if (matrix.length == 0) return ""
    val result = new mutable.StringBuilder

    // get rows and columns lengths
    val rowsLen = matrix.length
    val columnsLen = matrix(0).length
    val seen = Array.ofDim[Boolean](rowsLen, columnsLen)
    val directionRows = Array(0, 1, 0, -1)
    val directionColumns = Array(1, 0, -1, 0)
    var seenX = 0
    var seenY = 0
    var direction = 0

    // iterate from 0 to Rows * Columns - 1

    val iterationLimit = rowsLen * columnsLen

    for (i <- 0 until iterationLimit) {
      if (i == iterationLimit - 1) result.append(matrix(seenX)(seenY))
      else result.append(matrix(seenX)(seenY)).append(", ")
      seen(seenX)(seenY) = true

      val currentRow = seenX + directionRows(direction)
      val currentColumn = seenY + directionColumns(direction)
      if (0 <= currentRow
        && currentRow < rowsLen
        && 0 <= currentColumn
        && currentColumn < columnsLen
        && !seen(currentRow)(currentColumn)) {
        seenX = currentRow
        seenY = currentColumn
      } else {
        direction = (direction + 1) % 4
        seenX += directionRows(direction)
        seenY += directionColumns(direction)
      }
    }

    result.toString
  }
}
