# FAST-Estonian-ID-code-sorting-algorithm

An "in-place" sorting algorithm for Estonian ID-codes. Uses radix with counting sort and many optimizations.

## sort()
The first number in an Estonian ID code symbolises the gender and the century of birth:
  1. (1, 2) - Male, Female, 19th century
  2. (3, 4) - Male, Female, 20th century
  3. (5, 6) - Male, Female, 21st century
  4. (7, 8) - Male, Female, 22nd century

<br>
ID codes are sorted by the following rules:

  1. The birthyear (the 1st, 2nd and 3rd numbers)
  2. The birth month (the 4th and 5th numbers)
  3. The birth day (the 6th and 7th numbers)
  4. The order number for people born on the same day (8th, 9th and 10th numbers)
  5. The control number, doesn't matter (the 11th number)
  6. The gender (the 1st number)

## Approach
Since counting sort becomes efficient when there are many duplicates, the naive approach would be to separate each digit of the ID code.


Turns out that in 10 million idCodes, there are way too many duplicates for each digit and grouping them is very computationally inefficient. It is much more efficient to take larger groups since in 10 million idCodes there are a lot of duplicates with larger groups.


Through testing 4 groups can be found:

  1. Birth year
  2. Birth month and day
  3. Order number
  4. Gender

Since gender is a single digit, a further optimization can be made, where we group it with one of the three.
<br>
Because gender is supposed to be the least significant modifier, just adding it to the end and grouping it with the order number is fitting.
<br>
<br>
There rises another problem with this approach, the century has to be calculated and then manually added to the 2nd and 3rd numbers (as an example, 399 and 499 are same birthyears, but would be sorted differently).
<br>
As we already have the first digit, we can calculate a century indicating number with values in 0...3 and add it to the 2nd and 3rd digit (with the previous example, we now have 199 and 199).
<br>
<br>
In reality we do not convert the ID code, but we can imagine these situations, because that is how we will be grouping the ID codes in the counting sort.
<br>
<br>
Finally we need to do the counting sort for each partition and since we do it for each partition, it is actually radix sort with counting sort.
<br>
<br>
Now we have three ranges:

  1. Birth year
  2. Birth month and day
  3. Order number with gender

## optimizations
  1. Finding the "exact" size for each counting array to avoid initialising more values than needed.
  2. Since counting in the first loop of counting sort is not affected by sorting and does not sort, we can count all 3 groups at the same time.
  3. The second optimization further optimises the code by only needing to separate the first digit once (for 1st and 3rd groups); without it, there would need to be 3 loops where we separate the first digit for the 1st and 3rd loops (twice).
  4. Using bitwise operations where possible (like calculating the century from the first digit)
  5. Instead of putting the counting sort last output into a separate array, it should be put straight into the initial array and copied from the last array.
