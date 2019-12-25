package Basic;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.options.Options;
import soot.util.Chain;



public class AnalysisBasic implements Serializable {
	//soot分析初始化所需参数
    private static boolean SOOT_INITIALIZED=false;
    public final static String androidJAR="D:\\Android\\sdk\\platforms\\android-28\\android.jar";

    public List<Class> classlist = new ArrayList<>();  //普通类
    public List<Class> interfacelist = new ArrayList<>();//接口

    public List<String> classname = new ArrayList<String>();  //保存类

    public List<String> fieldmodifiers = new ArrayList<String>();  //保存属性权限
    public List<String> fieldname = new ArrayList<String>();  //保存属性名
    public List<String> fieldtype = new ArrayList<String>();  //保存属性类型

    public List<String> methodmodifiers = new ArrayList<String>();  //保存方法权限
    public List<String> methodname = new ArrayList<String>();  //保存方法名
    public List<String> methodreturntype = new ArrayList<String>();  //保存方法类型
    public List<String> methodparamtypes = new ArrayList<>();//保存方法参数类型

    public List<String> extenders = new ArrayList<String>();  //保存继承关系
    public List<String> interfacers = new ArrayList<String>();  //保存接口关系

    public List<String> rs1n = new ArrayList<String>();  //保存一对多关联关系
    public List<String> rs11 = new ArrayList<String>();  //保存一对一关联关系

    public static void setList(List<String> listname, String s) {
        listname.add(s);
    }

    public static List<String> getList(List<String> listname) {
        return listname;
    }

    public AnalysisBasic(String packagesname,String path) {
        System.out.println("---------------------------静态分析-----------------------------");
        initial(path);//Soot的一些配置
        Chain<SootClass> classes = null;//类链表
        classes = Scene.v().getApplicationClasses();   //加载所有类
        Iterator<SootClass> cit = classes.iterator();//类的迭代器
        for (; cit.hasNext(); ) {
            SootClass appclass = cit.next();
            Class c = new Class();
            int n = 0;  //标记
//            System.out.println(packagesname.split(" ").length);
            for (int m = 0; m < packagesname.split(" ").length; m++) {
                if (appclass.getName().contains(packagesname.split(" ")[m])) {
                    n = 1;
                    break;
                }
            }

            if (n == 0) continue;//该类不是软件系统的类，不保存
//            if(appclass.getName().contains("$")||appclass.getName().equals(packagesname+"R")||appclass.getName().equals(packagesname+"BuildConfig")||appclass.getName().equals(packagesname+"Manifest")) continue;
            else if (appclass.isInterface()) {
                c.classname = appclass.getName() + "<<interface>>";
                setList(classname, appclass.getName() + " <<interface>>");
            } else {
                c.classname = appclass.getName();
                setList(classname, appclass.getName());
            }

/////////////////////////////属性//////////////////////////////////////////////////////////////////////////////////////////////////
            Iterator<SootField> fieldIt = appclass.getFields().iterator();//获取类中的相关的属性
            while (fieldIt.hasNext()) {
                SootField s = fieldIt.next();
                String name = s.getName();                   //属性名
                String type = s.getType().toString();        //属性类型
                int modifiers = s.getModifiers();            //属性权限 返回值 1：public；返回值 2：private；返回值 4：protected

                if(name.contains("$"))
                    continue;
                //////////////////按顺序保存////////////////////////
                Class.Field f = c.new Field();
                f.modifiers = String.valueOf(modifiers);
                f.type = type;
                f.name = name;
                c.fields.add(f);      //保存属性
                //////////////////////////////////////////////////
                setList(fieldmodifiers, String.valueOf(modifiers));      //保存属性权限
                setList(fieldname, name);                                //保存属性名
                setList(fieldtype, type);                                 //保存属性类型

                //////////////////若有关联关系则类B是类A的属性//////////////
                for (int m = 0; m < packagesname.split(" ").length; m++) {
                    if (type.contains(packagesname.split(" ")[m])) {   //判断是否存在关联关系
                        if (type.contains("[]")) {  //判断是否为1对多
                            String sub = type.split("\\[")[0];
                            if (!rs1n.contains("@" + appclass.getName() + "@" + sub))
                                setList(rs1n, "@" + appclass.getName() + "@" + sub);
                        } else {
                            if (!rs11.contains("@" + appclass.getName() + "@" + type))
                                setList(rs11, "@" + appclass.getName() + "@" + type);  //1对1
                        }
                    }
                }

            }
            /////////////////类与类属性之间的分界///////////////
            setList(fieldmodifiers, "*");
            setList(fieldname, "*");
            setList(fieldtype, "*");

/////////////////////////////方法/////////////////////////////////////////////////////////////////////////////////////////////////
            Iterator<SootMethod> methodIt = appclass.getMethods().iterator();//获取类中的相关的方法
            while (methodIt.hasNext()) {
                SootMethod s = methodIt.next();
                String name = s.getName();             //方法名
                Type returntype = s.getReturnType();         //返回类型
                List<Type> paramtypes = s.getParameterTypes();//参数列表
                int modifiers = s.getModifiers();      //方法权限
                if (name.equals("<init>")) continue;  //不记录构造函数

                if (modifiers != 1 && modifiers != 2 && modifiers != 4 && modifiers != 0)
                    modifiers = 1;//方法默认为public
                if(name.contains("$"))
                    continue;
                //////////////////按顺序保存////////////////////////
                Class.Method m = c.new Method();
                m.modifiers = String.valueOf(modifiers);
                m.returntype = String.valueOf(returntype);
                m.name = name;
                for (Type ttemp : paramtypes)
                    m.paramtypes.add(String.valueOf(ttemp));
                c.methods.add(m);      //保存方法

                ////////////////////////////////////////////////////
                if (modifiers != 1 || modifiers != 2 || modifiers != 4 || modifiers != 0)
                    setList(methodmodifiers, String.valueOf(1));//方法默认为public
                else
                    setList(methodmodifiers, String.valueOf(modifiers));
                setList(methodreturntype, String.valueOf(returntype));
                setList(methodname, name);
                if (paramtypes.isEmpty()) {
                    setList(methodparamtypes, "n");
//                    setList(methodparamtypes, "/");
                } else {
                    String tempString ="";
                    for (Type t : paramtypes) {
                        tempString+=String.valueOf(t)+",";
                    }
                    setList(methodparamtypes, tempString);
//                    setList(methodparamtypes, "/");//同一个类的方法用“/”隔开
                }
            }
            //////////////////类与类方法之间的分界//////////////
            setList(methodmodifiers, "*");
            setList(methodname, "*");
            setList(methodreturntype, "*");
            setList(methodparamtypes, "*");


            if (c.classname.contains("<<interface>>"))
                interfacelist.add(c);//接口
            else
                classlist.add(c);//普通的类

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            SootClass superclass = appclass.getSuperclass();    //获取某个类的父类
            setList(extenders, "@" + appclass.getName() + "@" + superclass.getName());//格式@子类@父类

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Chain<SootClass> ci = appclass.getInterfaces();   //获取某个类的接口(Java可实现多接口）
            Iterator<SootClass> ii = ci.iterator();
            for (; ii.hasNext(); ) {
                SootClass interfaces = ii.next();
                setList(interfacers, "@" + appclass.getName() + "@" + interfaces.getName());//格式@子类@接口
            }
        }

        for (String s : extenders) {
            String superclass = s.substring(s.lastIndexOf("@") + 1);
            for (Class ctemp : classlist) {
                if (ctemp.classname.equals(superclass)) {
                    classlist.remove(ctemp);
                    ctemp.classname = ctemp.classname + "<<super>>";
                    classlist.add(ctemp);
                    break;
                }

            }
        }
        
        
        

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for(Class ctemp:classlist){
            System.out.print("类名："+ctemp.classname);
            for(Class.Field ftemp:ctemp.fields)
                System.out.print("属性："+ftemp.modifiers+"/"+ftemp.type+"/"+ftemp.name+" ");
            for(Class.Method mtemp:ctemp.methods){
                System.out.print("方法："+mtemp.modifiers+"/"+mtemp.returntype+"/"+mtemp.name+"/");
                for(String ptemp:mtemp.paramtypes){
                    System.out.print(ptemp+"#");
                }
                System.out.print(" ");
            }
            System.out.println();
        }

        for(Class ctemp:interfacelist){
            System.out.print("接口名："+ctemp.classname);
            for(Class.Field ftemp:ctemp.fields)
                System.out.print("属性："+ftemp.modifiers+"/"+ftemp.type+"/"+ftemp.name+" ");
            for(Class.Method mtemp:ctemp.methods){
                System.out.print("方法："+mtemp.modifiers+"/"+mtemp.returntype+"/"+mtemp.name+"/");
                for(String ptemp:mtemp.paramtypes){
                    System.out.print(ptemp+"#");
                }
                System.out.print(" ");

            }
            System.out.println();
        }

        System.out.println("继承关系："+extenders);
        System.out.println("接口关系："+interfacers);
    }

//    public static void initial(String apkPath) {
//        soot.G.reset();
//        //	Options.v().set_allow_phantom_refs(true);
//        Options.v().set_prepend_classpath(true);
//        Options.v().set_validate(true);
//        Options.v().set_output_format(Options.output_format_jimple);
//        Options.v().set_src_prec(Options.src_prec_java); //设置优先处理的源码的格式。
//        Options.v().set_process_dir(Collections.singletonList(apkPath)); //处理所有在apkPath中发现的类
////        Options.v().set_process_dir(Collections.singletonList(apkPath2)); //处理所有在apkPath中发现的类
//        Options.v().set_keep_line_number(true);
//        Options.v().set_no_bodies_for_excluded(true);
//        Options.v().set_app(true);
//        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
//        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
//        Scene.v().addBasicClass("java.lang.Thread", SootClass.SIGNATURES);
//        Scene.v().loadNecessaryClasses();
//    }

    /**
	 * soot分析进行初始化
	 */
	public void initial(String appApk){
        if(SOOT_INITIALIZED)
            return;
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_process_dir(Collections.singletonList(appApk));
        Options.v().set_force_android_jar(androidJAR);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_soot_classpath(androidJAR);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_multiple_dex(true);
        Scene.v().loadNecessaryClasses();
        SOOT_INITIALIZED=true;
    }
    
}
