#-----------------------------------------------------
# data segment - declare global data

.data
    intro:	         .asciiz "Christopher Thomas: Mips Program\n"
    promptN:         .asciiz "Enter Integer(max 19): "
    promptContinue:  .asciiz "<enter '6' to exit program>\n\tenter an Integer to continue: "
    promptPattern:   .asciiz "Starting Pattern\n1: The R-pentomino\n2: Diehard\n3: Acorn\nEnter Type(num): "
    promptPatternError:   .asciiz "Error reading in Pattern type input\n\n"
    newline:		.asciiz "\n"
    input:	.asciiz		"temp"
	  space:	.asciiz		" "

    univ: .space 804
    univTemp: .space 804

#-----------------------------------------------------
# text  segment - the instructions of the program

.text

# $s0 = n
# $s1 = n *10 or n length
# $s2 = univ
# $s3 = univTemp

#5x5
#  0  1  2  3  4
# 10 11 12 13 14
# 20 21 22 23 24
# 30 31 32 33 34
# 40 41 42 43 44

main:
 	li $v0, 4
  	la $a0, intro
  	syscall

	# printf("Enter Integer: ");
	#
	li $v0, 4
  	la $a0, promptN
 	syscall

	# scanf("%255s", s);
	#
	li $v0, 5
	syscall
	move $s0, $v0
	move $s1, $v0
	mult $s1, $s0
	mflo $s1

	la $s2, univ
	la $s3, univTemp


	#for(i=0;i<n;i++) {
    	#  for(j=0;j<n;j++) {
    	#    univ[i][j]=0;
    	#  }
    	#}
  	li $t0, 0
  	initUnivLoop:
  		beq $t0,$s1, afterInitUnivLoop
    		nop
    		#univ
    		li $t1,0
 		sll $t3, $t0, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)

 		#univTemp
 		sll $t3, $t0, 2
 		add $t3, $t3, $s3 # t3 = &($s0[$t2])
 		sb $t1, 0($t3)

 		addi $t0, $t0, 1
		j initUnivLoop
    		nop


  	afterInitUnivLoop:

  	#int type;
  	#printf("Starting Pattern\n");
  	#printf("1: The R-pentomino\n");
  	#printf("2: Diehard\n");
  	#printf("3: Acorn\n");
  	#printf("input: ");
  	#scanf("%d", &type);s

  	li $t1, 1
	li $t2, 2
	li $t3, 3

	li $v0, 4
  	la $a0, promptPattern
 	syscall

	# scanf("%255s", s);
	#
	li $v0, 5
	syscall
	move $t0, $v0

	# if type =1 or =2 or =3
	beq $t0, $t1, setPattern1
	nop
	beq $t0, $t2, setPattern2
	nop
	beq $t0, $t3, setPattern3
	nop

	li $v0, 4
  	la $a0, promptPatternError
 	syscall
  	j afterInitUnivLoop
  	nop

  	#The R-pentomino
  	setPattern1:
  	li $t0, 2
    	div $s1, $t0
    	mflo $t0 #center
    	mfhi $t6
    	li $t1,1 #value
    	li $t9,9 #value center

    	bne $t6, $zero, skipP1EvenFix
    	nop
    	li $t6, 2
    	div $s0, $t6
    	mflo $t6 #center
    	add $t0, $t0, $t6
    	subi $t0,$t0, 1

    	skipP1EvenFix:

  		#univ
  		addi $t2,$t0,0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
 		#univ
 		addi $t2,$t0,-1
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
 		#univ
 		sub $t2,$t0,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ
    		add $t2,$t0,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ
    		subi $t4, $s0, 1
    		sub $t2,$t0,$t4
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)

  		j afterSetPattern
  		nop


  	#Diehard
  	setPattern2:
	li $t0, 2
    	div $s1, $t0
    	mflo $t0 #center
    	mfhi $t6
    	li $t1,1 #value
    	li $t9,9 #value center

    	bne $t6, $zero, skipP2EvenFix
    	nop
    	li $t6, 2
    	div $s0, $t6
    	mflo $t6 #center
    	add $t0, $t0, $t6
    	subi $t0,$t0, 1

    	skipP2EvenFix:

		#univ[center-3][center]=1;
		addi $t2,$t0,-3
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center-2][center]=1;
    		addi $t2,$t0,-2
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center-2][center+1]=1;
    		subi $t4, $t0, 2
    		add $t2,$t4,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+2][center+1]=1;
    		addi $t2,$t0,2
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+3][center+1]=1;
    		addi $t2,$t0,3
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+3][center-1]=1;
    		addi $t2,$t0,3
    		sub $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+4][center+1]=1;
    		addi $t2,$t0,4
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)

  		j afterSetPattern
  		nop

  	#Acorn
  	setPattern3:
  	li $t0, 2
    	div $s1, $t0
    	mflo $t0 #center
    	mfhi $t6
    	li $t1,1 #value
    	li $t9,9 #value center

    	bne $t6, $zero, skipP3EvenFix
    	nop
    	li $t6, 2
    	div $s0, $t6
    	mflo $t6 #center
    	add $t0, $t0, $t6
    	subi $t0,$t0, 1

    	skipP3EvenFix:

  		#univ[center-3][center+1]=1;
  		addi $t2,$t0,-3
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center-2][center-1]=1;
    		addi $t2,$t0,-2
    		sub $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center-2][center+1]=1;
    		addi $t2,$t0,-2
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center][center]=1;
    		addi $t2,$t0,0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+1][center+1]=1;
    		addi $t2,$t0,1
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+2][center+1]=1;
    		addi $t2,$t0,2
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)
    		#univ[center+3][center+1]=1;
    		addi $t2,$t0,3
    		add $t2,$t2,$s0
 		sll $t3, $t2, 2
 		add $t3, $t3, $s2
 		sb $t1, 0($t3)

  		j afterSetPattern
  		nop

  	afterSetPattern:

  	# $s0 = n
	# $s1 = n *10 or n length
	# $s2 = univ
	# $s3 = univTemp

  	theGameOfLife:
  		#int univ2[n][n];
    		#printf("\n");
    		#for(i=0;i<n;i++) {
    		#  for(j=0;j<n;j++) {
    		#    univ2[j][i]=univ[j][i];
    		#    int output = univ[j][i];
    		#    printf("%d",output);
    		#  }
    		#  printf("\n");
    		#}
    		li $v0, 4
    		la $a0, newline
    		syscall

    		li $t0, 0 #i
 		forLoop1:
 			beq $t0, $s1, afterLoop1
 			nop

 			#print new line
 			div $t0, $s0
    			mfhi $t5
    			bne $t5, $zero, skipNewline
    			nop
    			beq $t0, $zero, skipNewline
    			nop
    			li $v0, 4
    			la $a0, newline
    			syscall
    			skipNewline:


 			# Get index at univ
 			sll $t1, $t0, 2
 			add $t1, $t1, $s2
 			lb $t2, 0($t1)
 			#save index to univtemp
 			sll $t4, $t0, 2
 			add $t4, $t4, $s3
 			sb $t2, 0($t4)

 			li $v0, 1
    			move $a0, $t2
    			syscall

    			addi $t0, $t0, 1
    			j forLoop1
    			nop

  		afterLoop1:
  		li $v0, 4
    		la $a0, newline
    		syscall

    		li $t0, 0
    		li $t9, 0 #compare
    		updateLoop:
    			beq $t0, $s1, doneUpdating
 			nop
 			li $s4,0


 			ifI:
 			div $t0, $s0
 			mfhi $t9
 			bnez $t9, elseIfI
 			nop
 				ifJ1:
 				bge $t0, $s0, elseIfJ1
 				nop
 					#if(univ[j+1][i]==1) count++;
 					addi $t1,$t0,1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next1
 					nop
 					addi $s4,$s4,1
 					next1:
            				#if(univ[j+1][i+1]==1) count++;
            				addi $t1,$t0,1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next2
 					nop
 					addi $s4,$s4,1
 					next2:
            				#if(univ[j][i+1]==1) count++;
    					add $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next3
 					nop
 					addi $s4,$s4,1
 					next3:
    					syscall
 					j check1
 					nop

 				elseIfJ1:
 				sub $t9,$s1,$s0
 				blt $t0, $t9, elseJ1
 				nop
 					#if(univ[j+1][i]==1) count++;
 					addi $t1,$t0,1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next4
 					nop
 					addi $s4,$s4,1
 					next4:
            				#if(univ[j+1][i-1]==1) count++;
            				addi $t1,$t0,1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next5
 					nop
 					addi $s4,$s4,1
 					next5:
            				#if(univ[j][i-1]==1) count++;
    					sub $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next6
 					nop
 					addi $s4,$s4,1
 					next6:
 					j check1
 					nop
 				elseJ1:
 					#if(univ[j][i-1]==1) count++;
    					sub $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next7
 					nop
 					addi $s4,$s4,1
 					next7:
            				#if(univ[j+1][i-1]==1) count++;
            				addi $t1,$t0,1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next8
 					nop
 					addi $s4,$s4,1
 					next8:
            				#if(univ[j+1][i]==1) count++;
            				addi $t1,$t0,1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next9
 					nop
 					addi $s4,$s4,1
 					next9:
            				#if(univ[j+1][i+1]==1) count++;
            				addi $t1,$t0,1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next10
 					nop
 					addi $s4,$s4,1
 					next10:
            				#if(univ[j][i+1]==1) count++;
    					add $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next11
 					nop
 					addi $s4,$s4,1
 					next11:
 					j check1
 					nop
 			elseIfI:
 			move $t9, $t0
 			addi $t9,$t9,1
 			div $t9, $s0
 			mfhi $t9
 			bnez $t9, elseI

 				ifJ2:
 				bge $t0, $s0, elseIfJ2
 				nop
 					#if(univ[j-1][i]==1) count++;
 					addi $t1,$t0,-1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next12
 					nop
 					addi $s4,$s4,1
 					next12:
            				#if(univ[j-1][i+1]==1) count++;
            				addi $t1,$t0,-1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next13
 					nop
 					addi $s4,$s4,1
 					next13:
            				#if(univ[j][i+1]==1) count++;
    					add $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next14
 					nop
 					addi $s4,$s4,1
 					next14:
 					j check1
 					nop
 				elseIfJ2:
 				sub $t9,$s1,$s0
 				blt $t0, $t9, elseJ2
 				nop
 					#if(univ[j-1][i]==1) count++;
 					addi $t1,$t0,-1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next15
 					nop
 					addi $s4,$s4,1
 					next15:
            				#if(univ[j-1][i-1]==1) count++;
            				addi $t1,$t0,-1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next16
 					nop
 					addi $s4,$s4,1
 					next16:
            				#if(univ[j][i-1]==1) count++;
    					sub $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next17
 					nop
 					addi $s4,$s4,1
 					next17:
 					j check1
 					nop
 				elseJ2:
 					#if(univ[j][i-1]==1) count++;
    					sub $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next18
 					nop
 					addi $s4,$s4,1
 					next18:
            				#if(univ[j-1][i-1]==1) count++;
            				addi $t1,$t0,-1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next19
 					nop
 					addi $s4,$s4,1
 					next19:
            				#if(univ[j-1][i]==1) count++;
            				addi $t1,$t0,-1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next20
 					nop
 					addi $s4,$s4,1
 					next20:
            				#if(univ[j-1][i+1]==1) count++;
            				addi $t1,$t0,-1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next21
 					nop
 					addi $s4,$s4,1
 					next21:
            				#if(univ[j][i+1]==1) count++;
    					add $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next22
 					nop
 					addi $s4,$s4,1
 					next22:
 					j check1
 					nop
 			elseI:
 				ifJ3:
 				bge $t0, $s0, elseIfJ3
 				nop
 					#if(univ[j-1][i]==1) count++;
 					addi $t1,$t0,-1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next23
 					nop
 					addi $s4,$s4,1
 					next23:
            				#if(univ[j-1][i+1]==1) count++;
            				addi $t1,$t0,-1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next24
 					nop
 					addi $s4,$s4,1
 					next24:
            				#if(univ[j][i+1]==1) count++;
    					add $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next25
 					nop
 					addi $s4,$s4,1
 					next25:
            				#if(univ[j+1][i+1]==1) count++;
            				addi $t1,$t0,1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next26
 					nop
 					addi $s4,$s4,1
 					next26:
            				#if(univ[j+1][i]==1) count++;
            				addi $t1,$t0,1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next27
 					nop
 					addi $s4,$s4,1
 					next27:
 					j check1
 					nop
 				elseIfJ3:
 				sub $t9,$s1,$s0
 				blt $t0, $t9, elseJ3
 				nop
 					#if(univ[j-1][i]==1) count++;
 					addi $t1,$t0,-1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next28
 					nop
 					addi $s4,$s4,1
 					next28:
            				#if(univ[j-1][i-1]==1) count++;
            				addi $t1,$t0,-1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next29
 					nop
 					addi $s4,$s4,1
 					next29:
            				#if(univ[j][i-1]==1) count++;
    					sub $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next282
 					nop
 					addi $s4,$s4,1
 					next282:
            				#if(univ[j+1][i-1]==1) count++;
            				addi $t1,$t0,1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next292
 					nop
 					addi $s4,$s4,1
 					next292:
            				#if(univ[j+1][i]==1) count++;
            				addi $t1,$t0,1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next30
 					nop
 					addi $s4,$s4,1
 					next30:
 					j check1
 					nop
 				elseJ3:
 					#if(univ[j-1][i-1]==1) count++;
 					addi $t1,$t0,-1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next31
 					nop
 					addi $s4,$s4,1
 					next31:
            				#if(univ[j-1][i]==1) count++;
            				addi $t1,$t0,-1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next32
 					nop
 					addi $s4,$s4,1
 					next32:
            				#if(univ[j-1][i+1]==1) count++;
            				addi $t1,$t0,-1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next322
 					nop
 					addi $s4,$s4,1
 					next322:
            				#if(univ[j][i-1]==1) count++;
    					sub $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next33
 					nop
 					addi $s4,$s4,1
 					next33:
            				#if(univ[j][i+1]==1) count++;
    					add $t1,$t0,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next34
 					nop
 					addi $s4,$s4,1
 					next34:
            				#if(univ[j+1][i-1]==1) count++;
            				addi $t1,$t0,1
    					sub $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next35
 					nop
 					addi $s4,$s4,1
 					next35:
            				#if(univ[j+1][i]==1) count++;
            				addi $t1,$t0,1
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next36
 					nop
 					addi $s4,$s4,1
 					next36:
            				#if(univ[j+1][i+1]==1) count++;
            				addi $t1,$t0,1
    					add $t1,$t1,$s0
 					sll $t1, $t1, 2
 					add $t1, $t1, $s2
 					lb $t2, 0($t1)
 					li $t9, 1
 					bne $t2, $t9, next37
 					nop
 					addi $s4,$s4,1
 					next37:

 			check1:
 				li $t9, 2
 				bge $s4, $t9, check12
 				nop
 				sll $t1, $t0, 2
 				add $t1, $t1, $s2
 				lb $t2, 0($t1)
 				li $t9, 1
 				bne $t2, $t9, check12
 				nop
 				li $t9, 0
 				sll $t4, $t0, 2
 				add $t4, $t4, $s3
 				sb $t9, 0($t4)
 				j checkDone
 				nop

 			check12:
 				li $t9, 2
 				blt $s4, $t9, check2
 				nop
 				li $t9, 3
 				bgt $s4, $t9, check2
 				nop
 				sll $t1, $t0, 2
 				add $t1, $t1, $s2
 				lb $t2, 0($t1)
 				li $t9, 1
 				bne $t2, $t9, check2
 				nop
 				li $t9, 1
 				sll $t4, $t0, 2
 				add $t4, $t4, $s3
 				sb $t9, 0($t4)
 				j checkDone
 				nop

 			check2:
 				li $t9, 3
 				ble $s4, $t9, check3
 				nop
 				sll $t1, $t0, 2
 				add $t1, $t1, $s2
 				lb $t2, 0($t1)
 				li $t9, 1
 				bne $t2, $t9, check3
 				nop
 				li $t9, 0
 				sll $t4, $t0, 2
 				add $t4, $t4, $s3
 				sb $t9, 0($t4)
 				j checkDone
 				nop

 			check3:
 				li $t9, 3
 				bne $s4, $t9, checkDone
 				nop
 				sll $t1, $t0, 2
 				add $t1, $t1, $s2
 				lb $t2, 0($t1)
 				li $t9, 0
 				bne $t2, $t9, checkDone
 				nop
 				li $t9, 1
 				sll $t4, $t0, 2
 				add $t4, $t4, $s3
 				sb $t9, 0($t4)

 			checkDone:

    			addi $t0, $t0, 1
    			j updateLoop
    			nop

    		doneUpdating:





    		updateUnivs:
    			#for(i=0;i<n;i++) {
      			# for(j=0;j<n;j++) {
        		# univ[j][i]=univ2[j][i];
      			# }
    			#}
    			li $t0, 0 #i
 			forLoopUS:
 				beq $t0, $s1, continue
 				nop

 				# Get index at univTemp
 				sll $t1, $t0, 2
 				add $t1, $t1, $s3
 				lb $t2, 0($t1)
 				#save index to univ
 				sll $t4, $t0, 2
 				add $t4, $t4, $s2
 				sb $t2, 0($t4)

    				addi $t0, $t0, 1
    				j forLoopUS
    				nop

    		continue:
    			li $v0, 4
    			la $a0, promptContinue
    			syscall

    			li $v0, 5
			syscall

			li $s4, 6
			beq $v0, $s4, theEnd
			nop

			li $s4, 66
			beq $v0, $s4, main
			nop

			j theGameOfLife

  theEnd:
  li $v0, 10
  syscall
