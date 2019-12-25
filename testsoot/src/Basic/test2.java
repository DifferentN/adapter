package Basic;

import android.util.Log;

import java.lang.reflect.Method;

public class test2 {
    public static void main(String[] args){
        try {
            java.lang.Class clz1 = java.lang.Class.forName("com.pwp.vo.ScheduleVO");
            Object object1 = clz1.getConstructor();
            Method method11 = clz1.getMethod("setDay",int.class);
            method11.invoke(object1,19);
            Method method12 = clz1.getMethod("setMonth",int.class);
            method12.invoke(object1,7);
            Method method13 = clz1.getMethod("setYear",int.class);
            method13.invoke(object1,2019);

            java.lang.Class clz2 = java.lang.Class.forName("com.pwp.calendar.LunarCalendar");
            Object object2 = clz2.getConstructor();
            Method method21 = clz2.getMethod("getLunarDateIn01",int.class,int.class,int.class,boolean.class);
            String s=(String)method21.invoke(object2,2019,7,30,false);
            System.out.println(s);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
