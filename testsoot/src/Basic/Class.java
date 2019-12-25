package Basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Class implements Serializable {
    public String classname;
    public List<Field> fields = new ArrayList<>();
    public List<Method> methods = new ArrayList<>();

    public class Field implements Serializable{
        public String modifiers;
        public String type;
        public String name;
    }

    public class Method implements Serializable{
        public String modifiers;
        public String returntype;
        public String name;
        public List<String> paramtypes = new ArrayList<>();
    }
}

