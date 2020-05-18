package aars.mips.hardware;

import java.util.Observer;

import aars.Globals;
import aars.assembler.SymbolTable;
import aars.mips.instructions.Instruction;
import aars.util.Binary;

/*
Copyright (c) 2003-2008,  Pete Sanderson and Kenneth Vollmar

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
 * Represents the collection of MIPS registers.
 *
 * @author Jason Bumgarner, Jason Shrewsbury
 * @version June 2003
 **/

public class RegisterFile {

    public static final int GLOBAL_POINTER_REGISTER = 12;
    public static final int STACK_POINTER_REGISTER = 13;
    public static int INITIAL_CPSR_VALUE = 0x83000000;
    private static Register[] regFile =
            {       new Register("a1", 0, 0), new Register("a2", 1, 0),
                    new Register("a3", 2, 0), new Register("a4", 3, 0),
                    new Register("v1", 4, 0), new Register("v2", 5, 0),
                    new Register("v3", 6, 0), new Register("v4", 7, 0),
                    new Register("v5", 8, 0), new Register("v6", 9, 0),
                    new Register("sl", 10, 0), new Register("fp", 11, 0),
                    new Register("ip", 12, Memory.globalPointer), new Register("sp", 13, Memory.stackPointer),
                    new Register("lr", 14, 0), new Register("pc", 15, Memory.textBaseAddress),

                    //new Register("gp", GLOBAL_POINTER_REGISTER, Memory.globalPointer),
                    //new Register("sp", STACK_POINTER_REGISTER, Memory.stackPointer),
                    //new Register("fp", 30, 0),new Register("ra", 31, 0)
            };

    private static Register CPSR = new Register("CPSR", 17,INITIAL_CPSR_VALUE);
    // private static Register hi = new Register("hi", 33, 0);//this is an internal register with arbitrary number
    // private static Register lo = new Register("lo", 34, 0);// this is an internal register with arbitrary number

    /**
     * Method for displaying the register values for debugging.
     **/

    public static void showRegisters() {
        for (int i = 0; i < regFile.length; i++) {
            System.out.println("Name: " + regFile[i].getName());
            System.out.println("Number: " + regFile[i].getNumber());
            System.out.println("Value: " + regFile[i].getValue());
            System.out.println("");
        }
    }
    public static int get_CPSR_N(){
        int value = CPSR.getValue();
        return (value & 0x00000001)==1? 1:0;
    }

    // return the Z Flag
    public static int get_CPSR_Z(){
        int value = CPSR.getValue();
        return (value & 0x00000002)==2? 1:0;
    }

    // return the C Flag
    public static int get_CPSR_C(){
        int value = CPSR.getValue();
        return (value & 0x00000004)==4? 1:0;
    }

    // return the V Flag
    public static int get_CPSR_V(){
        int value = CPSR.getValue();
        return (value & 0x00000008)==8? 1:0;
    }
    // // return the N Flag
    // public static int get_CPSR_N(){
    //     int value = CPSR.getValue();
    //     return (value & 0x00000001);
    // }

    // // return the Z Flag
    // public static int get_CPSR_Z(){
    //     int value = CPSR.getValue();
    //     return (value & 0x00000002);
    // }

    // // return the C Flag
    // public static int get_CPSR_C(){
    //     int value = CPSR.getValue();
    //     return (value & 0x00000004);
    // }

    // // return the V Flag
    // public static int get_CPSR_V(){
    //     int value = CPSR.getValue();
    //     return (value & 0x00000008);
    // }

    // reset the N Flag
    public static void reset_CPSR_N(){
        int value = CPSR.getValue();
        if(get_CPSR_N() == 0)
            return;
        value = value-1;
        CPSR.setValue(value);
    }

    // reset the Z Flag
    public static void reset_CPSR_Z(){
        int value = CPSR.getValue();
        if(get_CPSR_Z() == 0)
            return;
        value = value-2;
        CPSR.setValue(value);
    }

    // reset the C Flag
    public static void reset_CPSR_C(){
        int value = CPSR.getValue();
        if(get_CPSR_Z() == 0)
            return;
        value = value-4;
        CPSR.setValue(value);
    }

    // reset the V Flag
    public static void reset_CPSR_V(){
        int value = CPSR.getValue();
        if(get_CPSR_Z() == 0)
            return;
        value = value-8;
        CPSR.setValue(value);
    }


    // set the N Flag
    public static void set_CPSR_N(){
        int value = CPSR.getValue();
        if(get_CPSR_N() == 1)
            return;
        value = value+1;
        CPSR.setValue(value);
    }

    // set the Z Flag
    public static void set_CPSR_Z(){
        int value = CPSR.getValue();
        if(get_CPSR_Z() == 1)
            return;
        value = value+2;
        CPSR.setValue(value);
    }

    // set the C Flag
    public static void set_CPSR_C(){
        int value = CPSR.getValue();
        if(get_CPSR_Z() == 1)
            return;
        value = value+4;
        CPSR.setValue(value);
    }

    // set the V Flag
    public static void set_CPSR_V(){
        int value = CPSR.getValue();
        if(get_CPSR_Z() == 1)
            return;
        value = value+8;
        CPSR.setValue(value);
    }

    /**
     * This method updates the register value who's number is num.  Also handles the lo and hi registers
     *
     * @param num Register to set the value of.
     * @param val The desired value for the register.
     **/

    public static int updateRegister(int num, int val) {
        int old = 0;
        for (int i = 0; i < regFile.length; i++) {
            if (regFile[i].getNumber() == num) {
                old = (Globals.getSettings().getBackSteppingEnabled())
                        ? Globals.program.getBackStepper().addRegisterFileRestore(num, regFile[i].setValue(val))
                        : regFile[i].setValue(val);
                break;
            }

        }
        // if (num == 33) {//updates the hi register
        //     old = (Globals.getSettings().getBackSteppingEnabled())
        //             ? Globals.program.getBackStepper().addRegisterFileRestore(num, hi.setValue(val))
        //             : hi.setValue(val);
        // } else if (num == 34) {// updates the low register
        //     old = (Globals.getSettings().getBackSteppingEnabled())
        //             ? Globals.program.getBackStepper().addRegisterFileRestore(num, lo.setValue(val))
        //             : lo.setValue(val);
        // }
        return old;
    }

    /**
     * Sets the value of the register given to the value given.
     *
     * @param reg Name of register to set the value of.
     * @param val The desired value for the register.
     **/

    public static void updateRegister(String reg, int val) {
        if (reg.equals("zero")) {
            //System.out.println("You can not change the value of the zero register.");
        } else {
            for (int i = 0; i < regFile.length; i++) {
                if (regFile[i].getName().equals(reg)) {
                    updateRegister(i, val);
                    break;
                }
            }
        }
    }

    /**
     * Returns the value of the register who's number is num.
     *
     * @param num The register number.
     * @return The value of the given register.
     **/

    public static int getValue(int num) {
        // if (num == 33) {
        //     return hi.getValue();
        // } else if (num == 34) {
        //     return lo.getValue();
        // } else
        //     return regFile[num].getValue();
        if(num == 17){
            return CPSR.getValue();
        }
        return regFile[num].getValue();
       

    }

    /**
     * For getting the number representation of the register.
     *
     * @param n The string formatted register name to look for.
     * @return The number of the register represented by the string
     * or -1 if no match.
     **/

    public static int getNumber(String n) {
        int j = -1;
        for (int i = 0; i < regFile.length; i++) {
            if (regFile[i].getName().equals(n)) {
                j = regFile[i].getNumber();
                break;
            }
        }
        return j;
    }

    /**
     * For returning the set of registers.
     *
     * @return The set of registers.
     **/

    public static Register[] getRegisters() {
        return regFile;
    }

    /**
     * Get register object corresponding to given name.  If no match, return null.
     *
     * @param Rname The register name, either in $0 or $zero format.
     * @return The register object,or null if not found.
     **/

    public static Register getUserRegister(String Rname) {
        Register reg = null;
        if (Rname.charAt(0) >= 'a' || Rname.charAt(0) <= 'z') {
                // check for register [x + (number 0-31)].
                try {
                    int i = Binary.stringToInt(Rname.substring(1));
                    if (Rname.charAt(0) == 'r' && i >= 0 && i <= 15)
                        reg = regFile[i];
                    else{
                        reg = null; // just to be sure
                    // just do linear search; there aren't that many registers
                        for (int j = 0; j < regFile.length; j++) {
                            if (Rname.equals(regFile[j].getName())) {
                                reg = regFile[j];
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    //TODO: handle exception
                    reg = null; // just to be sure
                    // just do linear search; there aren't that many registers
                        for (int j = 0; j < regFile.length; j++) {
                            if (Rname.equals(regFile[j].getName())) {
                                reg = regFile[j];
                                break;
                            }
                        }
                }
                
        }
        return reg;
    }

    /**
     * For initializing the Program Counter.  Do not use this to implement jumps and
     * branches, as it will NOT record a backstep entry with the restore value.
     * If you need backstepping capability, use setProgramCounter instead.
     *
     * @param value The value to set the Program Counter to.
     **/

    public static void initializeProgramCounter(int value) {
        regFile[15].setValue(value);
    }

    /**
     * Will initialize the Program Counter to either the default reset value, or the address
     * associated with source program global label "main", if it exists as a text segment label
     * and the global setting is set.
     *
     * @param startAtMain If true, will set program counter to address of statement labeled
     *                    'main' (or other defined start label) if defined.  If not defined, or if parameter false,
     *                    will set program counter to default reset value.
     **/

    public static void initializeProgramCounter(boolean startAtMain) {
        int mainAddr = Globals.symbolTable.getAddress(SymbolTable.getStartLabel());
        if (startAtMain && mainAddr != SymbolTable.NOT_FOUND && (Memory.inTextSegment(mainAddr) || Memory.inKernelTextSegment(mainAddr))) {
            initializeProgramCounter(mainAddr);
        } else {
            initializeProgramCounter(regFile[15].getResetValue());
        }
    }

    /**
     * For setting the Program Counter.  Note that ordinary PC update should be done using
     * incrementPC() method. Use this only when processing jumps and branches.
     *
     * @param value The value to set the Program Counter to.
     * @return previous PC value
     **/

    public static int setProgramCounter(int value) {
        int old = regFile[15].getValue();
        regFile[15].setValue(value);
        if (Globals.getSettings().getBackSteppingEnabled()) {
            Globals.program.getBackStepper().addPCRestore(old);
        }
        return old;
    }

    /**
     * For returning the program counters value.
     *
     * @return The program counters value as an int.
     **/

    public static int getProgramCounter() {
        return regFile[15].getValue();
    }

    /**
     * Returns Register object for program counter.  Use with caution.
     *
     * @return program counter's Register object.
     */
    public static Register getProgramCounterRegister() {
        return regFile[15];
    }

    /**
     * For returning the program counter's initial (reset) value.
     *
     * @return The program counter's initial value
     **/

    public static int getInitialProgramCounter() {
        return regFile[15].getResetValue();
    }

    /**
     * Method to reinitialize the values of the registers.
     * <b>NOTE:</b> Should <i>not</i> be called from command-mode MARS because this
     * this method uses global settings from the registry.  Command-mode must operate
     * using only the command switches, not registry settings.  It can be called
     * from tools running stand-alone, and this is done in
     * <code>AbstractMarsToolAndApplication</code>.
     **/

    public static void resetRegisters() {
        for (int i = 0; i < regFile.length; i++) {
            regFile[i].resetValue();
        }
        initializeProgramCounter(Globals.getSettings().getStartAtMain());// replaces "programCounter.resetValue()", DPS 3/3/09
        // hi.resetValue();
        // lo.resetValue();
        CPSR.resetValue();
    }

    /**
     * Method to increment the Program counter in the general case (not a jump or branch).
     **/

    public static void incrementPC() {
        regFile[15].setValue(regFile[15].getValue() + Instruction.INSTRUCTION_LENGTH);
    }

    /**
     * Each individual register is a separate object and Observable.  This handy method
     * will add the given Observer to each one.  Currently does not apply to Program
     * Counter.
     */
    public static void addRegistersObserver(Observer observer) {
        for (int i = 0; i < regFile.length; i++) {
            regFile[i].addObserver(observer);
        }
        // hi.addObserver(observer);
        // lo.addObserver(observer);
        CPSR.addObserver(observer);
    }

    /**
     * Each individual register is a separate object and Observable.  This handy method
     * will delete the given Observer from each one.  Currently does not apply to Program
     * Counter.
     */
    public static void deleteRegistersObserver(Observer observer) {
        for (int i = 0; i < regFile.length; i++) {
            regFile[i].deleteObserver(observer);
        }
        // hi.deleteObserver(observer);
        // lo.deleteObserver(observer);
        CPSR.deleteObserver(observer);
    }
}
