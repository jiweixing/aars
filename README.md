README

# BIT-AARS : a ARM Simulator in Java 


## 1. Introduction
  BIT-AARS is a ARM simulator for teaching designed by Pengxiang Li and Donghai Liao based on MARS ansd RARS, a MIPS simulator in Java and a Risc-V simulator in Java. <br>

## 2. Supported Instructions
  The most of basic instructions set ARM 7.3.<br>
  Directives and macro are supported.<br>
  A few pseudo-instructions are supportet. we noticed that it's not enough and will be enriched in the future version.<br>
  For specific support instructions, please refer to the help documentation.<br>
  

## 3. Installing and Running
   Java JRE 1.6 or above is required. Download jar file in directory `.\bin` and Run it from console<br>
   
    $ java -jar BIT-AARS.jar 
   
  ![1](https://github.com/jiweixing/aars/raw/master/screen_shot/3_1.jpg "Main UI")	<br>
  There are mainly four sections in the main UI. <br>
  The first and second section are menu and tool bar. <br>
  The third section is "Edit/Excute" section, in which you can edit your code or watch the simulation result. <br>
  The fourth section is a console to display the output and reports of assembling and simulation.<br>
  <br>
  Here is an exmaple to show you how to use the simulator:<br>
  Click **File→Open**, and follow the instructions in [section 4→(2)](#4-examples) to find the examples. Copy and paste the example in the edit page.<br>
  ![2](https://github.com/jiweixing/aars/raw/master/screen_shot/3_2.jpg "Open file")
  ![3](https://github.com/jiweixing/aars/raw/master/screen_shot/3_3.jpg "Select file")	<br>
  <br>
  Go to Run→Assemble to assemble the asm code. You will see the execute page, in which there are source code, basic format of each code, binary code, code address(section 1), the memory(section 2) of and registers(section 3).<br>
  ![4](https://github.com/jiweixing/aars/raw/master/screen_shot/3_4.jpg "Assemble file")	<br>
  ![5](https://github.com/jiweixing/aars/raw/master/screen_shot/3_5.jpg "Assemble button in tool bar")	<br>
  Then all the work have been done. Just use run, step and other command in the tool bar(next to  the assemble button) to run the code and watch the outcomes.<br>
### About the source code：
  The main class is in Aars.java and other source codes are in help, images and Aars, in case anyone want to compile or read the code
## 4. Examples 
  (1. Open Aars.jar, find **Help→Aars→Examples**, where two example are presented.<br>
  (2. Open directory `.\asm_examples`. More examples will be found to implement Bubblesort and calculate fibonacci.<br>
## 5. Future Work
  More pseudo-instrucions to be added.<br>
  Syscall will be adjusted to follow the general use of register in ARM<br>
  <br>
  Please feel free to contact us(jwx@bit.edu.cn), if you have any questions about this project.<br>
