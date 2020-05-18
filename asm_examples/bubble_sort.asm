;Bubble_Sort
.data
str0:	.asciz	"Please input how many number to be sorted:\n"
str1:	.asciz	"Please input each number:\n"
str2:	.asciz	"The data sorted are:"
space:  .asciz  " "
	.align	#2
data:	.zero	#1		
.text

;input
get_array:
	mov r2, #4		; specify Print String service
	mov r4, str0		; set r4 to the address of the string which would be printed.
	syscall
	
	;input
	mov r2, #5		; input the amount of the numbers
	syscall				;Note: the syscall is the same as MARS, not follow ARM standard for register-use
	
	add	r6, r2, #0			; loop counter
	mov r7, #0
	mov r2, #4		; specify Print String service	
	mov r4, str1		; set r4 to the address of the string which would be printed.
	syscall				;Note: the syscall is the same as MARS, not follow ARM standard for register-use
	
	mov r1, data     ; set r4 to the address of data.
 
; input and store the data into memory
loop_store_data:
	cmp r7,r6					
	beq loop_bubble_sort	; if r7 == r6, jump out of the loop
	mov r2, #5				; specify Print String service	
	syscall
	str r2, [r1, #4]!			; store the data in r2, which is readed by "syscall",  to the address of data, which is stored in r1.
	add r7,r7,#1				; r7 = r7 +1 , update loop counter
	b loop_store_data		; do the loop


;bubble sort section
loop_bubble_sort:
	eor r7,r7,r7				; set r7 to 0. r7 is the counter of the outside loop
	add r7,r7,#1				; r7 = r7 + 1

loop_outside:
	mov r8, #1				; set r8 to 1. r7 is the counter of the outside loop	
	mov r9,data				; set r9 to the initial address of data
	 
loop_inner:
	ldr, r10,[r9,#0]			; read the data from the address which was stored in r9, and then set it to r10
	ldr, r11,[r9,#4]			; read next data and set it to r11
	sub r3, r10 ,r11			; Subtract r11 from r10, and then set it to r3. If the result is less than zero, the N in CPSR will be set to 1.
	blt switch_done			; jump to switch_done if r10 < r11	
	; swap the data in memory
	str r10,[r9,#4]			 
	str r11,[r9,#0]

switch_done:
	add r9, r9, #4			
	add r8, r8, #1			; r8 = r8 +1 , update loop counter
	
	cmp r8,r6					
	beq loop_inner_done  ; if r8 == r6, jump to the label: loop_inner_done
	b loop_inner				; else do the loop_inner


			
loop_inner_done:
	add r7,r7,#1				; r7 = r7 +1 , update loop counter
	cmp r7,r6					
	beq loop_outside_done
	b loop_outside

loop_outside_done:
	eor r7,r7,r7		; srt r7 to zero, and use r7 as the counter in the print loop

print_result:
	mov r2, #4		; specify Print String service
	mov r4, str2		; set r4 to the address of the string which would be printed.
	syscall
	mov r9, data
loop_print:
	mov r2, #1		; specify Print String service
	ldr r3,[r9,#0]
	add r9,r9,#4
	mov r4, r3		; set r4 to the address of the string which would be printed.
	syscall	
	
	mov r2, #4		; specify Print String service
	mov r4, space		; set r4 to the address of the string which would be printed.
	syscall	
	
	add r7,r7,#1		; r7 = r7 +1 , update loop counter
	cmp r7,r6
	beq print_end

	b loop_print
 
	
	
print_end:
	eor r0,r0,r0
	
	
	
