package Basic;


import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static Basic.StepOne.onetoone;;

public class StepTwo {

    private String packagename1;
    private String packagename2;

    private AnalysisBasic analysisbasic1;
    private AnalysisBasic analysisbasic2;

    public static double[][][] classSimilarity = null;
    public static double[] weight = new double[]{0.1, 0.2, 0.2, 0.25, 0.25};//类名，属性个数，方法个数，属性信息相似度，方法信息相似度
    public static double[][] similarityToWeight = null;
    public static HashMap<String, Double> similarityMap = new HashMap<>();

    public static int x, y;

    public StepTwo(String packagename1, String packagename2, AnalysisBasic analysisbasic1, AnalysisBasic analysisbasic2) {

        this.packagename1 = packagename1;
        this.packagename2 = packagename2;

        this.analysisbasic1 = analysisbasic1;
        this.analysisbasic2 = analysisbasic2;


        int x = analysisbasic2.classlist.size();
        int y = analysisbasic1.classlist.size();
        int z = 5;


        classSimilarity = new double[x][z][y];
        similarityToWeight = new double[x][y];


        findClassSimilarity(x, y, z);
        findSimilarityToWeight(x, y, z);
        similarityMap = transform(x, y);

//        for(int xxx=0;xxx<x;xxx++){
//            for(int zzz=0;zzz<z;zzz++){
//                for(int yyy=0;yyy<y;yyy++){
//                    System.out.print(classSimilarity[xxx][zzz][yyy]+"\t");
//                }
//                System.out.println();
//            }
//            System.out.println("--------------------------------");
//        }

//        for(int xxx=0;xxx<x;xxx++){
//            for(int yyy=0;yyy<y;yyy++){
//                System.out.print(new DecimalFormat("0.00").format(similarityToWeight[xxx][yyy])+" ");
//            }
//            System.out.println();
//        }

        for(Map.Entry entry:similarityMap.entrySet()){
            System.out.println("初步类相似度："+entry.getKey()+":"+entry.getValue());
        }

//        for(int xxx=0;xxx<analysisbasic2.classlist.size();xxx++){
//            String s=analysisbasic2.classlist.get(xxx).classname;
//
//            for (Map.Entry entry : StepTwo.similarityMap.entrySet()) {
//                if(entry.getKey().toString().split("<->")[0].equals(s))
//                System.out.print(new DecimalFormat("0.00").format(entry.getValue())+" ");
//            }
//            System.out.println();
//        }

    }


    public void findClassSimilarity(int x, int y, int z) {
        for (int xx = 0; xx < x; xx++) {
            boolean issuper2 = false;
            if (analysisbasic2.classlist.get(xx).classname.contains("<<super>>")) {
                issuper2 = true;

            }
            int fnum2, mnum2;//版本2该类的属性个数、方法个数

            if (analysisbasic2.classlist.get(xx).fields.isEmpty()) {
                fnum2 = 0;//版本2该类没有属性
            } else {
                fnum2 = analysisbasic2.classlist.get(xx).fields.size();
            }

            if (analysisbasic2.classlist.get(xx).methods.isEmpty()) {
                mnum2 = 0;//版本2该类没有方法
            } else {
                mnum2 = analysisbasic2.classlist.get(xx).methods.size();
            }

            for (int yy = 0; yy < y; yy++) {
                boolean issuper1 = false;
                if (analysisbasic1.classlist.get(yy).classname.contains("<<super>>")) {
                    issuper1 = true;

                }
                if ((issuper1 == true && issuper2 == false) || (issuper2 == true && issuper1 == false)) {
                    classSimilarity[xx][0][yy] = classSimilarity[xx][1][yy] = classSimilarity[xx][2][yy] = classSimilarity[xx][3][yy] = classSimilarity[xx][4][yy] = 0.0;
                    continue;
                }
                int fnum1, mnum1;//版本1该类的属性个数、方法个数
                if (analysisbasic1.classlist.get(yy).fields.isEmpty()) {
                    fnum1 = 0;//版本1该类没有属性
                } else {
                    fnum1 = analysisbasic1.classlist.get(yy).fields.size();
                }
                if (analysisbasic1.classlist.get(yy).methods.isEmpty()) {
                    mnum1 = 0;//版本1该类没有方法
                } else {
                    mnum1 = analysisbasic1.classlist.get(yy).methods.size();
                }

                int f, m;//相同的属性，方法个数 属性（属性类型 属性名） 方法（方法返回类型 方法名（参数列表类型）
                f = m = 0;
//属性////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (fnum1 == 0 && fnum2 == 0) {
                    f = 0;
                }
                if ((fnum1 == 0 && fnum2 != 0) || (fnum1 != 0 && fnum2 == 0)) {
                    f = -1;
                } else {
                    for (Class.Field ftemp2 : analysisbasic2.classlist.get(xx).fields) {
//                        String ftemp2type = "ftemp2type";
//                        String ftemp1type = "ftemp1type";

                        String ftemp2type = ftemp2.type;

                        boolean b = false;
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        if (ftemp2.type.contains(packagename2)) {
                            int ii2 = ftemp2.type.lastIndexOf(".") + 1;
                            ftemp2type = ftemp2.type.substring(ii2);

                        }
                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        for (Class.Field ftemp1 : analysisbasic1.classlist.get(yy).fields) {

                            if (!ftemp1.name.equals(ftemp1.name)) {//属性名不同，跳过循环
                                continue;
                            }
                            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                            String ftemp1type = ftemp1.type;

//                            if (ftemp1.type.contains(packagename1)) {
//                                int ii1 = ftemp1.type.lastIndexOf(".") + 1;
//                                ftemp1type = ftemp1.type.substring(ii1);
//
//                            }
                            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                            if (ftemp1type.equals(ftemp2type)) {
                                b = true;
                            } else {

                                String ftemp2type2 = "";
                                String ftemp1type2 = "";

                                String ftemp2type3 = "";
                                String ftemp1type3 = "";

                                ftemp2type2 = ftemp2type.replace("ArrayList<", "");
                                ftemp2type2 = ftemp2type2.replace(" >", "");
                                ftemp2type2 = ftemp2type2.replace("[]", "");
                                ftemp1type2 = ftemp1type.replace("ArrayList<", "");
                                ftemp1type2 = ftemp1type2.replace(" >", "");
                                ftemp1type2 = ftemp1type2.replace("[]", "");

                                ftemp2type3 = ftemp2type.replace(ftemp2type2, "");
                                ftemp1type3 = ftemp1type.replace(ftemp1type2, "");
                                for (String otmep : onetoone) {
                                    if (otmep.contains(ftemp2type) && otmep.contains(ftemp1type) && ftemp2type3.equals(ftemp1type3)) {
                                        b = true;
                                    }
                                }
                            }
                            if ((ftemp2.type.equals(ftemp2.type) || b == true) && ftemp2.name.equals(ftemp1.name)) {
                                f++;
                            }
                        }
                    }
                }
//属性完/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//方法///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (mnum1 == 0 && mnum2 == 0) {
                    m = 0;
                }
                if ((mnum1 == 0 && mnum2 != 0) || (mnum1 != 0 && mnum2 == 0)) {
                    m = -1;
                } else {
                    for (Class.Method mtemp2 : analysisbasic2.classlist.get(xx).methods) {
//                        String mtemp2returntype = "mtemp2returntype";
//                        String mtemp1returntype = "mtemp1returntype";

                        String mtemp2returntype = mtemp2.returntype;


                        String[] mtemp2paramtype = null;
                        String[] mtemp1paramtype = null;

                        boolean d = false;
                        boolean c = false;
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                        if (mtemp2.returntype.contains(packagename2)) {
//                            int ii2 = mtemp2.returntype.lastIndexOf(".") + 1;
//                            mtemp2returntype = mtemp2.returntype.substring(ii2);
//
//                        }

                        int xiabiao2 = 0;
                        mtemp2paramtype = new String[mtemp2.paramtypes.size()];
                        for (String ptemp2 : mtemp2.paramtypes) {
//                            if (ptemp2.contains(packagename2)) {
//                                int iii2 = ptemp2.lastIndexOf(".") + 1;
//                                mtemp2paramtype[xiabiao2] = ptemp2.substring(iii2);
//                            } else {
                            mtemp2paramtype[xiabiao2] = ptemp2;
//                            }
                            xiabiao2++;
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        for (Class.Method mtemp1 : analysisbasic1.classlist.get(yy).methods) {

                            if (!mtemp1.name.equals(mtemp2.name)) {
                                continue;
                            }

                            String mtemp1returntype = mtemp1.returntype;
                            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//                            if (mtemp1.returntype.contains(packagename1)) {
//                                int ii1 = mtemp1.returntype.lastIndexOf(".") + 1;
//                                mtemp1returntype = mtemp1.returntype.substring(ii1);
//
//                            }

                            int xiabiao1 = 0;
                            mtemp1paramtype = new String[mtemp1.paramtypes.size()];
                            for (String ptemp1 : mtemp1.paramtypes) {
//                                if (ptemp1.contains(packagename1)) {
//                                    int iii1 = ptemp1.lastIndexOf(".") + 1;
//                                    mtemp1paramtype[xiabiao1] = ptemp1.substring(iii1);
//                                } else {
                                mtemp1paramtype[xiabiao1] = ptemp1;
//                                }
                                xiabiao1++;
                            }
                            ///返回类型////////////////////////////////////////////////////////////////////////////////////////////////
                            if (mtemp1returntype.equals(mtemp2returntype)) {
                                d = true;
                            } else {

                                String mtemp2returntype2 = "";
                                String mtemp1returntype2 = "";

                                String mtemp2returntype3 = "";
                                String mtemp1returntype3 = "";

                                mtemp2returntype2 = mtemp2returntype.replace("ArrayList<", "");
                                mtemp2returntype2 = mtemp2returntype2.replace(" >", "");
                                mtemp2returntype2 = mtemp2returntype2.replace("[]", "");
                                mtemp1returntype2 = mtemp1returntype.replace("ArrayList<", "");
                                mtemp1returntype2 = mtemp1returntype2.replace(" >", "");
                                mtemp1returntype2 = mtemp1returntype2.replace("[]", "");

                                mtemp2returntype3 = mtemp2returntype.replace(mtemp2returntype2, "");
                                mtemp1returntype3 = mtemp1returntype.replace(mtemp1returntype2, "");

                                for (String otmep : onetoone) {
                                    if (otmep.contains(mtemp1returntype2) && otmep.contains(mtemp2returntype2) && mtemp2returntype3.equals(mtemp1returntype3)) {
                                        d = true;
                                    }
                                }
                            }
                            //返回类型完///////////////////////////////////////////////////////////////////////////////////////////////

                            //参数////////////////////////////////////////////////////////////////////////////////////////////////////
                            int size = 0;
                            if (mtemp2.paramtypes.isEmpty() && mtemp1.paramtypes.isEmpty()) {
                                c = true;

                            } else if ((!mtemp2.paramtypes.isEmpty() && mtemp1.paramtypes.isEmpty()) || (mtemp2.paramtypes.isEmpty() && !mtemp1.paramtypes.isEmpty())) {
                                c = false;
                                continue;
                            } else {

                                if (mtemp2paramtype.length != mtemp1paramtype.length) {
                                    c = false;
                                    continue;
                                } else {
                                    for (int i = 0; i < mtemp2paramtype.length; i++) {
//
                                        if (mtemp2paramtype[i].equals(mtemp1paramtype[i])) {
                                            size++;
                                        } else {
                                            String mtemp2paramtype2 = "";
                                            String mtemp1paramtype2 = "";

                                            String mtemp2paramtype3 = "";
                                            String mtemp1paramtype3 = "";


                                            mtemp2paramtype2 = mtemp2paramtype[i].replace("ArrayList<", "");
                                            mtemp2paramtype2 = mtemp2paramtype2.replace(" >", "");
                                            mtemp2paramtype2 = mtemp2paramtype2.replace("[]", "");
                                            mtemp1paramtype2 = mtemp1paramtype[i].replace("ArrayList<", "");
                                            mtemp1paramtype2 = mtemp1paramtype2.replace(" >", "");
                                            mtemp1paramtype2 = mtemp1paramtype2.replace("[]", "");

                                            mtemp2paramtype3 = mtemp2paramtype[i].replace(mtemp2paramtype2, "");
                                            mtemp1paramtype3 = mtemp1paramtype[i].replace(mtemp1paramtype2, "");

                                            for (String otmep : onetoone) {
                                                if (otmep.contains(mtemp1paramtype2) && otmep.contains(mtemp2paramtype2) && mtemp2paramtype3.equals(mtemp1paramtype3)) {
                                                    size++;
                                                }
                                            }
                                        }
                                    }
                                    if (size == mtemp2paramtype.length) {
                                        c = true;
                                    }
                                }
                            }
                            //参数完//////////////////////////////////////////////////////////////////////////////////////////////////
                            if ((mtemp2.returntype.equals(mtemp1.returntype) || d == true) && mtemp2.name.equals(mtemp1.name) && (mtemp2.paramtypes.equals(mtemp1.paramtypes) || c == true)) {
                                m++;
                            }
                        }
                    }
                }
//方法完//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                int i2 = analysisbasic2.classlist.get(xx).classname.lastIndexOf(".") + 1;
                int i1 = analysisbasic1.classlist.get(yy).classname.lastIndexOf(".") + 1;
                if (analysisbasic2.classlist.get(xx).classname.substring(i2).equals(analysisbasic1.classlist.get(yy).classname.substring(i1))) {
                    classSimilarity[xx][0][yy] = (double) 1;
                } else {
                    classSimilarity[xx][0][yy] = (double) 0;
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (fnum1 == 0 && fnum2 == 0) {//若两个类均没有属性，则属性个数相似度视为1
                    classSimilarity[xx][1][yy] = (double) 1;
                } else {//否则属性个数相似度=min{fnum1,fnum2}/max{fnum1,fnum2}
                    classSimilarity[xx][1][yy] = fnum1 > fnum2 ? ((double) fnum2 / (double) fnum1) : ((double) fnum1 / (double) fnum2);
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (mnum1 == 0 && mnum2 == 0) {//若两个类均没有方法，则方法个数相似度视为1
                    classSimilarity[xx][2][yy] = (double) 1;
                } else {//否则属性个数相似度=min{mnum1,mnum2}/max{mnum1,mnum2}
                    classSimilarity[xx][2][yy] = mnum1 > mnum2 ? ((double) mnum2 / (double) mnum1) : ((double) mnum1 / (double) mnum2);
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (f == -1 || f == 0) {
                    classSimilarity[xx][3][yy] = (double) 0;
                } else {
                    classSimilarity[xx][3][yy] = ((double) f / (double) fnum1 + (double) f / (double) fnum2) / 2;
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (m == -1 || m == 0) {
                    classSimilarity[xx][4][yy] = (double) 0;
                } else {
                    classSimilarity[xx][4][yy] = ((double) m / (double) mnum1 + (double) m / (double) mnum2) / 2;
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            }

        }
    }


    public void findSimilarityToWeight(int x, int y, int z) {
        for (int xx = 0; xx < x; xx++) {
            for (int yy = 0; yy < y; yy++) {
                for (int zz = 0; zz < z; zz++) {
                    similarityToWeight[xx][yy] += classSimilarity[xx][zz][yy] * weight[zz];
                }
            }
        }
//        for (int xx = 0; xx < x; xx++) {
//            double total = 0.0;
//            for (int yy = 0; yy < y; yy++) {
//                total += similarityToWeight[xx][yy];
//            }
//            for (int yy = 0; yy < y; yy++) {
//                similarityToWeight[xx][yy] = similarityToWeight[xx][yy] /total;
//            }
//        }
    }

    public HashMap<String, Double> transform(int x, int y) {
        HashMap<String, Double> similarity = new HashMap<>();
        for (int xx = 0; xx < x; xx++) {
            for (int yy = 0; yy < y; yy++) {
                String r = analysisbasic2.classlist.get(xx).classname + "<->" + analysisbasic1.classlist.get(yy).classname;
                similarity.put(r, similarityToWeight[xx][yy]);
            }
        }
        return similarity;
    }
}
