# JOURNEY TO BECOME EPIC



## PRINTING MATRIX CLOCKWISE

### Solution 1 — INITIAL

The algorithm work like the below:

It holds a "visited matrix" to store visited information, so visited[index] denotes that the cell on the column was previously seen. 

Then it moves through the matrix and follows the "R>D>L>U" directions order, if the candidate is in the bounds of the matrix and unseen, it becomes the next position; otherwise, the next position is the one after performing a clockwise turn.

ℹ️ This algorithm is implemented — please see `MatrixUtils.printClockwiseInitial` in the __`ScalaClockwiseMatrix`)__ project

#### Complexity Analysis:

__Time Complexity:__ `O(N)`
where N is the total number of elements in the input matrix.
 
To print matrix in clockwise order, every element in the matrix is added to the result builder.

__Auxiliary Space:__ `O(N)`
- the information stored in "visited" array
- result StringBuilder to build the final answer (space complexity is O(n)).
  

### Solution 2 — IMPROVED

The algorithm work like the below:

A cursor starts off at (0, -1), i.e. the position at '0', follows "R>D>L>U" directions order. It makes use of a direction matrix that records the offset for all directions, then an array of two elements that store the number of shifts for horizontal and vertical movements. This way it avoids having a "visited matrix", additional loops.

ℹ️ This algorithm is implemented — please see `MatrixUtils.printClockwiseImproved` in the __`ScalaClockwiseMatrix`__ project

This solutuon is and improved version that avoids having a `visited` matrix so its space efficient as well.

#### Complexity Analysis

__Time Complexity:__ `O(N)`
where N is the total number of elements in the input matrix.
 
To print matrix in clockwise order, every element in the matrix is added to the result builder.

__Auxiliary Space:__ `O(1)`
- - result StringBuilder to build the final answer (space complexity is O(n)).
