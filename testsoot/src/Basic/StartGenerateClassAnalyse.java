package Basic;

import java.io.*;
import java.util.Properties;

import static Basic.Main.FileWriters;

public class StartGenerateClassAnalyse {
    public static void main(String []args){
        InputStream in = StartGenerateClassAnalyse.class.getResourceAsStream("/config.properties");
        final Properties p = new Properties();
        try {
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String packagename1 = p.getProperty("PACKAGENAME1");
        String packagename2 = p.getProperty("PACKAGENAME2");
        String path1 = p.getProperty("PATH1");
        String path2 = p.getProperty("PATH2");

        String filename1 = "D:\\APK\\class\\original\\"+"_analysis-2.txt";
        String filename2 = "D:\\APK\\class\\original\\"+ "_classCollection-2.txt";
        String filename3 = "D:\\APK\\class\\change\\"+ "_analysis-2.txt";
        String filename4 = "D:\\APK\\class\\change\\"+ "_classCollection-2.txt";

      //版本1的信息
        AnalysisBasic analysisBasic1=new AnalysisBasic(packagename1,path1);

        try {
            FileOutputStream f1 = new FileOutputStream(new File(filename1));
            FileOutputStream f2 = new FileOutputStream(new File(filename2));
            ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(f1);
            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(f2);
            objectOutputStream1.writeObject(analysisBasic1);
            objectOutputStream2.writeObject(analysisBasic1.classlist);
            objectOutputStream2.writeObject(analysisBasic1.interfacelist);
            f1.close();
            f2.close();
            objectOutputStream1.close();
            objectOutputStream2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        FileWriters("D:\\APK\\class\\original\\"+"_class-2.txt",analysisBasic1.classname);

//        版本2的信息
        AnalysisBasic analysisBasic2=new AnalysisBasic(packagename2,path2);

        try {
            FileOutputStream f3 = new FileOutputStream(new File(filename3));
            FileOutputStream f4 = new FileOutputStream(new File(filename4));
            ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(f3);
            ObjectOutputStream objectOutputStream4 = new ObjectOutputStream(f4);
            objectOutputStream3.writeObject(analysisBasic2);
            objectOutputStream4.writeObject(analysisBasic2.classlist);
            objectOutputStream4.writeObject(analysisBasic2.interfacelist);
            f3.close();
            f4.close();
            objectOutputStream3.close();
            objectOutputStream4.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileWriters("D:\\APK\\class\\change\\"+"_class-2.txt",analysisBasic2.classname);
    }

}
