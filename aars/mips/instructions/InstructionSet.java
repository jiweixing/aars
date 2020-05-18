package aars.mips.instructions;

import aars.simulator.*;
import aars.mips.hardware.*;
import aars.mips.instructions.syscalls.*;
import aars.*;

import java.util.*;
import java.io.*;
	
	/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/**
 * The list of Instruction objects, each of which represents a MIPS instruction.
 * The instruction may either be basic (translates into binary machine code) or
 * extended (translates into sequence of one or more basic instructions).
 *
 * @author Pete Sanderson and Ken Vollmar
 * @version August 2003-5
 */

public class InstructionSet {
    private ArrayList instructionList;
    private ArrayList opcodeMatchMaps;
    private SyscallLoader syscallLoader;

    /**
     * Creates a new InstructionSet object.
     */
    public InstructionSet() {
        instructionList = new ArrayList();

    }

    /**
     * Retrieve the current instruction set.
     */
    public ArrayList getInstructionList() {
        return instructionList;

    }

    /**
     * Adds all instructions to the set.  A given extended instruction may have
     * more than one Instruction object, depending on how many formats it can have.
     *
     * @see Instruction
     * @see BasicInstruction
     * @see ExtendedInstruction
     */
    public void populate() {
        /* Here is where the parade begins.  Every instruction is added to the set here.*/
        instructionList.add(
            new BasicInstruction("mov r0, r1", 
                    "MOV (Move) writes a value to the destination register. The value can be either an immediate value or a value from a register, and can be shifted before the write.",
                    BasicInstructionFormat.I_FORMAT,
                    "1110 00101001 ssss ffff 000000000000", 
                 
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int num = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[0], num);
                                            }
                                        }));
        
        instructionList.add(
            new BasicInstruction("mov r0, #-112", 
            "MOV (Move) writes a value to the destination register. The value can be either an immediate value or a value from a register, and can be shifted before the write.",
                    BasicInstructionFormat.I_FORMAT,
                    "1110 00111011 0000 ssss 0000 ffffffff", 
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int num = operands[1];
                        RegisterFile.updateRegister(operands[0], num);
                                            }
                                        }));
        instructionList.add(
            new BasicInstruction("mov r1,#100020",
            "MOV (Move) writes a value to the destination register. The value can be either an immediate value or a value from a register, and can be shifted before the write.",
                    BasicInstructionFormat.U_FORMAT,
                    "ssssssssssssssssssss fffff 01101 11",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            RegisterFile.updateRegister(operands[0], operands[1] << 12);
                        }
                    }));
        instructionList.add(
            new BasicInstruction("mov r1,label",
            "MOV (Move) writes a value to the destination register. The value can be either an immediate value or a value from a register, and can be shifted before the write.",
                    BasicInstructionFormat.U_FORMAT,
                    "ssssssssssssssssssss fffff 01101 11",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            RegisterFile.updateRegister(operands[0], operands[1]);
                        }
                    }));


        instructionList.add(
            new BasicInstruction("mvn r0, r1", 
                    "MVN (Move Not) generates the logical ones complement of a value. The value can be either an immediate value or a value from a register, and can be shifted before the MVN operation.",
                    BasicInstructionFormat.I_FORMAT,
                     "1110 00011111 0000 ssss 00000000 ffff",
                    //1110 00011111 SBZ  Rd   00000000 Rm 
                    
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int num = RegisterFile.getValue(operands[1]);
                        num = num * (-1) - 1;
                        RegisterFile.updateRegister(operands[0], num);
                                            }
                                        }));
        
        instructionList.add(
            new BasicInstruction("mvn r0, #-112", 
            "MVN (Move Not) generates the logical ones complement of a value. The value can be either an immediate value or a value from a register, and can be shifted before the MVN operation.",
                     BasicInstructionFormat.I_FORMAT,
                    "1110 00011111 0000 ssss 00000000 ffff",
                    //1110 00011111 SBZ  Rd   00000000 Rm 
                        
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int num = operands[1];
                        num = num * (-1) - 1;
                        RegisterFile.updateRegister(operands[0], num);
                                            }
                                        }));
                                                           
        
         instructionList.add(
            new BasicInstruction("cmp r0, r1", 
           "CMP (2) compares two register values. The condition code flags are updated, based on the result of subtracting the second register value from the first, so that subsequent instructions can be conditionallyexecuted (using a conditional branch)."     ,               
                    BasicInstructionFormat.I_FORMAT,
                    "0000 00 0 1010 1 ssss                  0000  ffffffffffff",
                   //cond 00 I 1010 1 Rn(the first operand) SBZ   shifter_operand
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int cp1 = RegisterFile.getValue(operands[0]);
                        int cp2 = RegisterFile.getValue(operands[1]);
                            if(cp1 == cp2){
                                RegisterFile.set_CPSR_Z();
                            }
                            else{
                                    RegisterFile.reset_CPSR_Z();
                                }
                        }
                    }));

        instructionList.add(
            new BasicInstruction("cmp r0, #-112", 
            "CMP (1) (Compare) compares a register value with a large immediate value. The condition flags are updated, based on the result of subtracting the constant from the register value, so that subsequent instructions can be conditionally executed (using a conditional branch).",
            BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 1010 1 ssss                  0000  ffffffffffff", 
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int cp1 = RegisterFile.getValue(operands[0]);
                        int cp2 = operands[1];
                            if(cp1 == cp2){
                                RegisterFile.set_CPSR_Z();
                            }
                            else{
                                    RegisterFile.reset_CPSR_Z();
                                }
                        }
                }));

        instructionList.add(
            new BasicInstruction("add r0,r1,#-112",
                    "ADD (1) adds a small constant value to the value of a register and stores the result in a second register.It updates the condition code flags, based on the result.",
                    BasicInstructionFormat.R_FORMAT,
                    "1110 00 0 0100 1 tttt ssss ffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            int add1 = RegisterFile.getValue(operands[1]);
                            int add2 = operands[2];
                            int sum = add1 + add2;
                            RegisterFile.updateRegister(operands[0], sum);
                        }
                    }));
        instructionList.add(
            new BasicInstruction("add r0,#-112",
                    "ADD (2) adds a large immediate value to the value of a register and stores the result back in the same register.The condition code flags are updated, based on the result.",
                    BasicInstructionFormat.R_FORMAT,
                    "1110 00101001  ssss ssss 0000 ffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            int add1 = RegisterFile.getValue(operands[0]);
                            int add2 = operands[1];
                            int sum = add1 + add2;
                            RegisterFile.updateRegister(operands[0], sum);
                        }
                    }));
        instructionList.add(
                new BasicInstruction("add r0,r1,r2",
                        "ADD (3) adds the value of one register to the value of a second register, and stores the result in a third register.It updates the condition code flags, based on the result.",
                        BasicInstructionFormat.R_FORMAT,
                        "1110 00 0 0100 1 tttt ssss ffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[1]);
                                int add2 = RegisterFile.getValue(operands[2]);
                                int sum = add1 + add2;
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));
        instructionList.add(
                new BasicInstruction("sub r0,r1,#-112",
                        "SUB (1) (Subtract) subtracts a small constant value from the value of a register and stores the result in a second register.",
                        BasicInstructionFormat.R_FORMAT,
                        "1110 00 0 0010 1  tttt ssss ffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int sub1 = RegisterFile.getValue(operands[1]);
                                int sub2 = operands[2];
                                int dif = sub1 - sub2;
                                if(dif<0)
                                {
                                    RegisterFile.set_CPSR_N();
                                }
                                else{
                                    RegisterFile.reset_CPSR_N();
                                }
                                RegisterFile.updateRegister(operands[0], dif);
                            }
                        }));
            instructionList.add(
                new BasicInstruction("sub r0,#-112",
                        "SUB (2) subtracts a large immediate value from the value of a register and stores the result back in the same register.",
                        BasicInstructionFormat.R_FORMAT,
                        "1110 0010 0101  ssss ssss 0000 ffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int sub1 = RegisterFile.getValue(operands[0]);
                                int sub2 = operands[1];
                                int dif = sub1 - sub2;
                                if(dif<0)
                                {
                                    RegisterFile.set_CPSR_N();
                                }
                                else{
                                    RegisterFile.reset_CPSR_N();
                                }
                                RegisterFile.updateRegister(operands[0], dif);
                            }
                        }));
          instructionList.add(
                new BasicInstruction("sub r0,#-112",
                        "SUB (2) subtracts a large immediate value from the value of a register and stores the result back in the same register.",
                        BasicInstructionFormat.R_FORMAT,
                        "1110 0010 0101  ssss ssss 0000 ffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int sub1 = RegisterFile.getValue(operands[0]);
                                int sub2 = operands[1];
                                int dif = sub1 - sub2;
                                if(dif<0)
                                {
                                    RegisterFile.set_CPSR_N();
                                }
                                else{
                                    RegisterFile.reset_CPSR_N();
                                }
                                RegisterFile.updateRegister(operands[0], dif);
                            }
                        }));
        instructionList.add(
            new BasicInstruction("sub r0,r1,r2",
                    "SUB (3) subtracts the value of one register from the value of a second register and stores the result in a third register.",
                    BasicInstructionFormat.R_FORMAT,
                    "1110 00 0 0010 1  tttt ssss ffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            int sub1 = RegisterFile.getValue(operands[1]);
                            int sub2 = RegisterFile.getValue(operands[2]);
                            int dif = sub1 - sub2;
                            if(dif<0)
                                {
                                    RegisterFile.set_CPSR_N();
                                }
                            else{
                                RegisterFile.reset_CPSR_N();
                            }
                            RegisterFile.updateRegister(operands[0], dif);
                        }
                    }));
        instructionList.add(
            new BasicInstruction("str r11,[r0,#112]",
                    "STR (Store Register) stores a word from a register to memory.",
                    BasicInstructionFormat.R_FORMAT,
                    "1110 01 0 0 0 0 0 1 tttt       ssss       ffffffffffff",
                   //cond 01 I P U 0 W 1 Rn(4-bits) Rd(4-bits) addr_mode  
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            try {
                                Globals.memory.setWord(
                                        RegisterFile.getValue(operands[1])+(operands[2] << 16 >> 16),
                                        RegisterFile.getValue(operands[0])
                                                    & 0xffffffff);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            //int value_obtained = RegisterFile.getValue(operands[1]) + operands[2] 
                            //RegisterFile.updateRegister(operands[0], value_obtained)
                        }
                    }));   
        instructionList.add(
                new BasicInstruction("str r11,[r0]",
                        "STR (Store Register) stores a word from a register to memory.",
                        BasicInstructionFormat.U_FORMAT,
                        "1110 01 0 0 0 0 0 1 tttt       ssss       ffffffffffff",
                       //cond 01 I P U 0 W 1 Rn(4-bits) Rd(4-bits) addr_mode  
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                try {
                                    Globals.memory.setWord(
                                        RegisterFile.getValue(operands[1]),
                                        RegisterFile.getValue(operands[0])
                                                    & 0xffffffff);
                                    } catch (AddressErrorException e) {
                                        throw new ProcessingException(statement, e);
                                }
                            
                            }

                        }));

        instructionList.add(
            new BasicInstruction("ldr r0,[r13,#112]",
                    "LDR (Load Register) loads a word from a memory address.",
                    BasicInstructionFormat.R_FORMAT,
                    "1110 01 0 0 0 0 0 1 tttt       ssss       ffffffffffff",
                   //cond 01 I P U 0 W 1 Rn(4-bits) Rd(4-bits) addr_mode  
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();                       
                            try {
                               // int address = RegisterFile.getValue(operands[1]) + (operands[2] << 16 >> 16);
                               // int value_fetched_from_memory  =  Globals.memory.getWord(address);

                                int value_fetched_from_memory = Globals.memory.getHalf(
                                    RegisterFile.getValue(operands[1])
                                            + (operands[2] << 16 >> 16));
                                    RegisterFile.updateRegister(operands[0], value_fetched_from_memory);
                               
                            } catch (AddressErrorException e) {
                                throw new ProcessingException(statement, e);
                            }
                        }
                    }));   

        instructionList.add(
                new BasicInstruction("ldr r0,[r13]",
                        "LDR (Load Register) loads a word from a memory address.",
                        BasicInstructionFormat.U_FORMAT,
                        "1110 01 0 0 0 0 0 1 tttt       ssss       ffffffffffff",
                       //cond 01 I P U 0 W 1 Rn(4-bits) Rd(4-bits) addr_mode  
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                try {
                                     int value_fetched_from_memory = Globals.memory.getHalf(
                                    RegisterFile.getValue(operands[1]));
                                    RegisterFile.updateRegister(operands[0], value_fetched_from_memory);
                                   
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
    
                            }
                        }));
    instructionList.add(
        new BasicInstruction("ldr r0,label",
                "LDR (Load Register) loads a word from a memory address.",
                BasicInstructionFormat.U_FORMAT,
                "1110 01 0 0 0 0 0 1 tttt       ssss       ffffffffffff",
                //cond 01 I P U 0 W 1 Rn(4-bits) Rd(4-bits) addr_mode  
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                                int value_fetched_from_memory = Globals.memory.getHalf(
                            operands[1]);
                            RegisterFile.updateRegister(operands[0], value_fetched_from_memory);
                            
                        } catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }

                    }
                }));
 //////////////////////////跳转       
        instructionList.add(
            new BasicInstruction("beq label",
                    "Branch if equal : Branch(jump to label) to label if zero flag is set",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "0000 101 0 ffffffffffffffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            int zero = RegisterFile.get_CPSR_Z();
                            if (zero !=0 ) {
                                // lpx : 这个减四是为啥呢？32位？ 四个Byte？
                                //processJump(RegisterFile.getProgramCounter() + operands[0] - 4);
                                processBranch(operands[0]);
                            }
                        }
                    }));
    instructionList.add(
        new BasicInstruction("blt label",
                "Branch if less than : N set and V clear, or N clear and V set (N != V)",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "1011 101 0 ffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int V = RegisterFile.get_CPSR_V();
                        int N = RegisterFile.get_CPSR_N();
                        if (V!= N ) {
                            // lpx : 这个减四是为啥呢？32位？ 四个Byte？
                            //processJump(RegisterFile.getProgramCounter() + operands[0] - 4);
                            processBranch(operands[0]);
                        }
                    }
                }));
        instructionList.add(
            new BasicInstruction("bgt label",
                    "Branch if greater than :Z clear, and either N set and V set, or N clear and V clear (Z == 0,N == V)",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "1100 101 0 ffffffffffffffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            int V = RegisterFile.get_CPSR_V();
                            int N = RegisterFile.get_CPSR_N();
                            int Z = RegisterFile.get_CPSR_Z();
                            if (V == N && Z == 0) {
                                // lpx : 这个减四是为啥呢？32位？ 四个Byte？
                                //processJump(RegisterFile.getProgramCounter() + operands[0] - 4);
                                processBranch(operands[0]);
                            }
                        }
                    }));
    instructionList.add(
        new BasicInstruction("bge label",
                "Branch if not less than(greater than or equal) : N set and V set, or N clear and V clear (N == V)",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "1010 101 0 ffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int V = RegisterFile.get_CPSR_V();
                        int N = RegisterFile.get_CPSR_N();
                        int Z = RegisterFile.get_CPSR_Z();
                        if (V == N) {
                             
                            //processJump(RegisterFile.getProgramCounter() + operands[0] - 4);
                            processBranch(operands[0]);
                        }
                    }
                }));
    instructionList.add(
        new BasicInstruction("ble label",
                "Branch if less than or equal : Z set, or N set and V clear, or N clear and V set (Z == 1 or N != V)",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "1010 101 0 ffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int V = RegisterFile.get_CPSR_V();
                        int N = RegisterFile.get_CPSR_N();
                        int Z = RegisterFile.get_CPSR_Z();
                        if (V != N || Z > 0) {
                              
                            //processJump(RegisterFile.getProgramCounter() + operands[0] - 4);
                            processBranch(operands[0]);
                        }
                    }
                }));
        instructionList.add(
            new BasicInstruction("bxeq label",
                    "Branch if equal : Branch(jump to label) to label if zero flag is set",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "0000 101 0 ffffffffffffffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            int zero = RegisterFile.get_CPSR_Z();
                            if (zero !=0 ) {
 
                                //processJump(RegisterFile.getProgramCounter() + operands[0] - 4);
                                processBranch(RegisterFile.getValue(operands[0]));
                            }
                        }
                    }));
        instructionList.add(
            new BasicInstruction("b label",
                    "Branch unconditionally to label.B (Branch) and BL (Branch and Link) cause a branch to a target address, and provide both conditional andunconditional changes to program flow.",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "1110 101 0 ffffffffffffffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            processBranch(operands[0]);
                        }
                    }));
        instructionList.add(
            new BasicInstruction("bx label",
                    "Branch unconditionally to label",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "1110  101 0 ffffffffffffffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            processBranch(RegisterFile.getValue(operands[0]));
                        }
                    }));
        instructionList.add(
            new BasicInstruction("bl label",
                    "Subroutine call to function",
                    BasicInstructionFormat.I_BRANCH_FORMAT,
                    "1110  101 0 ffffffffffffffffffffffff",
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            processBranch(operands[0]);
                            }
                    }));
            instructionList.add(
        new BasicInstruction("syscall",
                "Issue a system call : Execute the system call specified by value in r2",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 00000 00000 001100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        findAndSimulateSyscall(RegisterFile.getValue(2), statement);
                    }
                }));
    
        instructionList.add(
            new BasicInstruction("and r1,r2,#112",
                    "AND performs a bitwise AND of two values. The first value comes from a register. The second value can be either an immediate value or a value from a register, and can be shifted before the AND operation.",
                    BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 0000 0 tttt ssss ffffffffffff",
                    //cond 00 I 0000 S Rn   Rd   shifter_operand
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            // 12 bit immediate value in operands[2] is sign-extended
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[1])
                                            & (operands[2] << 20 >> 20));
                        }
                    }));
        instructionList.add(
            new BasicInstruction("and r1,r2,r3",
            "AND performs a bitwise AND of two values. The first value comes from a register. The second value can be either an immediate value or a value from a register, and can be shifted before the AND operation.",
            BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 0000 0 tttt ssss ffffffffffff",
                    //cond 00 I 0000 S Rn   Rd   shifter_operand
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            // 12 bit immediate value in operands[2] is sign-extended
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[1])
                                            & (RegisterFile.getValue(operands[2]) << 20 >> 20));
                        }
                    }));
        instructionList.add(
            new BasicInstruction("orr r1,r2,#112",
                    "ORR (Logical OR) performs a bitwise (inclusive) OR of two values. The first value comes from a register.The second value can be either an immediate value or a value from a register, and can be shifted before the OR operation.",
                    BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 1100 0 tttt ssss ffffffffffff",
                    //cond 00 I 1100 S Rn   Rd   shifter_operand
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            // 12 bit immediate value in operands[2] is sign-extended
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[1])
                                            | (operands[2] << 20 >> 20));
                        }
                    }));

        instructionList.add(
            new BasicInstruction("orr r1,r2,r3",
            "ORR (Logical OR) performs a bitwise (inclusive) OR of two values. The first value comes from a register.The second value can be either an immediate value or a value from a register, and can be shifted before the OR operation.",
            BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 1100 0 tttt ssss ffffffffffff",
                    //cond 00 I 1100 S Rn   Rd   shifter_operand
                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            // 12 bit immediate value in operands[2] is sign-extended
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[1])
                                            | (RegisterFile.getValue(operands[2]) << 20 >> 20));
                        }
                    }));

        instructionList.add(
            new BasicInstruction("eor r1,r2,r3",
                    "EOR (Exclusive OR) performs a bitwise EOR of the values from two registers.",
                    BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 0001 0 tttt ssss ffffffffffff",
                    //cond 00 I 0001 S Rn   Rd   shifter_operand

                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            // 12 bit immediate value in operands[2] is sign-extended
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[1])
                                            ^ (RegisterFile.getValue(operands[2]) << 20 >> 20));
                        }
                    }));
        instructionList.add(
            new BasicInstruction("eor r1,r2,#-112",
                    "EOR (Exclusive OR) performs a bitwise EOR of the values from two registers.",
                    BasicInstructionFormat.I_FORMAT,
                    "1110 00 0 0001 0 tttt ssss ffffffffffff",
                    //cond 00 I 0001 S Rn   Rd   shifter_operand

                    new SimulationCode() {
                        public void simulate(ProgramStatement statement) throws ProcessingException {
                            int[] operands = statement.getOperands();
                            // 12 bit immediate value in operands[2] is sign-extended
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[1])
                                            ^ (operands[2] << 20 >> 20));
                        }
                    }));




         
        //PC aligned to a four-byte problem is unsolved


        ////////////// READ PSEUDO-INSTRUCTION SPECS FROM DATA FILE AND ADD //////////////////////
        addPseudoInstructions();

        ////////////// GET AND CREATE LIST OF SYSCALL FUNCTION OBJECTS ////////////////////
        syscallLoader = new SyscallLoader();
        syscallLoader.loadSyscalls();

        // Initialization step.  Create token list for each instruction example.  This is
        // used by parser to determine user program correct syntax.
        for (int i = 0; i < instructionList.size(); i++) {
            Instruction inst = (Instruction) instructionList.get(i);
            inst.createExampleTokenList();
        }

        HashMap maskMap = new HashMap();
        ArrayList matchMaps = new ArrayList();
        for (int i = 0; i < instructionList.size(); i++) {
            Object rawInstr = instructionList.get(i);
            if (rawInstr instanceof BasicInstruction) {
                BasicInstruction basic = (BasicInstruction) rawInstr;
                Integer mask = Integer.valueOf(basic.getOpcodeMask());
                Integer match = Integer.valueOf(basic.getOpcodeMatch());
                HashMap matchMap = (HashMap) maskMap.get(mask);
                if (matchMap == null) {
                    matchMap = new HashMap();
                    maskMap.put(mask, matchMap);
                    matchMaps.add(new MatchMap(mask, matchMap));
                }
                matchMap.put(match, basic);
            }
        }
        Collections.sort(matchMaps);
        this.opcodeMatchMaps = matchMaps;
    }

    public BasicInstruction findByBinaryCode(int binaryInstr) {
        ArrayList matchMaps = this.opcodeMatchMaps;
        for (int i = 0; i < matchMaps.size(); i++) {
            MatchMap map = (MatchMap) matchMaps.get(i);
            BasicInstruction ret = map.find(binaryInstr);
            if (ret != null) return ret;
        }
        return null;
    }

    /*  METHOD TO ADD PSEUDO-INSTRUCTIONS
     */

    private void addPseudoInstructions() {
        InputStream is = null;
        BufferedReader in = null;
        try {
            // leading "/" prevents package name being prepended to filepath.
            is = this.getClass().getResourceAsStream("/PseudoOps.txt");
            in = new BufferedReader(new InputStreamReader(is));
        } catch (NullPointerException e) {
            System.out.println(
                    "Error: MIPS pseudo-instruction file PseudoOps.txt not found.");
            System.exit(0);
        }
        try {
            String line, pseudoOp, template, firstTemplate, token;
            String description;
            StringTokenizer tokenizer;
            while ((line = in.readLine()) != null) {
                // skip over: comment lines, empty lines, lines starting with blank.
                if (!line.startsWith(";") && !line.startsWith(" ")
                        && line.length() > 0) {
                    description = "";
                    tokenizer = new StringTokenizer(line, "\t");
                    pseudoOp = tokenizer.nextToken();
                    template = "";
                    firstTemplate = null;
                    while (tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                        if (token.startsWith(";")) {
                            // Optional description must be last token in the line.
                            description = token.substring(1);
                            break;
                        }
                        if (token.startsWith("COMPACT")) {
                            // has second template for Compact (16-bit) memory config -- added DPS 3 Aug 2009
                            firstTemplate = template;
                            template = "";
                            continue;
                        }
                        template = template + token;
                        if (tokenizer.hasMoreTokens()) {
                            template = template + "\n";
                        }
                    }
                    ExtendedInstruction inst = (firstTemplate == null)
                            ? new ExtendedInstruction(pseudoOp, template, description)
                            : new ExtendedInstruction(pseudoOp, firstTemplate, template, description);
                    instructionList.add(inst);
                    //if (firstTemplate != null) System.out.println("\npseudoOp: "+pseudoOp+"\ndefault template:\n"+firstTemplate+"\ncompact template:\n"+template);
                }
            }
            in.close();
        } catch (IOException ioe) {
            System.out.println(
                    "Internal Error: MIPS pseudo-instructions could not be loaded.");
            System.exit(0);
        } catch (Exception ioe) {
            System.out.println(
                    "Error: Invalid MIPS pseudo-instruction specification.");
            System.exit(0);
        }

    }

    /**
     * Given an operator mnemonic, will return the corresponding Instruction object(s)
     * from the instruction set.  Uses straight linear search technique.
     *
     * @param name operator mnemonic (e.g. addi, sw,...)
     * @return list of corresponding Instruction object(s), or null if not found.
     */
    public ArrayList matchOperator(String name) {
        ArrayList matchingInstructions = null;
        // Linear search for now....
        for (int i = 0; i < instructionList.size(); i++) {
            if (((Instruction) instructionList.get(i)).getName().equalsIgnoreCase(name)) {
                if (matchingInstructions == null)
                    matchingInstructions = new ArrayList();
                matchingInstructions.add(instructionList.get(i));
            }
        }
        return matchingInstructions;
    }


    /**
     * Given a string, will return the Instruction object(s) from the instruction
     * set whose operator mnemonic prefix matches it.  Case-insensitive.  For example
     * "s" will match "sw", "sh", "sb", etc.  Uses straight linear search technique.
     *
     * @param name a string
     * @return list of matching Instruction object(s), or null if none match.
     */
    public ArrayList prefixMatchOperator(String name) {
        ArrayList matchingInstructions = null;
        // Linear search for now....
        if (name != null) {
            for (int i = 0; i < instructionList.size(); i++) {
                if (((Instruction) instructionList.get(i)).getName().toLowerCase().startsWith(name.toLowerCase())) {
                    if (matchingInstructions == null)
                        matchingInstructions = new ArrayList();
                    matchingInstructions.add(instructionList.get(i));
                }
            }
        }
        return matchingInstructions;
    }

    /*
     * Method to find and invoke a syscall given its service number.  Each syscall
     * function is represented by an object in an array list.  Each object is of
     * a class that implements Syscall or extends AbstractSyscall.
     */

    private void findAndSimulateSyscall(int number, ProgramStatement statement)
            throws ProcessingException {
        Syscall service = syscallLoader.findSyscall(number);
        if (service != null) {
            service.simulate(statement);
            return;
        }
        throw new ProcessingException(statement,
                "invalid or unimplemented syscall service: " +
                        number + " ", Exceptions.SYSCALL_EXCEPTION);
    }

    /*
     * Method to process a successful branch condition.  DO NOT USE WITH JUMP
     * INSTRUCTIONS!  The branch operand is a relative displacement in words
     * whereas the jump operand is an absolute address in bytes.
     *
     * The parameter is displacement operand from instruction.
     *
     * Handles delayed branching if that setting is enabled.
     */
    // 4 January 2008 DPS:  The subtraction of 4 bytes (instruction length) after
    // the shift has been removed.  It is left in as commented-out code below.
    // This has the effect of always branching as if delayed branching is enabled,
    // even if it isn't.  This mod must work in conjunction with
    // ProgramStatement.java, buildBasicStatementFromBasicInstruction() method near
    // the bottom (currently line 194, heavily commented).

    private void processBranch(int displacement) {
        if (Globals.getSettings().getDelayedBranchingEnabled()) {
            // Register the branch target address (absolute byte address).
            DelayedBranch.register(RegisterFile.getProgramCounter() + (displacement) - 4);
        } else {
            // Decrement needed because PC has already been incremented
            RegisterFile.setProgramCounter(
                    RegisterFile.getProgramCounter()
                            + (displacement) - 4); // - Instruction.INSTRUCTION_LENGTH);
        }
    }

    /*
     * Method to process a jump.  DO NOT USE WITH BRANCH INSTRUCTIONS!
     * The branch operand is a relative displacement in words
     * whereas the jump operand is an absolute address in bytes.
     *
     * The parameter is jump target absolute byte address.
     *
     * Handles delayed branching if that setting is enabled.
     */

    private void processJump(int targetAddress) {
        if (Globals.getSettings().getDelayedBranchingEnabled()) {
            DelayedBranch.register(targetAddress);
        } else {
            RegisterFile.setProgramCounter(targetAddress);
        }
    }

    /*
     * Method to process storing of a return address in the given
     * register.  This is used only by the "and link"
     * instructions: jal, jalr, bltzal, bgezal.  If delayed branching
     * setting is off, the return address is the address of the
     * next instruction (e.g. the current PC value).  If on, the
     * return address is the instruction following that, to skip over
     * the delay slot.
     *
     * The parameter is register number to receive the return address.
     */

    private void processReturnAddress(int register) {
        RegisterFile.updateRegister(register, RegisterFile.getProgramCounter() +
                ((Globals.getSettings().getDelayedBranchingEnabled()) ?
                        Instruction.INSTRUCTION_LENGTH : 0));
    }

    private static class MatchMap implements Comparable {
        private int mask;
        private int maskLength; // number of 1 bits in mask
        private HashMap matchMap;

        public MatchMap(int mask, HashMap matchMap) {
            this.mask = mask;
            this.matchMap = matchMap;

            int k = 0;
            int n = mask;
            while (n != 0) {
                k++;
                n &= n - 1;
            }
            this.maskLength = k;
        }

        public boolean equals(Object o) {
            return o instanceof MatchMap && mask == ((MatchMap) o).mask;
        }

        public int compareTo(Object other) {
            MatchMap o = (MatchMap) other;
            int d = o.maskLength - this.maskLength;
            if (d == 0) d = this.mask - o.mask;
            return d;
        }

        public BasicInstruction find(int instr) {
            int match = Integer.valueOf(instr & mask);
            return (BasicInstruction) matchMap.get(match);
        }
    }
}

