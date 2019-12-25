package Basic;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Plant {

    public Plant(String packagesname,List<String> method_invoke,AnalysisBasic a) {

        List<String> uml_class=new ArrayList<String>();

        OutputStream png = null;
        try {
            String name = packagesname.split("\\.")[0];
            png = new FileOutputStream(name + "_class.png");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }


        String source = "@startuml\n";
        source += "skinparam classAttributeIconSize 0\n";     //关闭图片显示

        /***********生成每个类***************/
        int m = 0, n = 0;  //计数
        for (int j = 0; j < AnalysisBasic.getList(a.classname).size(); j++) {

            source+="class ";
            if (AnalysisBasic.getList(a.classname).get(j).contains("<<interface>>")) {  //如果是个接口
                source += AnalysisBasic.getList(a.classname).get(j);
                source += "{\n";
            } else {
                source += AnalysisBasic.getList(a.classname).get(j);    //类名
                source += "{\n";
            }

            for (int i = m; i < AnalysisBasic.getList(a.fieldmodifiers).size(); i++) {   //属性
                if (AnalysisBasic.getList(a.fieldmodifiers).get(i).equals("*")) {
                    m = ++i;
                    break;
                }
                //如果执行到分界线证明该类的属性已经全部输出
                else if (AnalysisBasic.getList(a.fieldmodifiers).get(i).equals("1")) source += "+";
                else if (AnalysisBasic.getList(a.fieldmodifiers).get(i).equals("2")) source += "-";
                else if (AnalysisBasic.getList(a.fieldmodifiers).get(i).equals("4")) source += "#";

                source += AnalysisBasic.getList(a.fieldname).get(i);
                source += ": ";
                source += AnalysisBasic.getList(a.fieldtype).get(i);
                source += "\n";
            }
            for (int i = n; i < AnalysisBasic.getList(a.methodmodifiers).size(); i++) {    //方法
                if (AnalysisBasic.getList(a.methodmodifiers).get(i).equals("*")) {
                    n = ++i;
                    break;
                }
                //如果执行到分界线证明该类的方法已经全部输出
                else if (AnalysisBasic.getList(a.methodmodifiers).get(i).equals("1")) source += "+";
                else if (AnalysisBasic.getList(a.methodmodifiers).get(i).equals("2")) source += "-";
                else if (AnalysisBasic.getList(a.methodmodifiers).get(i).equals("4")) source += "#";
                source += AnalysisBasic.getList(a.methodname).get(i);
//                source+="():";
                source += "(";
                if( AnalysisBasic.getList(a.methodparamtypes).get(i)!="n"){
                    String s=AnalysisBasic.getList(a.methodparamtypes).get(i);
                    int i1=s.lastIndexOf(",");
                    s=s.substring(0,i1);
                    source+=s;
                }
                source+="):";
                source += AnalysisBasic.getList(a.methodreturntype).get(i);
                source += "\n";
            }
            source += "}\n";
        }
        /***********生成每个类***************/

        /***********生成继承关系***************/
        for (int i = 0; i < AnalysisBasic.getList(a.extenders).size(); i++) {
            if (a.extenders.get(i).contains("java")) continue;
            source += a.extenders.get(i).split("@")[2];
            source += "<|--";
            source += a.extenders.get(i).split("@")[1];
            source += "\n";
        }
        /***********生成继承关系***************/

        /***********生成实现关系***************/
        for (int i = 0; i < AnalysisBasic.getList(a.interfacers).size(); i++) {
            source += a.interfacers.get(i).split("@")[2];
            source += "<|..";
            source += a.interfacers.get(i).split("@")[1];
            source += "\n";
        }
        /***********生成实现关系***************/

        /***********生成关联关系***************/
        for (int i = 0; i < AnalysisBasic.getList(a.rs1n).size(); i++)   //一对多关联
        {
            source += a.rs1n.get(i).split("@")[1];
            source += " \"1\"--\"1..*\"";
            source += a.rs1n.get(i).split("@")[2];
            source += "\n";
        }
        for (int i = 0; i < AnalysisBasic.getList(a.rs11).size(); i++)   //一对一关联
        {
            source += a.rs11.get(i).split("@")[1];
            source += " \"1\"--\"1\"";
            source += a.rs11.get(i).split("@")[2];
            source += "\n";
        }
        /***********生成关联关系***************/

        /***********生成依赖关系***************/

        for(String s:method_invoke){
            String umlclass=s.split("->")[0].split(":")[0]+" ..> "+s.split("->")[1].split(":")[0]+"\n";
            if(!uml_class.contains(umlclass))
                uml_class.add(umlclass);
        }

        for(int i=0;i<uml_class.size();i++)
            source +=uml_class.get(i);

        /***********生成依赖关系***************/

        source += "hide <<interface>> circle\n";
        source += "@enduml\n";

        SourceStringReader read = new SourceStringReader(source);
        try {
            String desc = read.generateImage(png);
            System.out.println(desc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
