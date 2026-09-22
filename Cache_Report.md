
ICSI 404 Assignment 6 Cache Report

For this assignment, I measured performance by keeping track of currentClockCycle inside the processor. The processor adds cycle costs for ALU operations and also adds the cost of instruction and memory accesses through the instruction cache and L2 cache. My final design uses an 8-word instruction cache and a 4-line L2 cache. For data memory, load and store go through L2, and stores use  a write-through policy.

I tested three different assembly programs. The first program adds the numbers from 1 to 100 using a loop and does not rely much on data memory. The second program stores 100 integers in an array in memory and then loops through the array to add them together. The third program creates a linked list of 100 integers in memory and then walks through the list to find the total.

These were the cycle counts I got:

Sum from 1 to 100 in a loop: 2998 cycles
Sum of 100 integers in an array: 27948 cycles
Sum of 100 integers in a linked list: 59792 cycles
The loop-only program had the lowest cycle count, which makes sense because it mostly reuses the same small group of instructions and does not need much data memory access. After the first cache misses, the instruction cache helps the loop run more efficiently.

The array program took more cycles because it has to read and write data in memory, but it still performed much better than the linked list. Since array elements are stored next to each other, the cache can take advantage of spatial locality. In other words, once one memory location is loaded, nearby values are likely to be useful soon after.

The linked-list program had the highest cycle count. This is expected because linked lists do not have the same memory locality as arrays. Each step depends on following a pointer to another location, so the accesses are less predictable and the cache is less helpful.

   Overall, the results show that the cache hierarchy improves processor performance, especially for programs with repeated instruction use and nearby memory accesses. The instruction cache helps loops, and the L2 cache reduces the cost of both instruction misses and data accesses. The linked-list test also shows that caches work best when memory is accessed in a more regular pattern.
