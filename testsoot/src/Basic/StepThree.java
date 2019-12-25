package Basic;


import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import static Basic.StepTwo.similarityToWeight;
import static Basic.StepTwo.x;
import static Basic.StepTwo.y;

public class StepThree {

    private String packagename1;
    private String packagename2;

    private HashMap<String, Integer> call = new HashMap<>();//记录每个方法的调用次数
    private HashMap<String, Integer> becall = new HashMap<>();//记录每个方法的被调用次数

    private List<MethodInformation> methodCollection1 = new ArrayList<>();//版本1方法集合
    private List<MethodInformation> methodCollection2 = new ArrayList<>();//版本2方法集合

    private HashMap<String, Double> methodMapping = new HashMap<>();//记录版本2与版本1有映射关系的方法
    private HashMap<String, String> classMapping = new HashMap<>();//记录版本2与版本1有映射关系的类
    private List<String> classMappingList = new ArrayList<>();
    private HashMap<String, String> classMapping2 = new HashMap<>();


    public StepThree(String packagename1, String packagename2, List<String> method_call1, List<String> method_call2) {
        this.packagename1 = packagename1;
        this.packagename2 = packagename2;

        //统计每个方法的调用次数和被调用次数
        count(method_call1);
        count(method_call2);

        proprecess(method_call1, methodCollection1);//版本1方法集合
        proprecess(method_call2, methodCollection2);//版本2方法集合

//        for (MethodInformation methodInformation : methodCollection1) {
//            System.out.println(methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")");
//        }
//
//        for (MethodInformation methodInformation : methodCollection2) {
//            System.out.println(methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")");
//        }


        List<MethodInformation> methodCollection1Ectype = new ArrayList<>(methodCollection1);
        List<MethodInformation> methodCollection2Ectype = new ArrayList<>(methodCollection2);


//        HashMap<String, Double> Marix = methodSimilarity2(methodCollection1, methodCollection2);

//        for (int xtempx = 0; xtempx < methodCollection2.size(); xtempx++) {
//            MethodInformation methodInformation = methodCollection2.get(xtempx);
//            String s = methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")";
//            for (Map.Entry entry : Marix.entrySet()) {
//                if (entry.getKey().toString().split("<->")[0].equals(s))
//                    System.out.print(new DecimalFormat("0.00").format(entry.getValue()) + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("---------------------------------------------------------");
        while (!methodCollection2Ectype.isEmpty()&&!methodCollection1Ectype.isEmpty()) {
            HashMap<String, Double> methodSimilarity = methodSimilarity(methodCollection1Ectype, methodCollection2Ectype);

            //版本1和版本2的方法(每次取最高相似度的方法对)
            String methodPair = findHighest(methodSimilarity);

            Double d = Double.parseDouble(methodPair.split("@")[1]);
            String theHighestHost1 = methodPair.split("@")[0].split("<->")[1];
            String theHighestHost2 = methodPair.split("<->")[0];
            methodPair = methodPair.split("@")[0];
            methodMapping.put(methodPair, d);
//            System.out.println("最高相似度方法对：" + methodPair + ":" + d);

            del(methodCollection1Ectype, theHighestHost1);
            del(methodCollection2Ectype, theHighestHost2);

            changeMethodSimilarity(theHighestHost1, theHighestHost2);
            changeClassSimilarity(theHighestHost1, theHighestHost2);

//            for (Map.Entry entry : Marix.entrySet()) {
//                if (entry.getKey().toString().equals(methodPair)) {
//                    double value = (double) entry.getValue();
//                    entry.setValue(d);
//                    //System.out.print(new DecimalFormat("0.00").format(entry.getValue()) + " ");
//                }
//            }

        }
//        System.out.println("---------------------------------------------------");
//
//        for(int xtempx=0;xtempx<analysisBasic2.classlist.size();xtempx++){
//            String s=analysisBasic2.classlist.get(xtempx).classname;
//            for (Map.Entry entry : StepTwo.similarityMap.entrySet()) {
//                if(entry.getKey().toString().split("<->")[0].equals(s))
//                System.out.print(new DecimalFormat("0.00").format(entry.getValue())+" ");
//            }
//            System.out.println();
//        }


//        for (int xtempx = 0; xtempx < methodCollection2.size(); xtempx++) {
//            MethodInformation methodInformation = methodCollection2.get(xtempx);
//            String s = methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")";
//            for (Map.Entry entry : Marix.entrySet()) {
//                if (entry.getKey().toString().split("<->")[0].equals(s))
//                    System.out.print(new DecimalFormat("0.00").format(entry.getValue()) + " ");
//            }
//            System.out.println();
//        }


        for (Map.Entry entry : methodMapping.entrySet()) {
            System.out.println("方法映射：" + entry.getKey());
        }

        List<String> v2 = new ArrayList<>();
        List<String> v1 = new ArrayList<>();
        List<String> temp = new ArrayList<>();
//
        for (Map.Entry entry : StepTwo.similarityMap.entrySet()) {
//            System.out.println("类的相似度：" + entry.getKey() + ":" + entry.getValue());
            if ((double) entry.getValue() >= 0.8) {
                String part1 = entry.getKey().toString().split("<->")[0];
                String part2 = entry.getKey().toString().split("<->")[1];
                v2.add(part1);
                v1.add(part2);
                classMapping.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
////////////////////////////////////////一旦方法存在映射，他们所属的类存在映射///////////////////////////////////
        for (Map.Entry entry : methodMapping.entrySet()) {
            String part1 = entry.getKey().toString().split("<->")[0].split(":")[0];
            String part2 = entry.getKey().toString().split("<->")[1].split(":")[0];
            String s = part1 + "<->" + part2;
            temp.add(s);
        }
        removeSame(temp);
        temp = new ArrayList<String>(new HashSet<String>(temp));

        for (String s : temp) {
            if (!classMapping.containsKey(s)) {
                String part1 = s.split("<->")[0];
                String part2 = s.split("<->")[1];
                v2.add(part1);
                v1.add(part2);
                for (Map.Entry entry2 : StepTwo.similarityMap.entrySet()) {

                    if (entry2.getKey().equals(s))
                        classMapping.put(entry2.getKey().toString(), entry2.getValue().toString());
                }
            }
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
        v2 = findSame(v2);//找出相同，说明版本1对应版本2多个类
        v1 = findSame(v1);//找出相同，说明版本2对应版本1多个类

        v1 = new ArrayList<String>(new HashSet<String>(v1));
        v2 = new ArrayList<String>(new HashSet<String>(v2));
//        for(String s:v2){
//            System.out.println("v2"+s);
//        }
//        for(String s:v1){
//            System.out.println("v1"+s);
//        }
        Iterator<Map.Entry<String, String>> iterator = classMapping.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String part1 = entry.getKey().toString().split("<->")[0];
            String part2 = entry.getKey().toString().split("<->")[1];
            if (!v2.contains(part1) && !v1.contains(part2)) {
                classMappingList.add("[" + part2 + "]<->[" + part1 + "]");
            }
            if (v2.contains(part1)) {
                String s = new String();
                for (Map.Entry entry21 : classMapping.entrySet()) {
                    if (entry21.getKey().toString().split("<->")[0].equals(part1)) {
                        s += "[" + entry21.getKey().toString().split("<->")[1] + "]";
                    }
                }
                classMappingList.add("[" + s + "]<->[" + part1 + "]");
            }
            if (v1.contains(part2)) {
                String s = new String();
                for (Map.Entry entry12 : classMapping.entrySet()) {
                    if (entry12.getKey().toString().split("<->")[1].equals(part2)) {
                        s += "[" + entry12.getKey().toString().split("<->")[0] + "]";
                    }
                }
                classMappingList.add("[" + part2 + "]<->[" + s + "]");
            }
        }
        classMappingList = removeSame(classMappingList);
        classMapping2.put("一对一", "");
        classMapping2.put("一对多", "");
        classMapping2.put("多对一", "");

        for (String s : classMappingList) {
            if (s.contains("<->[["))
                for (Map.Entry entry : classMapping2.entrySet()) {
                    if (entry.getKey().equals("一对多"))
                        if (entry.getValue() == "")
                            entry.setValue(s);
                        else
                            entry.setValue(entry.getValue() + "\n" + s);
                }
            else if (s.contains("]]<->"))
                for (Map.Entry entry : classMapping2.entrySet()) {
                    if (entry.getKey().equals("多对一"))
                        if (entry.getValue() == "")
                            entry.setValue(s);
                        else
                            entry.setValue(entry.getValue() + "\n" + s);
                }
            else
                for (Map.Entry entry : classMapping2.entrySet()) {
                    if (entry.getKey().equals("一对一"))
                        if (entry.getValue() == "")
                            entry.setValue(s);
                        else
                            entry.setValue(entry.getValue() + "\n" + s);
                }
        }

//        for (Map.Entry entry : classMapping2.entrySet()) {
//            System.out.println("类映射(" + entry.getKey() + "):\n" + entry.getValue());
//        }



        try {
//            String name = packagename1.split("\\.")[0];
            String name = "D:\\APK\\mapping\\";
            FileOutputStream f1 = new FileOutputStream(new File(name + "_classMapping-3.txt"));
            ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(f1);
            objectOutputStream1.writeObject(classMapping2);

            FileOutputStream f2 = new FileOutputStream(new File(name + "_methodMapping-3.txt"));
            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(f2);
            objectOutputStream2.writeObject(methodMapping);

            f1.close();
            f2.close();

            writeFile(methodMapping);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFile(Map<String, Double> map) {
        FileWriter fw = null;
        File file = new File("D:\\APK\\mapping\\method-3.txt");
        try {
            if (!file.exists())
                file.createNewFile();
            fw = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            for (Map.Entry entry : map.entrySet()) {
                String s = "{\"" + entry.getKey().toString() + "\":\"" + entry.getValue().toString() + "\"}";
                System.out.println(s);
                bufferedWriter.write(s);
            }

            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //计算调用次数和被调用次数///////////////////////////////////////////////////////////////////////////////////////////////////////
    public void count(List<String> method_call) {
        for (String mc : method_call) {
            String method1 = mc.split("->")[0];//调用的方法
            String method2 = mc.split("->")[1];//被调用的方法
            boolean flag1 = false;
            boolean flag2 = false;
            for (Map.Entry entry : call.entrySet()) {
                if (entry.getKey().equals(method1)) {
                    flag1 = true;
                    entry.setValue((Integer) entry.getValue() + 1);
                }
            }
            if (flag1 == false) {
                call.put(method1, 1);
            }
            for (Map.Entry entry : becall.entrySet()) {
                if (entry.getKey().equals(method2)) {
                    flag2 = true;
                    entry.setValue((Integer) entry.getValue() + 1);
                }
            }
            if (flag2 == false) {
                becall.put(method2, 1);
            }
        }
    }

    //找出版本的所有方法////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void proprecess(List<String> method_call, List<MethodInformation> methodCollection) {
        List<String> temp = new ArrayList<>();
        for (String mc : method_call) {
            String part1 = mc.split("->")[0];//->前 类名+方法
            String part2 = mc.split("->")[1];//->后 类名+方法
            //temp为空，调用方法与被调用方法一致
            if (temp.isEmpty() && part1.equals(part2)) {
                temp.add(part1);
            }
            //temp为空，调用方法与被调用方法不一致
            else if (temp.isEmpty() && !part1.equals(part1)) {
                temp.add(part1);
                temp.add(part2);
            } else {
                //temp不为空，调用方法与被调用方法一致
                if (part1.equals(part2)) {
                    boolean flag = false;
                    for (String s : temp) {
                        if (s.equals(part1))
                            flag = true;
                    }
                    if (flag == false) {
                        temp.add(part1);
                    }
                }
                //temp不为空，调用方法与被调用方法不一致
                else {
                    boolean flag1 = false;
                    boolean flag2 = false;
                    for (String s : temp) {
                        if (s.equals(part1))
                            flag1 = true;
                        if (s.equals(part2))
                            flag2 = true;
                    }
                    if (flag1 == false) {
                        temp.add(part1);
                    }
                    if (flag2 == false)
                        temp.add(part2);
                }
            }
        }
        //格式：Vehicle.information.BusVehicle:float calcRentVehicle(int)
        for (String s : temp) {
            String beCallClass = s.split(":")[0];// 类名
            String beCallMethod = s.split(":")[1];// 方法

            MethodInformation methodInformation = new MethodInformation();
            methodInformation.className = beCallClass;
            methodInformation.returnType = beCallMethod.split(" ")[0];
            methodInformation.methodName = beCallMethod.split(" ")[1].split("\\(")[0];

            if ((beCallMethod.indexOf("(") + 1) == beCallMethod.indexOf(")"))
                methodInformation.paramTypes = new ArrayList<>();
            else {
                String param = beCallMethod.split("\\)")[0].split("\\(")[1];
                String[] paramtemp = param.split(",");
                methodInformation.paramTypes = Arrays.asList(paramtemp);
            }
            //调用次数
            for (Map.Entry entry : call.entrySet()) {
                if (entry.getKey().equals(s)) {
                    methodInformation.callCount = (int) entry.getValue();
                }
            }
            //被调用次数
            for (Map.Entry entry : becall.entrySet()) {
                if (entry.getKey().equals(s)) {
                    methodInformation.beCallCount = (int) entry.getValue();
                }
            }
            List<String> calltemp = new ArrayList<>();
            List<String> becalltemp = new ArrayList<>();
            for (String mc : method_call) {
                String part1 = mc.split("->")[0];//->前 类名+方法
                String part2 = mc.split("->")[1];//->后面 类名+方法
                if (part1.equals(s))//该方法调用其他方法
                    becalltemp.add(part2);
                if (part2.equals(s))//该方法被调用
                    calltemp.add(part1);
            }
            methodInformation.call = removeSame(calltemp);
            methodInformation.becall = removeSame(becalltemp);
            methodCollection.add(methodInformation);
        }
    }

    public ArrayList removeSame(List<String> arr) {
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

    public List<String> findSame(List<String> arr) {
        ArrayList<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (String str : arr) {
            if (builder.indexOf("," + str + ",") > -1) {
                list.add(str);
            } else {
                builder.append(",").append(str).append(",");
            }
        }
        return list;
    }

    //计算相似度//////////////////////////////////////////////////////////////////////////////////////////////////////////
    public HashMap<String, Double> methodSimilarity(List<MethodInformation> methodCollection1, List<MethodInformation> methodCollection2) {
        //已经没有方法可以匹配
//        if (methodCollection1.isEmpty() || methodCollection2.isEmpty())
//            return null;
        int x = methodCollection2.size();
        int y = methodCollection1.size();
        int z = 6;
        double[] weight = new double[]{0.15, 0.2, 0.2, 0.15, 0.15, 0.15};

        //存储版本2所有方法对版本1所有方法的相似度
        double[][][] methodSimilarity = new double[x][z][y];
        //存储版本2所有方法对版本1所有方法加权后的相似度
        double[][] methodSimilarityToWeight = new double[x][y];

        findMethodSimilarity(x, y, z, methodCollection1, methodCollection2, methodSimilarity);

        findMethodSimilarityToWeight(x, y, z, methodSimilarity, methodSimilarityToWeight, weight);


        HashMap<String, Double> methodSimilarityMap = transform(x, y, methodCollection1, methodCollection2, methodSimilarityToWeight);

//        for (Map.Entry entry : methodSimilarityMap.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }
        return methodSimilarityMap;
    }

    public HashMap<String, Double> methodSimilarity2(List<MethodInformation> methodCollection1, List<MethodInformation> methodCollection2) {
        //已经没有方法可以匹配
        if (methodCollection1.isEmpty() || methodCollection2.isEmpty())
            return null;
        int x = methodCollection2.size();
        int y = methodCollection1.size();
        int z = 6;
        double[] weight = new double[]{0.15, 0.2, 0.2, 0.15, 0.15, 0.15};

        //存储版本2所有方法对版本1所有方法的相似度
        double[][][] methodSimilarity = new double[x][z][y];
        //存储版本2所有方法对版本1所有方法加权后的相似度
        double[][] methodSimilarityToWeight = new double[x][y];

        findMethodSimilarity(x, y, z, methodCollection1, methodCollection2, methodSimilarity);

        findMethodSimilarityToWeight(x, y, z, methodSimilarity, methodSimilarityToWeight, weight);

        HashMap<String, Double> methodSimilarityMap = transform2(x, y, methodCollection1, methodCollection2, methodSimilarityToWeight);

        return methodSimilarityMap;
    }


    //相似度矩阵///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void findMethodSimilarity(int x, int y, int z, List<MethodInformation> methodCollection1, List<MethodInformation> methodCollection2, double[][][] methodSimilarity) {

        for (int xtemp = 0; xtemp < x; xtemp++) {

            String m2returnType = null;
            String m1returnType = null;

            String[] m2paramType = null;
            String[] m1paramType = null;

            boolean d = false;
            boolean c = false;
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            MethodInformation m2 = methodCollection2.get(xtemp);
            m2returnType=m2.returnType;
            m2paramType=m2.paramTypes.toArray(new String[m2.paramTypes.size()]);

            for (int ytemp = 0; ytemp < y; ytemp++) {

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                MethodInformation m1 = methodCollection1.get(ytemp);
                m1returnType=m1.returnType;
                m1paramType=m1.paramTypes.toArray(new String[m1.paramTypes.size()]);
                ///返回类型////////////////////////////////////////////////////////////////////////////////////////////////
                if (m1returnType.equals(m2returnType)) {
                    d = true;
                } else {

                    String m2returnType2;
                    String m1returnType2;

                    String m2returnType3;
                    String m1returnType3;

                    //处理ArrayList<类>和类[]
                    m2returnType2 = m2returnType.replace("ArrayList<", "");
                    m2returnType2 = m2returnType2.replace(" >", "");
                    m2returnType2 = m2returnType2.replace("[]", "");
                    m1returnType2 = m1returnType.replace("ArrayList<", "");
                    m1returnType2 = m1returnType2.replace(" >", "");
                    m1returnType2 = m1returnType2.replace("[]", "");

                    m2returnType3 = m2returnType.replace(m2returnType2, "");
                    m1returnType3 = m1returnType.replace(m1returnType2, "");


                    if (m1returnType2.equals(m2returnType2) && m2returnType3.equals(m1returnType3)) {
                        d = true;
                    }
                }
                //返回类型完///////////////////////////////////////////////////////////////////////////////////////////////

                //参数////////////////////////////////////////////////////////////////////////////////////////////////////
                int size = 0;
                if (m2.paramTypes.isEmpty() && m1.paramTypes.isEmpty()) {
                    c = true;
                } else if ((!m2.paramTypes.isEmpty() && m1.paramTypes.isEmpty()) || (m2.paramTypes.isEmpty() && !m1.paramTypes.isEmpty())) {
                    c = false;
                } else {

                    if (m2paramType.length != m1paramType.length) {
                        c = false;
                    } else {
                        for (int i = 0; i < m2paramType.length; i++) {
                            if (m2paramType[i].equals(m1paramType[i])) {
                                size++;
                            } else {
                                String m2paramType2;
                                String m1paramType2;

                                String m2paramType3;
                                String m1paramType3;

                                //处理ArrayList<类>和类[]
                                m2paramType2 = m2paramType[i].replace("ArrayList<", "");
                                m2paramType2 = m2paramType2.replace(" >", "");
                                m2paramType2 = m2paramType2.replace("[]", "");
                                m1paramType2 = m1paramType[i].replace("ArrayList<", "");
                                m1paramType2 = m1paramType2.replace(" >", "");
                                m1paramType2 = m1paramType2.replace("[]", "");

                                m2paramType3 = m2paramType[i].replace(m2paramType2, "");
                                m1paramType3 = m1paramType[i].replace(m1paramType2, "");


                                if (m1paramType2.equals(m2paramType2) && m2paramType3.equals(m1paramType3)) {
                                    size++;
                                }
                            }
                        }
                        if (size == m2paramType.length) {
                            c = true;
                        }
                    }
                }
                //参数完//////////////////////////////////////////////////////////////////////////////////////////////////


                String r1 = m2.className + "<->" + m1.className;
                String r2 = m1.className + "<->" + m2.className;
                double themethodSimilarity =0;
                if(StepTwo.similarityMap.get(r1)!=null)
                    themethodSimilarity=StepTwo.similarityMap.get(r1);
                else if(m2.className.equals(m1.className))
                    themethodSimilarity=1.0;

                //第一个评判因素:类的相似度
                methodSimilarity[xtemp][0][ytemp] = themethodSimilarity;

                //第二个评判因素：调用次数
                if (m1.callCount == m2.callCount)
                    methodSimilarity[xtemp][1][ytemp] = 1.0;
                else
                    methodSimilarity[xtemp][1][ytemp] = 0.0;
                //第三个评判因素：被调用次数
                if (m1.beCallCount == m2.beCallCount)
                    methodSimilarity[xtemp][2][ytemp] = 1.0;
                else
                    methodSimilarity[xtemp][2][ytemp] = 0.0;
                //第四个评判因素：所有调用它的方法中相似方法的占比
                if (!m1.call.isEmpty() && !m2.call.isEmpty() && m1.call.size() == m2.call.size()) {
                    int samenum = 0;
                    for (Map.Entry entry : methodMapping.entrySet()) {
                        for (String s2 : m2.call) {
                            for (String s1 : m1.call) {
                                if (entry.getKey().toString().split("<->")[0].equals(s2) && entry.getKey().toString().split("<->")[1].equals(s1)) {
                                    samenum++;
                                }
                            }
                        }
                    }
                    methodSimilarity[xtemp][3][ytemp] = samenum / m1.call.size();
                } else
                    methodSimilarity[xtemp][3][ytemp] = 0;
                //第五个评判因素：所有调用它的方法中相似方法的占比
                if (!m1.becall.isEmpty() && !m2.becall.isEmpty() && m1.becall.size() == m2.becall.size()) {
                    int samenum = 0;
                    for (Map.Entry entry : methodMapping.entrySet()) {
                        for (String s2 : m2.becall) {
                            for (String s1 : m1.becall) {
                                if (entry.getKey().toString().split("<->")[0].equals(s2) && entry.getKey().toString().split("<->")[1].equals(s1)) {
                                    samenum++;
                                }
                            }
                        }
                    }
                    methodSimilarity[xtemp][4][ytemp] = samenum / m1.becall.size();
                } else
                    methodSimilarity[xtemp][4][ytemp] = 0;
                //第六个评判因素：方法的静态信息
                int methodParam1, methodParam2;//methodParam1标志方法返回类型+参数类型相同，methodParam2标志方法名相同
                methodParam1 = methodParam2 = 0;
                if ((m2.returnType.equals(m1.returnType) || d == true) && (m2.paramTypes.equals(m1.paramTypes) || c == true))
                    methodParam1 = 1;
                if (m2.methodName.equals(m1.methodName))
                    methodParam2 = 1;
                methodSimilarity[xtemp][5][ytemp] = methodParam1 * 0.8 + methodParam2 * 0.2;

            }
        }
    }

    //加权后的矩阵//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void findMethodSimilarityToWeight(int x, int y, int z, double[][][] methodSimilarity, double[][] methodSimilarityToweight, double[] weight) {
        for (int xtemp = 0; xtemp < x; xtemp++) {
            for (int ytemp = 0; ytemp < y; ytemp++) {
                for (int zz = 0; zz < z; zz++) {
                    methodSimilarityToweight[xtemp][ytemp] += methodSimilarity[xtemp][zz][ytemp] * weight[zz];
                }
            }
        }
    }

    //转换成HashMap<String,Double>/////////////////////////////////////////////////////////////////////////////////////////////////
    public HashMap<String, Double> transform(int x, int y, List<MethodInformation> methodCollection1, List<MethodInformation> methodCollection2, double[][] methodSimilarityToWeight) {
        HashMap<String, Double> similarity = new HashMap<>();
        for (int xtemp = 0; xtemp < x; xtemp++) {
            MethodInformation m2 = methodCollection2.get(xtemp);
            String host2 = m2.className + ":" + m2.returnType + " " + m2.methodName + "(" + StringUtils.join(m2.paramTypes, ",") + ")";
            int highest = 0;
            for (int ytemp = 0; ytemp < y; ytemp++) {
                if (methodSimilarityToWeight[xtemp][ytemp] > methodSimilarityToWeight[xtemp][highest])
                    highest = ytemp;
            }
            MethodInformation m1 = methodCollection1.get(highest);
            String host1 = m1.className + ":" + m1.returnType + " " + m1.methodName + "(" + StringUtils.join(m1.paramTypes, ",") + ")";
            String r = host2 + "<->" + host1;
            similarity.put(r, methodSimilarityToWeight[xtemp][highest]);
        }
        return similarity;
    }

    public HashMap<String, Double> transform2(int x, int y, List<MethodInformation> methodCollection1, List<MethodInformation> methodCollection2, double[][] methodSimilarityToWeight) {
        HashMap<String, Double> similarity = new HashMap<>();
        for (int xtemp = 0; xtemp < x; xtemp++) {
            MethodInformation m2 = methodCollection2.get(xtemp);
            String host2 = m2.className + ":" + m2.returnType + " " + m2.methodName + "(" + StringUtils.join(m2.paramTypes, ",") + ")";
            for (int ytemp = 0; ytemp < y; ytemp++) {
                MethodInformation m1 = methodCollection1.get(ytemp);
                String host1 = m1.className + ":" + m1.returnType + " " + m1.methodName + "(" + StringUtils.join(m1.paramTypes, ",") + ")";
                String r = host2 + "<->" + host1;
                similarity.put(r, methodSimilarityToWeight[xtemp][ytemp]);
            }
        }
        return similarity;
    }

    //找出最高相似度的方法对////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String findHighest(HashMap<String, Double> methodSimilarityMap) {
        double highest = 0;
        String methodpair = new String();
        for (Map.Entry entry : methodSimilarityMap.entrySet()) {
            double d = (double) entry.getValue();
            BigDecimal data1 = new BigDecimal(d);
            BigDecimal data2 = new BigDecimal(highest);
            if (data1.compareTo(data2) == 1) {
                methodpair = entry.getKey().toString();
                highest = d;
            }
        }
        methodpair = methodpair + "@" + String.valueOf(highest);
        System.out.println("最高相似方法对："+methodpair);
        return methodpair;

    }

    //去除具有最高相似度的方法/////////////////////////////////////////////////////////////////////////////////////////////
    public void del(List<MethodInformation> methodCollection, String theHighestHost) {
        int index = -1;
        for (MethodInformation methodInformation : methodCollection) {
            String s = methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")";
            if (s.equals(theHighestHost)) {
                index = methodCollection.indexOf(methodInformation);
            }
        }
        if (index != -1)
            methodCollection.remove(index);
    }

    //调整类相似度/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void changeClassSimilarity(String host1, String host2) {
        String host1ClassName = host1.split(":")[0];
        String host2ClassName = host2.split(":")[0];
        String r = host2ClassName + "<->" + host1ClassName;
        for (Map.Entry entry : StepTwo.similarityMap.entrySet()) {
            if (entry.getKey().equals(r)) {
                double preclassSimilarity = (double) entry.getValue();
                entry.setValue((preclassSimilarity + 1) / 2);
                if ((double) entry.getValue() > 1)
                    entry.setValue(1.0);
//                System.out.println("调整" + r + "类相似度：原先" + preclassSimilarity + "现在：" + entry.getValue());
            }
        }
    }

    //调整方法相似度/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void changeMethodSimilarity(String theHighestHost1, String theHighestHost2) {
        for (Map.Entry entry : methodMapping.entrySet()) {
            String host2 = entry.getKey().toString().split("<->")[0];//版本2中已确定映射的方法
            String host1 = entry.getKey().toString().split("<->")[1];//版本1中已确定映射的方法
            List<String> call2 = null, becall2 = null, call1 = null, becall1 = null;

            for (MethodInformation methodInformation : methodCollection2) {
                String s = methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")";
                if (host2.equals(s)) {
                    call2 = new ArrayList<>(methodInformation.call);//版本2已确定映射的方法的调用方法集合
                    becall2 = new ArrayList<>(methodInformation.becall);//版本2已确定映射的方法的被调用方法集合
                }
            }
            for (MethodInformation methodInformation : methodCollection1) {
                String s = methodInformation.className + ":" + methodInformation.returnType + " " + methodInformation.methodName + "(" + StringUtils.join(methodInformation.paramTypes, ",") + ")";
                if (host1.equals(s)) {
                    call1 = new ArrayList<>(methodInformation.call);//版本1已确定映射的方法的调用方法集合
                    becall1 = new ArrayList<>(methodInformation.becall);//版本1已确定映射的方法的被调用方法集合
                }
            }
            if (call1 != null && call2 != null) {
                if (call2.contains(theHighestHost2) && call1.contains(theHighestHost1)) {
                    int size = call2.size();
                    entry.setValue((double) entry.getValue() + 1 / size * 0.15);
                }
            }
            if (becall1 != null && becall2 != null) {
                if (becall2.contains(theHighestHost2) && becall1.contains(theHighestHost1)) {
                    int size = becall2.size();
                    entry.setValue((double) entry.getValue() + 1 / size * 0.15);
                }
            }
        }
    }


    class MethodInformation {
        String className;
        String returnType;
        String methodName;
        List<String> paramTypes;
        int callCount = 0;//调用次数
        int beCallCount = 0;//被调用次数
        List<String> call = new ArrayList<>();//调用它的方法
        List<String> becall = new ArrayList<>();//它调用的方法
    }
}


