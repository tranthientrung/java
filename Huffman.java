/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nenhuffman;

import java.awt.Component;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sun.dc.pr.PathFiller;

/**
 *
 * @author No0ne
 */
public class Huffman {
    ArrayList<Node> arrNode;
    ArrayList<Bit> arrBit;
    String readFilePath;
    String writeFilePath;
    String strBit;
    String bitTemp;
    String strDecode;
    Node tree; //cay huffman/nut gốc
    private static final char ENTER=13;
    private static final char CONVERT_ENTER=10;
    private static final int NUM_BIT=8;
    public Huffman(){
        arrBit=new ArrayList<>();
        arrNode=new ArrayList<>();
        tree=new Node();
        readFilePath="";
        writeFilePath="";
        bitTemp="";
        strBit="";
        strDecode="";
    }
    public Huffman(String readFilePath, String writeFilePath){
        arrBit=new ArrayList<>();
        arrNode=new ArrayList<>();
        tree=new Node();
        this.readFilePath=readFilePath;
        this.writeFilePath=writeFilePath;
       bitTemp="";
         strBit="";
         strDecode="";
    }
    
    private String readFile(String filePath) throws IOException{
        //StandardCharsets.UTF_8
        String str=new String(Files.readAllBytes(Paths.get(filePath)),StandardCharsets.UTF_8);
        /*
        String str="";
        File file=new File(filePath);
        BufferedReader read=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
        String temp;
        while((temp=read.readLine()) != null){
            str=str+temp;
            System.out.println(str);
        }
        */
        return str;
    }
    
    private ArrayList<Node> frequencyTable(String filePath, ArrayList<Node> arrayNode) throws IOException{
        
        String str=readFile(filePath); //lay String tu filePath
        
        for(int i=0;i<str.length();i++){
            Node temp=new Node();
            boolean existed=false;
            int j=0;
            char c = str.charAt(i);
            if(c==ENTER){
                continue;
            }
            
            while(j<arrayNode.size()){ 
                if(c == arrayNode.get(j).c){
                    arrayNode.get(j).fre++;
                    
                    existed=true;
                    break;
                }
                j++;
            }
            if(!existed){
                temp.c=c;
                temp.fre=1;
               
                arrayNode.add(temp);
            }
        }
        return arrayNode;
        
    }
    
    private ArrayList<Node> sortFrequencyTable(ArrayList<Node> arr){
        for(int i=arr.size()-1;i>0;i--){
            if(arr.get(i).fre < arr.get(i-1).fre){
                Node temp = new Node();
                temp=arr.get(i);
                arr.set(i, arr.get(i-1));
                arr.set(i-1,temp);
                arr=sortFrequencyTable(arr);
            }
        }
        return arr;
    }
    
    private Node createHuffmanTree(ArrayList<Node> arrNode){
        ArrayList<Node> arr=new ArrayList<Node>(arrNode);
        while(arr.size()!=1){
            Node temp=new Node();
            temp.left=arr.get(0);
            temp.right=arr.get(1);
            temp.fre=temp.left.fre+temp.right.fre;
            arr.remove(1);
            arr.remove(0);
            arr.add(temp);
            arr=sortFrequencyTable(arr);
        }
        return arr.get(0);
    }
    
    
    public void compress() throws IOException{ 
        
        //xu ly filePath
        
       arrNode=frequencyTable(readFilePath,arrNode);
       arrNode=sortFrequencyTable(arrNode);
       
        tree=createHuffmanTree(arrNode);
        
        createArrayBit(arrNode,tree);
        
        strBit=encode(readFilePath,arrBit);
        
        
        
        writeFile(writeFilePath,strBit,arrBit);
        
        JFrame frame=new JFrame();
        JOptionPane.showMessageDialog(frame,"Nen thanh cong");
    }
    
    private void createArrayBit(ArrayList<Node> arrNode,Node tree){
        /*
        for(int i=0;i<arrNode.size();i++){
            Bit temp=new Bit();
            temp.c=arrNode.get(i).c;
            temp.bit=convertCharToBit(temp.c);
            arrBit.add(temp);
        }
        for(int i=0;i<arrBit.size();i++){
            String temp=arrBit.get(i).bit;
            while(temp.length()<NUM_BIT){
                temp="0"+temp;
            }
            arrBit.get(i).bit=temp;
        }
        for(int i=0;i<arrBit.size();i++){
            bitTemp="";
            searchFromTree(arrBit.get(i).c, tree,"");
            arrBit.get(i).bit=bitTemp+arrBit.get(i).bit;
            
        }
        */
         
        for(int i=0;i<arrNode.size();i++){
            Bit bit=new Bit();
            bit.c=arrNode.get(i).c;
            searchFromTree(bit.c, tree, bitTemp);
            bit.bit=bitTemp;
            arrBit.add(bit);
            bitTemp="";
        }
    }
    
    private void searchFromTree(char c,Node tree,String bit){
        if(tree.left != null || tree.right!= null){
            if(tree.left != null){
                
                searchFromTree(c, tree.left,bit+"0");
            }
            if(tree.right != null){
                
                searchFromTree(c, tree.right,bit+"1");
            }
        }
        if(tree.c == c){
            bitTemp=bit;
            return;
        }
    }
     
    private void writeFileTxt(String writeFilePath, String strDecode) throws IOException{
        String text="";
        
        for(int i=0; i<strDecode.length(); i++){
            if(strDecode.charAt(i) == CONVERT_ENTER){
                text+="\r\n";
                
            }else{
                text+=Character.toString(strDecode.charAt(i));
            }
        }
        
        File file=new File(writeFilePath);
        FileWriter fw=new FileWriter(file);
        fw.write(text);
        fw.close();
    }
    
    private void writeFile(String writeFilePath, String strBit, ArrayList<Bit> arrBit){
        
        //save huffman tree
        String strCharBit="";
        for(int i=0;i<arrBit.size();i++){
            if(arrBit.get(i).c== CONVERT_ENTER){
                strCharBit+=arrBit.get(i).bit+"\r\n";
            }else{
                strCharBit+=arrBit.get(i).bit+arrBit.get(i).c;
            }
        }
        strCharBit=strCharBit+"\r\n";
        
        //save to text
        
        long strBitLength=strBit.length(); 
        int bit0=0; //number 0 added 
        while((strBitLength % NUM_BIT) != 0){
            strBit=strBit+"0";
            strBitLength=strBitLength+1;
            bit0=bit0+1;
        }
        String strConverted=convertBitToChar(strBit); //save the string is converted from strBit
        String strWrite=strCharBit+bit0+strConverted;
        try{
            File f=new File(writeFilePath);
            FileWriter fw=new FileWriter(f);
            fw.write(strWrite);
            fw.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        //save binary string
    }
    
    private String convertBitToChar(String strBit){
        String strConverted="";
        String binary=""; // save binary from NUM_BIT first char of strBit
        char c='\0'; //save char from binary
        while(true){
            binary=strBit.substring(0,NUM_BIT);
            c=(char) Integer.parseInt(binary, 2);
            strConverted+=Character.toString(c);
            if(strBit.length() == NUM_BIT){
                break;
            }    
            strBit=strBit.substring(NUM_BIT);
            
            
        }
        return strConverted;
    }
    
    private String convertCharToBit(String strChar){
        int numAdded=Integer.parseInt(Character.toString(strChar.charAt(0)));
        String strBit="";
        String strBinary="";
        for(int i=1; i<strChar.length(); i++){
            strBinary=Integer.toBinaryString((int)strChar.charAt((int) i));
            while(strBinary.length() < NUM_BIT){
                strBinary="0"+strBinary;
            }
            strBit+=strBinary;
            
        }
        strBit=strBit.substring(0, strBit.length() - numAdded);
        
        return strBit;
    }
    
    /*
    private void writeFileHuff(String writeFilePath,String strBit,ArrayList<Bit> arrBit){
        String strCharBit="";
        for(int i=0;i<arrBit.size();i++){
            if(arrBit.get(i).c== CONVERT_ENTER){
                strCharBit+=arrBit.get(i).bit+"\r\n";
            }else{
                strCharBit+=arrBit.get(i).bit+arrBit.get(i).c;
            }
            
        }
        String strWrite=strCharBit+"\r\n"+strBit;
        try{
            
            File f=new File(writeFilePath);
            FileWriter fw=new FileWriter(f);
            
            fw.write(strWrite);
            fw.close();
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    */
    
    private String encode(String filePath, ArrayList<Bit> arrBit) throws IOException{
        String str=readFile(filePath);
        String strBit="";
        for(int i=0;i<str.length();i++){
            int j=0;
            char c=str.charAt(i);
            if(c==(char)ENTER){
                continue;
            }
            while(c != arrBit.get(j).c){
                j++;
            }
            strBit+=arrBit.get(j).bit;
        }
        return strBit;
    }
    
    
    
    public void printArray() throws IOException{
        for(int i=0;i<arrNode.size();i++){
            char temp=arrNode.get(i).c;
            System.out.println(temp + " fre: "+arrNode.get(i).fre);
        }
        
        //printTree(tree);
        printArrBit(arrBit);
        printBit(strBit);
        printStr(readFilePath);
            
    }
    
    private void printTree(Node tree) throws IOException{
        Node temp=tree;
        if(temp.left==null && temp.right==null){
            char tempc=temp.c;
            System.out.println(tempc+" "+temp.fre);
            return;
        }
        printTree(temp.left);
        printTree(temp.right);
        
    }
    
    private void printArrBit(ArrayList<Bit> arrBit){
        for(int i=0;i<arrBit.size();i++){
            System.out.println(arrBit.get(i).bit +" "+ arrBit.get(i).c);
        }
    }
    
    private void printBit(String strBit){
        System.out.println(strBit);
    }
    private void printStr(String readFilePath) throws IOException{
        System.out.println(readFile(readFilePath).toString());
    }
    
    public void decompression() throws IOException{
        String strCharBit=readFile(readFilePath); //read file
        
        arrBit=createArrayBit(strCharBit); //save binary tree into arrBit and save character string into strBit 
        
        tree=createHuffmanTreeFromArrayBit(arrBit);
        
        
        decode(strBit, tree,tree,0);//giai ma chuoi bit dua vao strDecode
        writeFileTxt(writeFilePath, strDecode);
        JFrame frame=new JFrame();
        JOptionPane.showMessageDialog(frame,"Giai nen thanh cong");
        
    }
    
    private void decode(String strBit, Node tree, Node treeRoot, int i){
        
        if(i<strBit.length()){
            if(tree.left == null && tree.right == null){
                strDecode+=Character.toString(tree.c);
                decode(strBit,treeRoot,treeRoot, i);
            }
            else if(strBit.charAt(i) == '0'){
                decode(strBit,tree.left, treeRoot, i+1);
            } else{
                decode(strBit,tree.right, treeRoot, i+1);
            }
        }else{
            strDecode+=Character.toString(tree.c);
        }
    }
    
    private Node createHuffmanTreeFromArrayBit(ArrayList<Bit> arrBit){
        Node tree=new Node();
        for(int i=0;i<arrBit.size();i++){
            String bit=arrBit.get(i).bit;
            char c=arrBit.get(i).c;
            createTree(c, tree, bit, 0, bit.length());
        }
        
        return tree;
    }

    private void createTree(char c, Node tree, String bit, int start, int end){
        
        if(start < end){
            char bitAtHere=bit.charAt(start);
            if(bitAtHere == '0'){
                if(tree.left==null){
                    tree.left=new Node();
                }
                createTree(c,tree.left,bit,start + 1,end);
            }
            if(bitAtHere == '1'){
                if(tree.right==null){
                    tree.right=new Node();
                }
                createTree(c,tree.right,bit,start + 1,end);
            }
        }else{
            tree.c=c;
        } 
    }
    
    private ArrayList<Bit> createArrayBit(String strCharBit){
        ArrayList<Bit> arrlBit=new ArrayList<>();
        int i=0;
        Bit temp;
        while(true){
            temp=new Bit();
            while(strCharBit.charAt(i) == '0' || strCharBit.charAt(i) == '1'){
                temp.bit+=strCharBit.charAt(i);
                i++;
            }
            if(strCharBit.charAt(i)==ENTER){
                  i++;
            }
            temp.c=strCharBit.charAt(i);
            arrlBit.add(temp);
            i++;
            if(strCharBit.charAt(i) == ENTER){
                strBit=createStrBit(strCharBit,i); //lay ra chuoi nhi phan
                 break;
            }
        }
        return arrlBit;
    }
    
    //lay chuoi nhi phan
    private String createStrBit(String strCharBit, int i){
        String strBit="";
        String strChar=strCharBit.substring(i+2);
        strBit=convertCharToBit(strChar);
        return strBit;
    }
    
    
   
}
