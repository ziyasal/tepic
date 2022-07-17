# JOURNEY TO BECOME EPIC



## PRINTING MATRIX CLOCKWISE

### Solution 1 — Simulation algorithm

ℹ️ This algorithm is implemented. 

#### Complexity Analysis:

__Time Complexity:__ `O(N)`
where N is the total number of elements in the input matrix.
 
To print matrix in clockwise order, every element in the matrix is added to the result builder.

__Auxiliary Space:__ `O(N)`
- the information stored in "visited" array
- result StringBuilder to build the final answer (space complexity is O(n)).
  

### Solution 2
This solutuon is space effcient. So for large matrix, if thereis mem constraints this solution would be convenient.

#### Complexity Analysis

__Time Complexity:__ `O(MxN)`

__Auxiliary Space:__ `O(1)`
