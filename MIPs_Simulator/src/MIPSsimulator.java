//File: MIPSsimulator.java
//Name: Christopher Thomas


//imports
import java.io.*;
import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.String;
import java.lang.Exception;

//Main Class
public class MIPSsimulator {

  public static void main(String[] args) {
    if(args.length == 0) {
      errorMessage();
    }
    System.out.println("------------------------");
    System.out.println("ChrisTopher C. Thomas");
    System.out.println("Program3: MIPS simulator");
    boolean debug=false;
    if(args[0].equals("-d")) {debug=true;System.out.println("Mode: Debug");}
    else if(args[0].equals("-n")) {debug=false;System.out.println("Mode: Normal");}
    else {errorMessage();}
    System.out.println("End Program: control+c");
    System.out.println("------------------------");
    new MIPSsim(debug);
  }

  private static void errorMessage() {
    System.out.println("<ERROR...>");
    System.out.println("<java MIPSsimulator -n> //Normal Mode");
    System.out.println("<java MIPSsimulator -d> //Debug Mode");
    System.out.println("<ENDING PROGRAM...>");
    System.exit(0);
  }

}//MIPSsimulator

//Simulator Class
class MIPSsim {
  //Time Program
  long startProgram;
  long endProgram;
  //Input File
  private File file;
  //Read File
  private BufferedReader bufferedReader;
  private FileReader fileReader;
  //inputs
  Scanner scan = new Scanner(System.in);
  Boolean debugModeMaster=false;
  Boolean debugMode1=false;
  Boolean debugMode2=false;
  //Memory's
  private long mainMemorySize = (long)Math.pow(2,20);
  private long[] MEM = new long[(int)mainMemorySize]; // Main Memory
  private String word;
  /* General purpose register
      $0 = zero -> always value 0
      $1 = at -> Assembler Temp
      $2-$3 = v0-v1
      $4-$7 = a0-a3
      $8-$15,$24-$25 = t0-t9 -> temp registers
      $16-$23,$30 = s0-s8 -> saved registers
      $26-$27 = k0-k1 -> OS kernel & exception return
      $28 = gp -> Global pointer
      $29 = sp -> Stack pointer
      $31 = ra -> return adress
  */
  private long[] GPR = new long[32];
  private long PC,nPC,LO,HI=0;

  /*
      Start of Simulator
  */
  public MIPSsim(boolean debug) {
    startProgram = System.currentTimeMillis();
    for(int i=0;i<mainMemorySize;i++) MEM[i]=0;
    for(int i=0;i<32;i++) GPR[(int)i]=0;

    if(debug) {
      debugModeMaster=true;
      debugMode1=true;
      debugMode2=true;
    }
    if(!debug) {
      debugModeMaster=false;
      debugMode1=false;
      debugMode2=false;
    }

    fetchFile();

    /* Remember Your Failure!!!
    for (int i=0;i<mainMemorySize;i++) {
      if(MEM[i]!=0) {
        if(debugMode) {debugContinue();}
        mipsInstructions(MEM[(int)(PC/4)]);
      }
    }
    */
    while (true) {
      if(debugModeMaster) {debugContinue();}
      //System.out.printf("%x%n", PC);
      mipsInstructions(MEM[(int)(PC/4)]);
    }
  }//MIPSsim

  //debugContinue()
  private void debugContinue() {
    System.out.println("<Press Enter to Continue...>");
    String cmd="cmd";
    cmd = scan.nextLine();
    if(!cmd.equals("")) debugContinue();
  }

  ////Interpret line from input file
  private void parseFile(String line) {
    boolean isIndexSpot=true;
    String temp="";
    String indexSpot="";
    ArrayList<String> hexCode=new ArrayList<>();

    //if line is not blank
    if(line.length()>0) {
      //loop through each character on line
      for (int i=0;i<line.length();i++) {
        //if comment break
        if(line.charAt(i)=='#')break;

        //if index spot
        if(line.charAt(i) == '[' && isIndexSpot) {
          //if save location is in main Memory
          if(line.charAt(i+1)=='0' && line.charAt(i+2)=='x') {
            for (int j=i+3;j<line.length();j++) {
              if(line.charAt(j)==']') break;
              indexSpot+=line.charAt(j);
              i=j;
            }
          }
          //if save location is a register
          else {
            for(int j=i+1;j<line.length();j++) {
              if(line.charAt(j)==']') break;
              indexSpot+=line.charAt(j);
              i=j;
            }
          }
          isIndexSpot=false;
        }

        //interpret hex code(instructions)
        if(line.charAt(i-1)=='0' && line.charAt(i)=='x' && !isIndexSpot) {
          for (int j=i+1;j<line.length();j++) {
            if(line.charAt(j)==' ') break;
            temp+=line.charAt(j);
            i=j;
          }
          if(temp.length()==8) hexCode.add(temp);
          temp="";
        }
      }

      //If line cointained information needed for interpretation
      if(!isIndexSpot) {
        //Save interpreted code
        if(indexSpot.equals("PC")) {
          PC = (Long.parseLong(hexCode.get(0),16)/4);
          nPC=PC+4;
        }
        else if (indexSpot.charAt(0)=='R') {
          String registerLocation="";
          for(int i=1;i<indexSpot.length();i++){
            registerLocation+=indexSpot.charAt(i);
          }
          long registerLocationIndex = Long.parseLong(registerLocation);
          GPR[(int)registerLocationIndex] = Long.parseLong(hexCode.get(0),16);
        }
        else {
          long indexRegister = Long.parseLong(indexSpot,16);
          for(int i=0;i<hexCode.size();i++) {
            MEM[((int)indexRegister+(i*4))/4] = Long.parseLong(hexCode.get(i),16);
          }
        }
      }

    } //if

  }

  //Fetch File path
  private void fetchFile() {
    System.out.println("Input File");
    System.out.print("%");
    String inputString = scan.nextLine();
    inputString = checkEasterEggs(inputString);
    File file = new File("../TextFiles/" + inputString + ".txt");
    this.file=file;
    readFile();
  }

  //Read in input file
  private void readFile() {
    try {
      fileReader = new FileReader(file);
      bufferedReader = new BufferedReader(fileReader);
      String line;
      while((line = bufferedReader.readLine()) != null) {
        parseFile(line);
      }//while
    }//try
    catch(FileNotFoundException e) {
      System.out.println("<FileNotFoundException>");
      System.out.println("<Example: fileName.txt>");
      System.out.println("<input: fileName>");
      fetchFile();
    }
    catch(IOException e) {e.printStackTrace();}
    finally {
      try {
        bufferedReader.close();
      }catch (IOException e) {
        e.printStackTrace();
      }
    }//finally
  }//readFile

  //Check mips instructions
  private void mipsInstructions(long decimal) {
    String binary = convertToBinary(decimal);
    int[] instr = convertToIntArray(binary);

    //Print
    if (debugMode1) {
      System.out.println("*Instruction*");
      System.out.println("Dec: " + decimal);
      System.out.print("Binary: ");
      for(int i=0;i<32;i++) {
        if (i==4 || i==8 || i==12 || i==16 || i==20 || i==24 || i==28)
          System.out.print(" ");
        System.out.print(instr[i]);
      }
      System.out.print("\n");
    }

    //init $s
    int[] $s = new int[32];String sRegister="";long s=0;
    for (int i=0;i<=10;i++) $s[i]=0;
    for (int i=6;i<=10;i++) {
      sRegister+=Character.getNumericValue(binary.charAt(i));
      $s[i+5]=Character.getNumericValue(binary.charAt(i));
    }
    //init $t
    int[] $t = new int[32];String tRegister="";long t=0;
    for (int i=11;i<=15;i++) tRegister+=Character.getNumericValue(binary.charAt(i));
    //init $d
    int[] $d = new int[32];String dRegister="";long d=0;
    for (int i=16;i<=20;i++) dRegister+=Character.getNumericValue(binary.charAt(i));
    //init imm
    int[] imm= new int[32];String immRegister="";long immVal=0;
    for (int i=0;i<=15;i++) imm[i]=0;
    for (int i=16;i<=31;i++) {
      immRegister+=Character.getNumericValue(binary.charAt(i));
      imm[i]=Character.getNumericValue(binary.charAt(i));
    }


    //Check Instructions
    if(Arrays.equals(AND(instr,maskADD),instrADD)) { //ADD - Add(with overflow)
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[0]);
        System.out.println("ADD: $d = $s + $t; advance_pc(4)");
      }
      //add $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 0000
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)(int)s];
      t=GPR[(int)(int)t];
      long saveRegister = Long.parseLong(dRegister,2);

      if(debugMode2) {
        System.out.println("Saves " + s + "+" + t + " to GPR[" + saveRegister + "]");
      }
      GPR[(int)saveRegister] = s+t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskADDI),instrADDI)) { //ADDI - Add immediate(with overflow)
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[1]);
        System.out.println("ADDI: $t = $s + imm; advance_pc(4)");
      }
      //addi $t, $s, imm
      //0010 00ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);
      long saveRegister = Long.parseLong(tRegister,2);

      if(debugMode2) {
        System.out.println("Saves " + s + "+" + immVal + " to GPR[" + saveRegister + "]");
      }
      GPR[(int)saveRegister] = s+immVal;
      advancePC(14);

    }
    else if(Arrays.equals(AND(instr,maskADDIU),instrADDIU)) { //ADDIU - Add immediate unsigned(no overflow)
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[2]);
        System.out.println("ADDIU: $t = $s + imm; advance_pc(4)");
      }
      //addiu $t, $s, imm
      //0010 01ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);
      long saveRegister = Long.parseLong(tRegister,2);

      if(debugMode2) {
        System.out.println("Saves " + s + "+" + immVal + " to GPR[" + saveRegister + "]");
      }
      GPR[(int)saveRegister] = s+immVal;
      //System.out.println("(" + (s+immVal) + ")");
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskADDU),instrADDU)) { //ADDU - add unsighted(no overflow)
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[3]);
        System.out.println("ADDU: $d = $s + $t; advance_pc(4)");
      }
      //addu $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 0001
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      //System.out.printf("(%x, %d, %d)\n", PC, s, t);
      s=GPR[(int)s];
      t=GPR[(int)t];
      long saveRegister = Long.parseLong(dRegister,2);
      //System.out.printf("(%d, %d, %d)\n", saveRegister, s, t);
      if(debugMode2) {
        System.out.println("Saves " + s + "+" + t + " to GPR[" + saveRegister + "]");
      }
      GPR[(int)saveRegister] = s+t;
      advancePC(4);
      //System.out.println(GPR[4]);
    }
    else if(Arrays.equals(AND(instr,maskAND),instrAND)) { //AND - Bitwise and
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[4]);
        System.out.println("AND: $d = $s & $t; advance_pc(4)");
      }
      //and $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 0100
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)s];
      t=GPR[(int)t];
      //sRegister = convertToBinary(s);
      //tRegister = convertToBinary(t);
      //$s = convertToIntArray(sRegister);
      //$t = convertToIntArray(tRegister);

      long saveRegister = Long.parseLong(dRegister,2);dRegister="";

      //AND $s and $t
      //for (int i=0;i<32;i++) {
      //  if($s[i]==0 || $t[i]==0) dRegister+='0';
      //  else dRegister += '1';
      //}
      //Save t in main Memory
      if(debugMode2) {
        System.out.println("Saves " + s + "&" + t + " to GPR[" + saveRegister + "]");
      }
      GPR[(int)saveRegister] = s&t; //Long.parseLong(dRegister,2);
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskANDI),instrANDI)) { //ANDI - Bitwise and immediate
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[5]);
        System.out.println("ANDI: $t = $s & imm; advance_pc(4)");
      }
      //andi $t, $s, imm
      //0011 00ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      //sRegister = convertToBinary(s);
      //$s = convertToIntArray(sRegister);

      long saveRegister = Long.parseLong(tRegister,2);tRegister="";

      //AND $s and imm
      for (int i=0;i<32;i++) {
        if($s[i]==0 || imm[i]==0) tRegister+='0';
        else tRegister += '1';
      }
      //Save t in main Memory
      if(debugMode2) {
        System.out.println("Saves " + s + "&" + immVal + " to GPR[" + saveRegister + "]");
      }
      GPR[(int)saveRegister] = s&immVal; //Long.parseLong(tRegister,2);
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBEQ),instrBEQ)) { //BEQ - Branch on equal
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[6]);
        System.out.println("BEQ: if $s == $t advance_pc (offset << 2)); else advance_pc (4)");
      }
      //beq $s, $t, offset
      //0001 00ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2); //sRegister =ss sss
      t = Long.parseLong(tRegister,2); //tRegister =t tttt
      //System.out.println("\n" + s + " " + t);
      s=GPR[(int)s];
      t=GPR[(int)t];
      //System.out.println(s + " " + t + "\n");

      //immRegister = iiii iiii iiii iiii
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + "==" + t + " advance_PC(" + offset + "); else advance_PC(4)");
      }
      if(s==t) advancePC(offset);
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBGEZ),instrBGEZ)) { //BGEZ - Branch on greater than or equal to zero
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[7]);
        System.out.println("BGEZ: if $s >= 0 advance_pc (offset << 2)); else advance_pc (4)");
      }
      //bgez $s, offset
      //0000 01ss sss0 0001 iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + ">= 0 advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s>=0) advancePC(immVal<<2);
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBGEZAL),instrBGEZAL)) { //BGEZAL - Branch on greater than or equal to zero and link
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[8]);
        System.out.println("BGEZAL: if $s >= 0 $31 = PC + 8 (or nPC + 4); advance_pc (offset << 2)); else advance_pc (4)");
      }
      //bgezal $s, offset
      //0000 01ss sss1 0001 iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);


      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + ">= 0; GPR[31] = " + PC + "+8; advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s>=0) {
        GPR[(int)31]=PC+8;
        advancePC(immVal<<2);
      }
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBGTZ),instrBGTZ)) { //BGTZ - Branch on greater than zero
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[9]);
        System.out.println("BGTZ: if $s > 0 advance_pc (offset << 2)); else advance_pc (4)");
      }
      //bgtz $s, offset
      //0001 11ss sss0 0000 iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + ">0 advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s>0) advancePC(immVal<<2);
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBLEZ),instrBLEZ)) { //BLEZ - Branch on less than or equal to zero
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[10]);
        System.out.println("BLEZ: if $s <= 0 advance_pc (offset << 2)); else advance_pc (4)");
      }
      //blez $s, offset
      //0001 10ss sss0 0000 iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + "<=0 advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s<=0) advancePC(immVal<<2);
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBLTZ),instrBLTZ)) { //BLTZ - Branch on less than zero
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[11]);
        System.out.println("BLTZ: if $s < 0 advance_pc (offset << 2)); else advance_pc (4)");
      }
      //bltz $s, offset
      //0000 01ss sss0 0000 iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + "<0 advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s<0) advancePC(immVal<<2);
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBLTZAL),instrBLTZAL)) { //BLTZAL - Branch on less than zero and link
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[12]);
        System.out.println("BLTZAL: if $s < 0 $31 = PC + 8 (or nPC + 4); advance_pc (offset << 2)); else advance_pc (4)");
      }
      //bltzal $s, offset
      //0000 01ss sss1 0000 iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + "<0; GPR[31] = " + PC + "+8; advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s<0) {
        GPR[(int)31]=PC+8;
        advancePC(immVal<<2);
      }
      else advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskBNE),instrBNE)) { //BNE - Branch on not equal
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[13]);
        System.out.println("BNE: if $s != $t advance_pc (offset << 2)); else advance_pc (4)");
      }
      //bne $s, $t, offset
      //0001 01ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)s];
      t=GPR[(int)t];
      immVal = Long.parseLong(immRegister,2);

      Long offset = immVal<<2;
      if(debugMode2) {
        System.out.println("if " + s + "!=" + t + " advance_PC(" + offset + "); else advance_PC(4)");
      }

      if(s!=t) advancePC(immVal<<2);
      else advancePC(4);


    }
    else if(Arrays.equals(AND(instr,maskDIV),instrDIV)) { //DIV - Divide
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[14]);
        System.out.println("DIV: $LO = $s / $t; $HI = $s % $t; advance_pc (4)");
      }
      //div $s, $t
      //0000 00ss ssst tttt 0000 0000 0001 1010
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)s];
      t=GPR[(int)t];

      if(debugMode2) {
        System.out.println("$LO = " + s + "/" + t + "; $HI = " + s + "%" + t + "); advance_PC(4)");
      }

      LO = s/t;
      HI = s%t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskJ),instrJ)) { //J - Jump
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[15]);
        System.out.println("J: PC = nPC; nPC = (PC & 0xf0000000) | (target << 2)");
      }
      //j target
      //0000 10ii iiii iiii iiii iiii iiii iiii
      immRegister="";
      for(int i=6;i<32;i++) {
        immRegister+=Character.getNumericValue(binary.charAt(i));
      }
      immVal = Long.parseLong(immRegister,2);

      long and = (PC&0xf0000000);
      long offset = (immVal<<2);
      long or = (and|offset);
      //System.out.printf("%x%n", or);
      if(debugMode2) {
        System.out.println("PC = "+ nPC + "; nPC = (" + and + "|" + offset + ") = " +  or + ";" );
      }
      PC = nPC;
      nPC= or;

    }
    else if(Arrays.equals(AND(instr,maskJAL),instrJAL)) { //JAL - Jump and link
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[16]);
        System.out.println("JAL: $31 = PC + 8 (or nPC + 4); PC = nPC; nPC = (PC & 0xf0000000) | (target << 2)");
      }
      //jal target
      //0000 11ii iiii iiii iiii iiii iiii iiii
      //0000 1100 0000 0000 0000 0000 0000 1001
      immRegister="";
      for(int i=6;i<32;i++) {
        immRegister+=Character.getNumericValue(binary.charAt(i));
      }
      immVal = Long.parseLong(immRegister,2);


      long and = (PC&0xf0000000);
      long offset = (immVal<<2);
      long or = (and|offset);
      if(debugMode2) {
        System.out.println("GPR[31] = " + PC + "+8; PC = " + nPC + "; nPC = (" + and + "|" + offset + ") = " +  or + ";");
      }
      GPR[(int)31] = PC + 8;
      PC=nPC;
      nPC=or;
    }
    else if(Arrays.equals(AND(instr,maskJR),instrJR)) { //JR - Jump register
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[17]);
        System.out.println("JR: PC = nPC; nPC = $s");
      }
      //jr $s
      //0000 00ss sss0 0000 0000 0000 0000 1000
      s = Long.parseLong(sRegister,2);
      //System.out.println("[" + s + "]");
      s=GPR[(int)s];
      //System.out.println("(" + s + ")");

      if(debugMode2) {
        System.out.println("PC = " + nPC + "; nPC = " + s + ";");
      }
      PC=nPC;
      nPC=s;
    }
    else if(Arrays.equals(AND(instr,maskLB),instrLB)) { //LB - Load byte
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[18]);
        System.out.println("LB: $t = MEM[$s + offset]; advance_pc (4)");
      }
      //lb $t, offset($s)
      //1000 00ss ssst tttt iiii iiii iiii iiii
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      long offset = immVal;

      if(debugMode2) {
        System.out.println("Saves MEM[" + s + "+" + offset + "] or " + MEM[((int)s+(int)offset)/4] + " at GPR[" + t + "]; advance_PC(4);");
      }

      GPR[(int)t] = MEM[((int)s+(int)offset)/4];
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskLUI),instrLUI)) { //LUI - Load upper immediate
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[19]);
        System.out.println("LUI: $t = (imm << 16); advance_pc (4)");
      }
      //lui $t, imm
      //0011 11-- ---t tttt iiii iiii iiii iiii
      t = Long.parseLong(tRegister,2);
      immVal = Long.parseLong(immRegister,2);
      //Long offset = immVal<<16
      //t=GPR[(int)t];

      long offset = immVal<<16;
      if(debugMode2) {
        System.out.println("Saves " + offset + " to  GPR[" + t + "];");
      }
      GPR[(int)t] = offset;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskLW),instrLW)) { //LW - Load word
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[20]);
        System.out.println("LW: $t = MEM[$s + offset]; advance_pc (4)");
      }
      //lw $t, offset($s)
      //1000 11ss ssst tttt iiii iiii iiii iiiis
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      immVal = Long.parseLong(immRegister,2);

      long offset = immVal;

      if(debugMode2) {
        System.out.println("Saves MEM[" + s + "+" + offset + "] or " + MEM[((int)s+(int)offset)/4] + " at GPR[" + t + "]; advance_PC(4);");
      }

      GPR[(int)t] = MEM[((int)s+(int)offset)/4];
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskMFHI),instrMFHI)) { //MFHI - Move from HI
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[21]);
        System.out.println("MFHI: $d = $HI; advance_pc (4)");
      }
      //mfhi $d
      //0000 0000 0000 0000 dddd d000 0001 0000
      d = Long.parseLong(dRegister,2);

      if (debugMode2) {
        System.out.println("GPR[" + d + "] = " + HI + ";");
      }
      GPR[(int)d]=HI;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskMFLO),instrMFLO)) { //MFLO - Move from LO
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[22]);
        System.out.println("MFLO: $d = $LO; advance_pc (4)");
      }
      //mflo $d
      //0000 0000 0000 0000 dddd d000 0001 0010
      d = Long.parseLong(dRegister,2);

      if (debugMode2) {
        System.out.println("GPR[" + d + "] = " + LO + ";");
      }

      GPR[(int)d]=LO;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskMULT),instrMULT)) { //MULT - Multiply
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[23]);
        System.out.println("MULT: $LO = $s * $t; advance_pc (4)");
      }
      //mult $s, $t
      //0000 00ss ssst tttt 0000 0000 0001 1000
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)s];
      t=GPR[(int)t];

      if(debugMode2) {
        System.out.println("$LO = " + s + "*" + t + ";");
      }

      LO = s * t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskOR),instrOR)) { //OR - Bitwise or
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[24]);
        System.out.println("OR: $d = $s | $t; advance_pc (4)");
      }
      //or $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 0101
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)s];
      t=GPR[(int)t];
      d = Long.parseLong(dRegister,2);

      if (debugMode2) {
        System.out.println("GPR[" + d + "] = (" + s + "|" + t + ") or " + (s|t) + ";");
      }

      GPR[(int)d] = s|t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskORI),instrORI)) { //ORI - Bitwise or immediate
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[25]);
        System.out.println("ORI: $t = $s | imm; advance_pc (4)");
      }
      //ori $t $s imm
      //0011 01ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s=GPR[(int)s];
      //Spot where value is saved
      long saveRegister = Long.parseLong(tRegister,2);tRegister="";

      //Save t in main Memory
      immVal = Long.parseLong(immRegister,2);

      if (debugMode2) {
        System.out.println("GPR[" + saveRegister + "] = (" + s + "|" + immVal + ") or " + (s|immVal) + ";");
      }

      GPR[(int)saveRegister] = s|immVal;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSB),instrSB)) { //SB - Store byte
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[26]);
        System.out.println("SB: MEM[$s + offset] = (0xff & $t); advance_pc (4);");
      }
      //sb $t, offset($s)
      //1010 00ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      t = Long.parseLong(tRegister,2);
      s=GPR[(int)s];
      t=GPR[(int)t];
      immVal = Long.parseLong(immRegister,2);

      long offset = immVal;
      if(debugMode2) {
        System.out.println("MEM[" + s + "+" + offset + "] = (" + 0xff + "&"+ t + ") or " + (0xff & t) + "; advance_PC(4);");
      }

      MEM[((int)s+(int)offset)/4] = (0xff & t);
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSLL),instrSLL)) { //SLL - Shift left logical
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[27]);
        System.out.println("SLL: $d = $t << h; advance_pc (4);");
      }
      //sll $d, $t, h
      //0000 00ss ssst tttt dddd dhhh hh00 0000
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      t=GPR[(int)t];
      String h="";
      for(int i=21;i<=25;i++) {
        h+=Character.getNumericValue(binary.charAt(i));
      }
      long hlong = Long.parseLong(h,2);

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = (" + t + "<<" + hlong + ")" + " or "+ (t<<hlong) + ";");
      }
      GPR[(int)d] = (t<<hlong);
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSLLV),instrSLLV)) { //SLLV - Shift left logical variable
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[28]);
        System.out.println("SLLV: $d = $t << $s; advance_pc (4)");
      }
      //sllv $d, $t, $s
      //0000 00ss ssst tttt dddd d--- --00 0100
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      t=GPR[(int)t];
      s=GPR[(int)s];

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = (" + t + "<<" + s + ") or " + (t<<s) +";");
      }

      GPR[(int)d] = t<<s;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSLT),instrSLT)) { //SLT - Set on less than (signed)
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[29]);
        System.out.println("SLT: if $s < $t $d = 1; advance_pc (4); else $d = 0; advance_pc (4)");
      }
      //slt $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 1010
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      //System.out.printf("(%x, %d, %d)\n", PC, s, t);
      s = GPR[(int)s];
      t = GPR[(int)t];
      //System.out.printf("(%d, %d)\n", s, t);

      if(debugMode2) {
        System.out.println("if (" + s + "<" + t + ") GPR[" + d + "] = 1; else GPR[" + d + "] = 0;");
      }

      if(s<t) {GPR[(int)d]=1; advancePC(4);}
      else {GPR[(int)d]=0; advancePC(4);}


    }
    else if(Arrays.equals(AND(instr,maskSLTI),instrSLTI)) { //SLTI - Set on less than immediate (signed)
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[30]);
        System.out.println("SLTI: if $s < imm $t = 1; advance_pc (4); else $t = 0; advance_pc (4)");
      }
      //slti $t, $s, imm
      //0010 10ss ssst tttt iiii iiii iiii iiii
      immVal = Long.parseLong(immRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      s = GPR[(int)s];

      if(debugMode2) {
        System.out.println("if (" + s + "<" + immVal + ") GPR[" + t + "] = 1; else GPR[" + t + "] = 0;");
      }

      if(s<immVal) {GPR[(int)t]=1; advancePC(4);}
      else {GPR[(int)t]=0; advancePC(4);}

    }
    else if(Arrays.equals(AND(instr,maskSLTIU),instrSLTIU)) { //SLTIU - Set on less than immediate unsigned
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[32]);
        System.out.println("SLTIU: if $s < imm $t = 1; advance_pc (4); else $t = 0; advance_pc (4)");
      }
      //sltiu $t, $s, imm
      //0010 11ss ssst tttt iiii iiii iiii iiii
      immVal = Long.parseLong(immRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      s = GPR[(int)s];

      if(debugMode2) {
        System.out.println("if (" + s + "<" + immVal + ") GPR[" + t + "] = 1; else GPR[" + t + "] = 0;");
      }

      if(s<immVal) {GPR[(int)t]=1; advancePC(4);}
      else {GPR[(int)t]=0; advancePC(4);}


    }
    else if(Arrays.equals(AND(instr,maskSLTU),instrSLTU)) { //SLTU - Set on less than unsigned
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[33]);
        System.out.println("SLTU: if $s < $t $d = 1; advance_pc (4); else $d = 0; advance_pc (4)");
      }
      //sltu $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 1011
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      s = GPR[(int)s];
      t = GPR[(int)t];

      if(debugMode2) {
        System.out.println("if (" + s + "<" + t + ") GPR[" + d + "] = 1; else GPR[" + d + "] = 0;");
      }

      if(s<t) {GPR[(int)d]=1; advancePC(4);}
      else {GPR[(int)d]=0; advancePC(4);}

    }
    else if(Arrays.equals(AND(instr,maskSRA),instrSRA)) { //SRA - Shift right arithmetic
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[34]);
        System.out.println("SRA: $d = $t >> h; advance_pc (4)");
      }
      //sra $d, $t, h
      //0000 00-- ---t tttt dddd dhhh hh00 0011
      String h="";
      for(int i=21;i<=25;i++) {
        h+=Character.getNumericValue(binary.charAt(i));
      }
      long hlong = Long.parseLong(h,2);

      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      t = GPR[(int)t];

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = " + t + ">>" + hlong + ") or " + (t>>hlong) + ";");
      }

      GPR[(int)d] = t>>hlong;
      advancePC(4);


    }
    else if(Arrays.equals(AND(instr,maskSRL),instrSRL)) { //SRL - Shift right logical
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[35]);
        System.out.println("SRL: $d = $t >> h; advance_pc (4)");
      }
      //srl $d, $t, h
      //0000 00-- ---t tttt dddd dhhh hh00 0010
      String h="";
      for(int i=21;i<=25;i++) {
        h+=Character.getNumericValue(binary.charAt(i));
      }
      long hlong = Long.parseLong(h,2);

      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      t = GPR[(int)t];

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = " + t + ">>" + hlong + ") or " + (t>>hlong) + ";");
      }

      GPR[(int)d] = t>>hlong;
      advancePC(4);


    }
    else if(Arrays.equals(AND(instr,maskSRLV),instrSRLV)) { //SRLV - Shift right logical variable
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[36]);
        System.out.println("SRLV: $d = $t >> $s; advance_pc (4)");
      }
      //srlv $d, $t, $s
      //0000 00ss ssst tttt dddd d000 0000 0110
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      t = GPR[(int)t];
      s = GPR[(int)s];

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = " + t + ">>" + s + ") or " + (t>>s) + ";");
      }

      GPR[(int)d] = t>>s;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSUB),instrSUB)) { //SUB - Subtract
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[37]);
        System.out.println("SUB: $d = $s - $t; advance_pc (4)");
      }
      //sub $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 0010
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      t = GPR[(int)t];
      s = GPR[(int)s];

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = " + s + "-" + t + ") or " + (s-t) + ";");
      }

      GPR[(int)d]=s-t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSUBU),instrSUBU)) { //SUBU - Subtract unsigned
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[38]);
        System.out.println("SUBU: $d = $s - $t; advance_pc (4)");
      }
      //subu $d, $s, $t
      //0000 00ss ssst tttt dddd d000 0010 0011
      d = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      t = GPR[(int)t];
      s = GPR[(int)s];

      if(debugMode2) {
        System.out.println("GPR[" + d + "] = " + s + "-" + t + ") or " + (s-t) + ";");
      }

      GPR[(int)d]=s-t;
      advancePC(4);


    }
    else if(Arrays.equals(AND(instr,maskSW),instrSW)) { //SW - Store word
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[39]);
        System.out.println("SW: MEM[$s + offset] = $t; advance_pc (4)");
      }
      //sw $t, offset($s)
      //1010 11ss ssst tttt iiii iiii iiii iiii
      immVal = Long.parseLong(immRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      t = GPR[(int)t];
      s = GPR[(int)s];

      long offset = (immVal);

      if(debugMode2) {
        System.out.println("MEM[" + s + "+" + offset + "] = " + t + ";");
      }

      MEM[((int)s + (int)offset)/4] = t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskSYSCALL),instrSYSCALL)) { //SYSCALL - System call
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[40]);
        System.out.println("SYSCALL: advance_pc (4)");
      }
      //syscall
      //0000 00-- ---- ---- ---- ---- --00 1100
      systemCall();
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskXOR),instrXOR)) { //XOR - Bitwise exclusive or
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[41]);
        System.out.println("XOR: $d = $s ^ $t; advance_pc (4)");
      }
      //xor $d, $s, $t
      //0000 00ss ssst tttt dddd d--- --10 0110
      long saveRegister = Long.parseLong(dRegister,2);
      t = Long.parseLong(tRegister,2);
      s = Long.parseLong(sRegister,2);
      t = GPR[(int)t];
      s = GPR[(int)s];

      if(debugMode2) {
        System.out.println("GPR[" + saveRegister + "] = (" + s + "^" + t + ") or " + (s^t) + ";");
      }

      GPR[(int)saveRegister] = s^t;
      advancePC(4);

    }
    else if(Arrays.equals(AND(instr,maskXORI),instrXORI)) { //XORI - Bitwise exclusive or immediate
      if(debugMode1) {
        System.out.println("instru: " + instrStingInstructions[42]);
        System.out.println("XORI: $t = $s ^ imm; advance_pc (4)");
      }
      //xori $t, $s, imm
      //0011 10ss ssst tttt iiii iiii iiii iiii
      s = Long.parseLong(sRegister,2);
      s = GPR[(int)s];

      //Spot where value is saved
      long saveRegister = Long.parseLong(tRegister,2);

      //Save t in main Memory
      immVal = Long.parseLong(immRegister,2);

      if(debugMode2) {
        System.out.println("GPR[" + saveRegister + "] = (" + s + "^" + immVal + ") or " + (s^immVal) + ";");
      }

      GPR[(int)saveRegister] = s^immVal;
      advancePC(4);



    }
    else{
      System.out.println("x");
    }
  }

  //SYSCALL
  private void systemCall() {
    if(debugModeMaster) System.out.println("\n");
    if(GPR[2]==1) {
      //print integer
      System.out.print(GPR[4]);
    }
    else if(GPR[2]==4) {
      int startPosition=(int)GPR[4];
      int counter=123456789;
      int memoryIndex=0;
      int offset=0;
      //System.out.println("Start Positon: " + startPosition);

      while(true) {
        String hex = Long.toHexString( Integer.reverseBytes((int)MEM[((startPosition+memoryIndex)/4)]));
        offset = ((startPosition+memoryIndex)%4);
        while(hex.length()<8) {
          hex = '0' + hex;
        }
        try{
          String value = "" + hex.charAt(offset*2) + hex.charAt((offset*2)+1);
          //System.out.println("(" + value + ") Hex: " + hex + "; MEM[" + ((startPosition+memoryIndex)%4) + "]");
          int letter = Integer.parseInt(value,16);
          char c = (char)letter;
          if(c=='\0') {
            //System.out.print(" ");
            break;
          }
          System.out.print(c);
        }catch(Exception e){}
        memoryIndex+=1;
        counter = (int)MEM[((startPosition+memoryIndex)/4)] ;
      }


    }
    else if(GPR[2]==11) {
      //print character
      System.out.print(GPR[4]);
    }
    else if(GPR[2]==5) {
      int input=0;
      input = scan.nextInt();
      GPR[2] = input;
    }
    else if(GPR[2]==8) {
      System.out.println("Read String");
      /*
      Reads a string into address pointed to by a0=GPR[4],
      up to $a1-1 characters, and null terminates the
      string. Note that the characters must be stored
      as bytes, so you will have to deal with converting
      a string from the language used for your simulator
      to a null terminated string stored in an array of ints.
      */

    }
    else if(GPR[2]==9) {
      System.out.println("Allocate memory");
      /*
      Allocates bytes based on a0=GPR[4], returns address in v0=GPR[2].
      This can be done by incrementing a global pointer by the
      size of the memory allocation, and then returning the
      value of the pointer before incrementing it.
      */
    }
    else if(GPR[2]==10) {
      endProgram();
    }
  }

  //advance PC and nPC
  private void advancePC(long advance) {
    PC=nPC;
    nPC+=advance;
  }

  //Convert long to Binary, if length not 32 add zero's to front
  private String convertToBinary(long decimal) {
    String binary = Long.toBinaryString(decimal);
    while (binary.length()<32) {
      binary = '0' + binary;
    }
    return binary;
  }
  private int[] convertToIntArray(String a) {
    int[] returnVal = new int[32];
    for(int i=0;i<a.length();i++) {
      char c = a.charAt(i);
      int value = Character.getNumericValue(c);
      returnVal[i] = value;

    }
    return returnVal;
  }

  //CIRCUIT LIKE METHODS
  private int[] AND(int[] a,int[] b) {
    int[] returnVal = new int[32];
    for (int i=0;i<32;i++) {
      returnVal[i] = a[i]*b[i];
    }
    return returnVal;
  }

  //End Program: No methods past this point
  private void endProgram() {
    endProgram = System.currentTimeMillis();
    System.out.println("\n");
    System.out.println("<Program Ending...>");
    /*
    System.out.println("\nClosing time");
    System.out.println("Open all the doors and let you out into the world");
    System.out.println("Closing time");
    System.out.println("Turn the lights up over every boy and every girl.");
    System.out.println("Closing time");
    System.out.println("One last call for alcohol so finish your whiskey or beer.");
    System.out.println("Closing time");
    System.out.println("You don't have to go home but you can't stay here.\n");
    */
    System.out.println("<Run Time: " + ((endProgram-startProgram)*.001) + "s>");
    System.exit(0);
  }

  //Masks
  private int[] maskADD =   {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}; //ADD
  private int[] maskADDI =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ADDI
  private int[] maskADDIU =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ADDIU
  private int[] maskADDU =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //ADDU
  private int[] maskAND =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //AND
  private int[] maskANDI =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ANDI
  private int[] maskBEQ =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BEQ
  private int[] maskBGEZ =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BGEZ
  private int[] maskBGEZAL =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BGEZAL
  private int[] maskBGTZ =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BGTZ
  private int[] maskBLEZ =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BLEZ
  private int[] maskBLTZ =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BLTZ
  private int[] maskBLTZAL =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BLTZAL
  private int[] maskBNE =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BNE
  private int[] maskDIV =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}; //DIV
  private int[] maskJ =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //J
  private int[] maskJAL =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //JAL
  private int[] maskJR =  {1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}; //JR
  private int[] maskLB =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //LB
  private int[] maskLUI =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //LUI
  private int[] maskLW =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //LW
  private int[] maskMFHI =  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //MFHI
  private int[] maskMFLO =  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //MFLO
  private int[] maskMULT =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}; //MULT
  private int[] maskOR =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //OR
  private int[] maskORI =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ORI
  private int[] maskSLL =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1}; //SLL
  private int[] maskSLLV =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1}; //SLLV
  private int[] maskSLT =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //SLT
  private int[] maskSLTI =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SLTI
  private int[] maskSLTIU =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SLTIU
  private int[] maskSLTU =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //SLTU
  private int[] maskSRA =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1}; //SRA
  private int[] maskSRL =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1}; //SRL
  private int[] maskSRLV =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //SRLV
  private int[] maskSUB =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //SUB
  private int[] maskSUBU =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1}; //SUBU
  private int[] maskSW =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SW
  private int[] maskSB =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SB
  private int[] maskSYSCALL =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1}; //SYSCALL
  private int[] maskXOR =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1}; //XOR
  private int[] maskXORI =  {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //XORI

  private int[] instrADD = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0}; //ADD
  private int[] instrADDI = {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ADDI
  private int[] instrADDIU = {0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ADDIU
  private int[] instrADDU = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1}; //ADDU
  private int[] instrAND = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0}; //AND
  private int[] instrANDI = {0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ANDI
  private int[] instrBEQ = {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BEQ
  private int[] instrBGEZ = {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BGEZ
  private int[] instrBGEZAL = {0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BGEZAL
  private int[] instrBGTZ = {0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BGTZ
  private int[] instrBLEZ = {0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BLEZ
  private int[] instrBLTZ = {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BLTZ
  private int[] instrBLTZAL = {0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BLTZAL
  private int[] instrBNE = {0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //BNE
  private int[] instrDIV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,0}; //DIV
  private int[] instrJ = {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //J
  private int[] instrJAL = {0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //JAL
  private int[] instrJR = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0}; //JR
  private int[] instrLB = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //LB
  private int[] instrLUI = {0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //LUI
  private int[] instrLW = {1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //LW
  private int[] instrMFHI = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0}; //MFHI
  private int[] instrMFLO = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0}; //MFLO
  private int[] instrMULT = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0}; //MULT
  private int[] instrOR = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,1}; //OR
  private int[] instrORI = {0,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //ORI
  private int[] instrSLL = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SLL
  private int[] instrSLLV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0}; //SLLV
  private int[] instrSLT = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,1,0}; //SLT
  private int[] instrSLTI = {0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SLTI
  private int[] instrSLTIU = {0,0,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SLTIU
  private int[] instrSLTU = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,1,1}; //SLTU
  private int[] instrSRA = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1}; //SRA
  private int[] instrSRL = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0}; //SRL
  private int[] instrSRLV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0}; //SRLV
  private int[] instrSUB = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0}; //SUB
  private int[] instrSUBU = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1}; //SUBU
  private int[] instrSW = {1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SW
  private int[] instrSB = {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //SB
  private int[] instrSYSCALL = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0}; //SYSCALL
  private int[] instrXOR = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,0}; //XOR
  private int[] instrXORI = {0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //XORI

  private String instrStingADD =  "0000 0000 0000 0000 0000 0000 0010 0000"; //ADD      index=0
  private String instrStingADDI =   "0010 00ss ssst tttt iiii iiii iiii iiii"; //ADDI
  private String instrStingADDIU =   "0010 01ss ssst tttt iiii iiii iiii iiii"; //ADDIU
  private String instrStingADDU =   "0000 00ss ssst tttt dddd d000 0010 0001"; //ADDU
  private String instrStingAND =  "0000 00ss ssst tttt dddd d000 0010 0100"; //AND      index=4
  private String instrStingANDI =   "0011 00ss ssst tttt iiii iiii iiii iiii"; //ANDI
  private String instrStingBEQ =  "0001 00ss ssst tttt iiii iiii iiii iiii"; //BEQ
  private String instrStingBGEZ =   "0000 01ss sss0 0001 iiii iiii iiii iiii"; //BGEZ
  private String instrStingBGEZAL =   "0000 01ss sss1 0001 iiii iiii iiii iiii"; //BGEZAL
  private String instrStingBGTZ =   "0001 11ss sss0 0000 iiii iiii iiii iiii"; //BGTZ      index=9
  private String instrStingBLEZ =  "0001 10ss sss0 0000 iiii iiii iiii iiii"; //BLEZ
  private String instrStingBLTZ =  "0000 01ss sss0 0000 iiii iiii iiii iiii"; //BLTZ
  private String instrStingBLTZAL =  "0000 01ss sss1 0000 iiii iiii iiii iiii"; //BLTZAL
  private String instrStingBNE =  "0001 01ss ssst tttt iiii iiii iiii iiii"; //BNE
  private String instrStingDIV =  "0000 00ss ssst tttt 0000 0000 0001 1010"; //DIV       index=14
  private String instrStingJ =  "0000 10ii iiii iiii iiii iiii iiii iiii"; //J
  private String instrStingJAL =  "0000 11ii iiii iiii iiii iiii iiii iiii"; //JAL
  private String instrStingJR =  "0000 00ss sss0 0000 0000 0000 0000 1000"; //JR
  private String instrStingLB =  "1000 00ss ssst tttt iiii iiii iiii iiii"; //LB
  private String instrStingLUI =  "0011 11-- ---t tttt iiii iiii iiii iiii"; //LUI      index=19
  private String instrStingLW =  "1000 11ss ssst tttt iiii iiii iiii iiii"; //LW
  private String instrStingMFHI =  "0000 0000 0000 0000 dddd d000 0001 0000"; //MFHI
  private String instrStingMFLO =  "0000 0000 0000 0000 dddd d000 0001 0010"; //MFLO
  private String instrStingMULT =  "0000 00ss ssst tttt 0000 0000 0001 1000"; //MULT
  private String instrStingOR =  "0000 00ss ssst tttt dddd d000 0010 0101"; //OR       index=24
  private String instrStingORI =  "0011 01ss ssst tttt iiii iiii iiii iiii"; //ORI
  private String instrStingSLL =  "0000 00ss ssst tttt dddd dhhh hh00 0000"; //SLL
  private String instrStingSLLV =  "0000 00ss ssst tttt dddd d--- --00 0100"; //SLLV
  private String instrStingSLT =  "0000 00ss ssst tttt dddd d000 0010 1010"; //SLT      index=29
  private String instrStingSLTI =  "0010 10ss ssst tttt iiii iiii iiii iiii"; //SLTI
  private String instrStingSLTIU =  "0010 11ss ssst tttt iiii iiii iiii iiii"; //SLTIU
  private String instrStingSLTU =  "0000 00ss ssst tttt dddd d000 0010 1011"; //SLTU
  private String instrStingSRA =  "0000 00-- ---t tttt dddd dhhh hh00 0011"; //SRA
  private String instrStingSRL =  "0000 00-- ---t tttt dddd dhhh hh00 0010"; //SRL      index=34
  private String instrStingSRLV =  "0000 00ss ssst tttt dddd d000 0000 0110"; //SRLV
  private String instrStingSUB =  "0000 00ss ssst tttt dddd d000 0010 0010"; //SUB
  private String instrStingSUBU =  "0000 00ss ssst tttt dddd d000 0010 0011"; //SUBU
  private String instrStingSW =  "1010 11ss ssst tttt iiii iiii iiii iiii"; //SW
  private String instrStingSB =  "1010 00ss ssst tttt iiii iiii iiii iiii"; //SB      index=39
  private String instrStingSYSCALL =  "0000 00-- ---- ---- ---- ---- --00 1100"; //SYSCALL
  private String instrStingXOR =  "0000 00ss ssst tttt dddd d--- --10 0110"; //XOR
  private String instrStingXORI =  "0011 10ss ssst tttt iiii iiii iiii iiii"; //XORI

  private String[] instrStingInstructions = {
    instrStingADD,instrStingADDI,instrStingADDIU,instrStingADDU,instrStingAND,
    instrStingANDI,instrStingBEQ,instrStingBGEZ,instrStingBGEZAL,instrStingBGTZ,
    instrStingBLEZ,instrStingBLTZ,instrStingBLTZAL,instrStingBNE,instrStingDIV,
    instrStingJ,instrStingJAL,instrStingJR,instrStingLB,instrStingLUI,instrStingLW,
    instrStingMFHI,instrStingMFLO,instrStingMULT,instrStingOR,instrStingORI,
    instrStingSLL,instrStingSLLV,instrStingSLT,instrStingSLTI,instrStingSLTIU,
    instrStingSLTU,instrStingSRA,instrStingSRL,instrStingSRLV,instrStingSUB,
    instrStingSUBU,instrStingSW,instrStingSB,instrStingSYSCALL,instrStingXOR,instrStingXORI
  };
  
}//MIPSsim
