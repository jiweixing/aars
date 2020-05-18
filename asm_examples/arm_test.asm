.global main
main:

sub   r1, r3, #16 ;如果上一条比较结果是小于（查看CPSR），则将R0加1
sub   r0, r0, r1 ;如果上一条比较结果是小于（查看CPSR），则将R0加1
