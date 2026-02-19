import java.util.*;
import soot.*;

public class SampleSceneTransform extends SceneTransformer {

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {

        List<SootClass> classes = new ArrayList<>();
        for(SootClass sc: Scene.v().getApplicationClasses()) {
            if(!sc.isJavaLibraryClass() && !sc.isAbstract() && !sc.isInterface()) classes.add(sc);
        }

        classes.sort(Comparator.comparing(SootClass::getName));

        for (SootClass sc : classes) {
            int objectSize = 12;

            // Class
            System.out.println("CLASS" + " " + sc.getName());

            // Fields
            System.out.println("FIELDS");

            List<SootField> fields = new ArrayList<>();
            collectFields(sc, fields);

            for(SootField field: fields) {
                if(field.isStatic()) continue;
                System.out.println(field.getDeclaringClass().getName() 
                    + "::" 
                    + field.getType() 
                    + " " 
                    + field.getName());
                objectSize += getTypeSize(field.getType());
            } 

            // Object Size
            System.out.println("OBJECT_SIZE " + objectSize);

            // Methods
            System.out.println("METHODS");
            
            Map<String, SootMethod> methods = new LinkedHashMap<>();
            collectMethods(sc, methods);

            for(SootMethod m: methods.values()) {
                if(m.isStatic() || m.isConstructor()) continue;
                StringBuilder sb = new StringBuilder();
                sb.append(m.getDeclaringClass().getName())
                    .append("::")
                    .append(m.getReturnType())
                    .append(" ")
                    .append(m.getName())
                    .append("(");

                List<Type> params = m.getParameterTypes();
                for(int i=0; i<params.size(); i++) {
                    sb.append(params.get(i));
                    if(i < params.size() - 1) sb.append(", ");
                }
                sb.append(")");

                System.out.println(sb.toString());
            }

            // End Class
            System.out.println("END_CLASS\n");
        }
    }

    private void collectFields(SootClass sc, List<SootField> fields) {

        // if (sc.isInterface()) return;
        
        if(sc.hasSuperclass()) {
            SootClass sup = sc.getSuperclass();
            if(sup.isApplicationClass()) collectFields(sup, fields);
        }
        for(SootField f: sc.getFields()) {
            if(!f.isStatic()) fields.add(f);
        }
    }

    private void collectMethods(SootClass sc, Map<String, SootMethod> methods) {

        // if (sc.isInterface()) return;
        if(sc.hasSuperclass()) {
            SootClass sup = sc.getSuperclass();
            if(sup.isApplicationClass()) collectMethods(sup, methods);
        }
        for(SootMethod m: sc.getMethods()) {
            if(m.isStatic() || m.isConstructor()) continue;
            if(!m.getDeclaringClass().isApplicationClass()) continue;

            methods.put(m.getSubSignature(), m);
        }
    }

    private int getTypeSize(Type t) {
        if(t instanceof BooleanType) return 1;
        if(t instanceof ByteType) return 1;
        if(t instanceof CharType) return 2;
        if(t instanceof ShortType) return 2;
        if(t instanceof IntType) return 4;
        if(t instanceof FloatType) return 4;
        if(t instanceof LongType) return 8;
        if(t instanceof DoubleType) return 8;
        if(t instanceof RefType || t instanceof ArrayType) return 4;
        return 0;
    }
}
