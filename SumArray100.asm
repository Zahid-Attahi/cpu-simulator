add 12 r7
leftshift 4 r7
add 7 r7
add 12 r4
leftshift 3 r4
add 4 r4
add 1 r4
add 1 r3
add 12 r2
leftshift 4 r2
add 8 r2
add 12 r8
leftshift 3 r8
add 4 r8
store r3 r7
add 1 r3
compare r3 r4
blt 7
copy r2 r9
load 0 r9
add r9 r1
add 1 r2
add 1 r5
compare r5 r8
blt 9
syscall
halt
