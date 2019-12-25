package Basic;


import java.io.*;
import java.util.*;

import gen.lib.common.output__c;
import h.ndata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        InputStream in = Main.class.getResourceAsStream("/config.properties");
        final Properties p = new Properties();
        p.load(in);

        String packagename1 = p.getProperty("PACKAGENAME1");
        String packagename2 = p.getProperty("PACKAGENAME2");

        String filename1 = "D:\\APK\\class\\original\\"+"_analysis-2.txt";
        String filename3 = "D:\\APK\\class\\change\\"+ "_analysis-2.txt";

        FileInputStream fileInputStream1 = new FileInputStream(new File(filename1));
        ObjectInputStream objectInputStream1 = new ObjectInputStream(fileInputStream1);
        AnalysisBasic analysisBasic1 = (AnalysisBasic) objectInputStream1.readObject();

        FileInputStream fileInputStream2 = new FileInputStream(new File(filename3));
        ObjectInputStream objectInputStream2 = new ObjectInputStream(fileInputStream2);
        AnalysisBasic analysisBasic2 = (AnalysisBasic) objectInputStream2.readObject();

        StepOne stepOne = new StepOne(packagename1, packagename2, analysisBasic1, analysisBasic2);
        StepTwo stepTwo = new StepTwo(packagename1, packagename2, analysisBasic1, analysisBasic2);

        ArrayList<String> method_invoke1 = new ArrayList<>();
        ArrayList<String> method_invoke2 = new ArrayList<>();

        //APP调用记录
        method_invoke1 = FileReaders("D:\\APK\\invoke\\original\\reformLog_original-2.txt");
        method_invoke2 = FileReaders("D:\\APK\\invoke\\change\\reformLog_change-2.txt");

        StepThree stepThree = new StepThree(packagename1, packagename2, method_invoke1, method_invoke2);
    }

    public static ArrayList<String> FileReaders(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String str;            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static ArrayList<String> FileWriters(String name,List<String> className){
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileWriter fw = new FileWriter(name);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String s:className){
                s=s.split("<<interface>>")[0]+"\r\n";
//                System.out.print(s);
                bw.write(s);
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
