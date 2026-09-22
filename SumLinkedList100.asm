add 12 r2
leftshift 4 r2
add 8 r2
add 1 r3
add 12 r4
leftshift 3 r4
add 4 r4
add 1 r4
copy r2 r7
add 0 r0
copy r3 r6
subtract r2 r6
store r3 r6
copy r2 r8
add 2 r8
copy r2 r9
add 1 r9
copy r8 r6
subtract r9 r6
store r8 r6
add 2 r2
add 1 r3
compare r3 r4
blt 5
subtract 1 r2
store r5 r2
copy r7 r6
load 0 r6
add r6 r1
copy r7 r9
add 1 r9
load 0 r9
copy r9 r7
compare r7 r5
bne 13
syscall
halt
