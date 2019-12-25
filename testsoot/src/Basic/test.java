package Basic;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class test {
    public static void main(String[] args){
        ArrayList<String> addSchedule=new ArrayList<>();
        addSchedule=FileReaders("E:\\APK\\invoke\\original\\reformLog_original-2.txt");
//        addSchedule=FileReaders("E:\\APK\\invoke\\change\\reformLog_change-2.txt");
        addSchedule=removeSame(addSchedule);
        for(String s:addSchedule){
            System.out.println(s);
        }


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

    public static ArrayList removeSame(List<String> arr) {
        ArrayList<String> list = new ArrayList<>();
        Iterator<String> it = arr.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        return list;
    }
}