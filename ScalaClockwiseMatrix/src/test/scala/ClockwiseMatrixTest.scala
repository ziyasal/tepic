package org.github.journeytobecomeepic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ClockwiseMatrixTest extends AnyFlatSpec with Matchers {

  "An empty matrix" should "print an empty string" in {
    val expected = ""
    val testCase = Array.ofDim[Int](0, 0)

    val actual = MatrixUtils.printClockwiseImproved(testCase)

    assert(actual == expected)
  }


  "A matrix with single row and single column with single item (1X1 Matrix)" should "print the single item" in {
    val expected = "2"
    val testCase = Array(Array(2))

    val actual = MatrixUtils.printClockwiseImproved(testCase)

    assert(actual == expected)
  }

  "A matrix with single row (row vectors)" should "print the row items" in {
    val expected = "2, 3, 4, 8"
    val testCase = Array(Array(2, 3, 4, 8))

    val actual = MatrixUtils.printClockwiseImproved(testCase)

    assert(actual == expected)
  }

  "A matrix with single column" should "print the column items" in {
    val expected = "2, 5, 1"
    val testCase = Array(
      Array(2),
      Array(5),
      Array(1)
    )

    val actual = MatrixUtils.printClockwiseImproved(testCase)

    assert(actual == expected)
  }

  "A matrix with multiple row and multiple column (infinite matrix)" should "print all items" in {
    val expected = "2, 3, 4, 8, 12, 10, 6, 0, 1, 5, 7, 9"
    val testCase = Array(
      Array(2, 3, 4, 8),
      Array(5, 7, 9, 12),
      Array(1, 0, 6, 10)
    )

    val actual = MatrixUtils.printClockwiseImproved(testCase)

    assert(actual == expected)
  }
}
