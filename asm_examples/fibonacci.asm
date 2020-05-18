.data
	fibs:	.word	#0 : #12		; "array" of 12 words to contain fib values
	header: .asciz "This is the result of fibonacci: \n"
	space:  .asciz  " "
.text				;define the text section
	; initalize the fibs  
	mov a1, #9 	;the value n		; r0 = 9 ,this is the iteration nums of fibonacc
	eor fp, fp, fp				; set r11 to 0, r11 represents the sum of the fibonacci array
	mov v4, #1				; set r7 to 1, r7 represents i, the loop counter
	mov v5, #0				; set r8 to 0, r8 represents fibs[i]
	mov v6, #1				; set r9 to 1, r9 represents fibs[i+1]
	mov sl,#0			        ; set r10 to 0, r10 represents the result of fibs[i+2]
	mov fp, r9
	;print header
	;Note: the syscall is the same as MARS, not follow ARM standard for register-use
	mov r2, #4		; specify Print String service
	mov r4, header		; set r4 to the address of the string which would be printed.
	syscall
	
	mov r5,fibs 
	str r8, [r5]
	str r9, [r5,#4]!



loop:
	cmp r7,r0		; loop while r7 < r0
	beq loopexit		; if r7 >=r0 , the 'Z' in CPSR will be set to 1, and the procedure will jump to loopexit 
	add r10,r8,r9		; r10 = r8 + r9, calculate the fibonacci
	push {r10}		; Just show how to push. U can choose '.heap' in Data Segent to see the stack.
	str r10, [r5,#4]!	;store F[i+2]
	;add r5, r5, #4
	
	;print fibonacci number
	mov v1, r10	; specify Print Integer service
	mov r2, #1 
	syscall
	
	;print ' '
	mov r2, #4	; specify Print String service
	mov r4, space
	syscall
	
	mov r8,r9				; r8 = r9
	mov r9,r10				; r9 = r10
	add r7,r7,#1				; r7 = r7 +1 , update loop counter
	add r11,r11,r10				; r11 = r11 + r10  

b loop

loopexit:
 	;clean up the registers
	mov  r0,  #0				 
	mov  r1,  #0
	mov  r2,  #0
	mov  r3,  #0
	mov  r4,  #0
	mov  r5,  #0
	mov  r6,  #0
	mov  r7,  #0
	mov  r8,  #0
	mov  r9,  #0
	mov  r10, #0
	mov  r11, #0
	mov  r12, #0
