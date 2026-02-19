import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

public class AnalysisTransformer extends BodyTransformer {

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        UnitGraph graph = new ExceptionalUnitGraph(body);
        new RedundantLoadAnalysis(graph);
    }

    public static void printResults() {
        List<String> keys = new ArrayList<>(RedundantLoadAnalysis.allResults.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            System.out.println(key);
            for (String line : RedundantLoadAnalysis.allResults.get(key)) {
                System.out.println(line);
            }
        }
    }
}

class Lattice {
    Map<String, String> aliasMap = new HashMap<>();
    Map<Pair<String, String>, String> fieldMap = new HashMap<>();
    Map<String, String> localMap = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lattice)) return false;

        Lattice other = (Lattice) o;
        return aliasMap.equals(other.aliasMap)
                && fieldMap.equals(other.fieldMap)
                && localMap.equals(other.localMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliasMap, fieldMap, localMap);
    }
}

class RedundantLoadAnalysis extends ForwardFlowAnalysis<Unit, Lattice> {

    private UnitGraph Ugraph;

    public static final Map<String, List<String>> allResults = new TreeMap<>();

    private final Set<String> redundantEvents = new LinkedHashSet<>();

    private int tempCounter = 0;

    private String newTemp() {
        return "temp_" + tempCounter++;
    }

    private String getRootObject(String var, Map<String, String> aliasMap) {
        String current = var;
        while (aliasMap.containsKey(current)) {
            String parent = aliasMap.get(current);
            if (parent == null) break;
            current = parent;
        }
        return current;
    }


    public RedundantLoadAnalysis(UnitGraph graph) {
        super(graph);
        this.Ugraph = graph;
        doAnalysis(); 
        collectResults(); 
    }

    @Override
    protected Lattice newInitialFlow() {
        return new Lattice();
    }

    @Override
    protected Lattice entryInitialFlow() {
        return new Lattice();
    }

    @Override
    protected void copy(Lattice src, Lattice dest) {

        dest.aliasMap.clear();
        dest.aliasMap.putAll(src.aliasMap);

        dest.fieldMap.clear();
        dest.fieldMap.putAll(src.fieldMap);

        dest.localMap.clear();
        dest.localMap.putAll(src.localMap);
    }

    @Override
    protected void merge(Lattice in1, Lattice in2, Lattice out) {

        out.aliasMap.clear();
        out.fieldMap.clear();
        out.localMap.clear();

        for (Map.Entry<String, String> e : in1.aliasMap.entrySet()) {
            String key = e.getKey();
            String v1 = e.getValue();
            String v2 = in2.aliasMap.get(key);
            if (v2 != null && v1.equals(v2)) {
                out.aliasMap.put(key, v1);
            }
        }

        for (Map.Entry<Pair<String, String>, String> e : in1.fieldMap.entrySet()) {
            Pair<String, String> key = e.getKey();
            String v1 = e.getValue();
            String v2 = in2.fieldMap.get(key);
            if (v2 != null && v1.equals(v2)) {
                out.fieldMap.put(key, v1);
            }
        }

        for (Map.Entry<String, String> e : in1.localMap.entrySet()) {
            String key = e.getKey();
            String v1 = e.getValue();
            String v2 = in2.localMap.get(key);
            if (v2 != null && v1.equals(v2)) {
                out.localMap.put(key, v1);
            }
        }
    }

    @Override
    protected void flowThrough(Lattice in, Unit u, Lattice out) {

        copy(in, out);
        if (u instanceof AssignStmt assignStmt) {
            Value lhs = assignStmt.getLeftOp();
            Value rhs = assignStmt.getRightOp();

            if ((rhs instanceof NewExpr || rhs instanceof AnyNewExpr) && lhs instanceof Local local) {
                String varName = local.toString();
                out.aliasMap.put(varName, null);
            }

            // load
            if (rhs instanceof InstanceFieldRef || rhs instanceof ArrayRef) {
                String b, f;
                if (rhs instanceof InstanceFieldRef fieldRef) {
                    Value base = fieldRef.getBase(); // $r0
                    SootField field = fieldRef.getField();
                    b = getRootObject(base.toString(), out.aliasMap);
                    f = field.getName();
                } else {
                    ArrayRef arrayRef = (ArrayRef) rhs;
                    Value base = arrayRef.getBase();
                    Value index = arrayRef.getIndex();
                    b = getRootObject(base.toString(), out.aliasMap);
                    f = index.toString();
                }

                String left = lhs.toString();
                Pair<String, String> key = new Pair<>(b, f);

                if (!out.fieldMap.containsKey(key)) {
                    String t = newTemp();
                    out.aliasMap.put(t, null);
                    out.fieldMap.put(key, t);
                }

                String target = getRootObject(out.fieldMap.get(key), out.aliasMap);
                out.fieldMap.put(key, target);
                if (!out.aliasMap.containsKey(left))
                    out.aliasMap.put(left, target);

                if (out.localMap.containsKey(target)) {
                    int line = u.getJavaSourceStartLineNumber();
                    if (line > 0) {
                        String event = line + ":" + rhs + " " + out.localMap.get(target);
                        redundantEvents.add(event);
                    }
                } else {
                    out.localMap.put(target, left);
                }
            }

            // store
            if (lhs instanceof InstanceFieldRef || lhs instanceof ArrayRef) {
                String b, f;
                if (lhs instanceof InstanceFieldRef fieldRef) {
                    Value base = fieldRef.getBase(); // $r0
                    SootField field = fieldRef.getField();
                    b = getRootObject(base.toString(), out.aliasMap);
                    f = field.getName();
                } else {
                    ArrayRef arrayRef = (ArrayRef) lhs;
                    Value base = arrayRef.getBase();
                    Value index = arrayRef.getIndex();
                    b = getRootObject(base.toString(), out.aliasMap);
                    f = index.toString();
                }

                Pair<String, String> key = new Pair<>(b, f);
                out.fieldMap.remove(key);
                String target;

                if (rhs instanceof Local local) {
                    target = local.toString();
                    if (!out.aliasMap.containsKey(target))
                        out.aliasMap.put(target, null);
                } else {
                    target = newTemp();
                    out.aliasMap.put(target, null);
                }
                out.fieldMap.put(key, target);
            }

        }

        if (u instanceof Stmt stmt && stmt.containsInvokeExpr()) {
            InvokeExpr invoke = stmt.getInvokeExpr();
            SootMethod method = invoke.getMethod();

            if (!method.isConstructor()) {
                for (String key : new ArrayList<>(out.aliasMap.keySet())) {
                    out.aliasMap.put(key, null);
                }
                out.localMap.clear();
                out.fieldMap.clear();
            }
        }
    }

    public void collectResults() {
        if (redundantEvents.isEmpty()) return;

        SootMethod method = Ugraph.getBody().getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        List<String> results = new ArrayList<>(redundantEvents);
        
        results.sort(Comparator.comparingInt(
                s -> Integer.parseInt(s.substring(0, s.indexOf(':')))));

        allResults.put(className + ":" + methodName, results);
    }

}
