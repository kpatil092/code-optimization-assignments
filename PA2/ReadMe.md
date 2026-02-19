# Programming Assignment 2  
## Let Go of Them (Redundant Load Elimination)

**CS6004 — Code Optimization for Object-Oriented Languages**  
Spring 2026  


---

## 1. Introduction and Motivation

Object-oriented languages such as Java frequently perform field accesses within loops, conditional branches, and method invocations. In many programs, the same object field is loaded repeatedly even though its value remains unchanged between successive accesses.

These **redundant field loads** lead to:

- Unnecessary memory operations
- Runtime performance degradation
- Increased intermediate representation size
- Reduced static-analysis precision

Eliminating such redundancies improves both optimization opportunities and analysis clarity.

This assignment focuses on identifying redundant field loads and reporting them using static analysis.

---

## 2. Detailed Specification

At every program point, we want to determine:

> Which object fields have already been loaded and are still valid?

A load statement:
```
x = o.f
```

is **redundant** if:

1. The same `(o, f)` has already been loaded earlier on **all paths**, and  
2. No write to `o.f` has occurred since that load.

---

### Objectives

You must implement:

#### 1. Intraprocedural Points-to Analysis
Compute **flow-sensitive** and **field-sensitive intraprocedural points-to information** for each method.

#### 2. Intraprocedural Assumption
Since analysis is intraprocedural:

- Assume **conservative results at call sites**.

#### 3. Execution Model
- Assume **single-threaded execution**
- No parallel execution occurs

#### 4. Required Output

Using generated points-to information:

- Identify **source-code line numbers** where one or more loads are redundant.
- A redundant load must be replaceable by an already available variable.

Results must:

- Be printed **per class**
- Organized **by method**
- Sorted in **lexicographical order**
- Print class/method only if at least one redundant load exists

---

### Notes

- Use:
```
getJavaSourceStartLineNumber()
```

to obtain source line numbers of a `Unit`.

- If multiple redundant loads occur on the same line, print separately.
- Ignore constructors and library methods in final output.

---

## 3. Sample Input Programs

### Testcase 1

```java
class Node {
  Node f1;
  Node f2;
  Node g;
  Node() {}
}

public class Test {
  public static void main(String[] args) {
      Node a = new Node();
      a.f1 = new Node();

      Node b = new Node();
      b.f1 = new Node();

      a.f2 = new Node();

      Node c = a.f1;
      a.f2 = a.f1;   // Redundant
      b.f1 = a.f2;   // Redundant
  }
}
```

### Testcase 2

```java
public class Test {
    int f1;

    public static void main(String[] args) {
        Test a = new Test();
        a.f1 = 10;

        int b = a.f1;
        int c = a.f1;   // Redundant

        a.foo();

        int d = a.f1;
    }

    void foo() {
        Test o1 = new Test();
        o1.f1 = 20;

        Test o2 = o1;
        int x = o1.f1;
        int y = o2.f1;  // Redundant
    }
}
```

### Expected Output
#### Output Example 1
```
Test:main
16:$r0.<Node:Node f1> r5
17:$r0.<Node:Node f2> r5
```

#### Output Example 2
```
Test:foo
17:$r0.<Test:int f1> i0
Test:main
7:$r0.<Test:int f1> i0
```

---

### Output Format
```
SourceCode_LineNumber:LoadStatement_in_jimple jimple_var_name
```

## 4. Run the starter code

### Compile Testcase
```bash
javac testcases/Test1/Test.java
```

### Compile Analysis
```bash
javac -cp .:soot-4.6.0-jar-with-dependencies.jar Main.java
```

### Run Analysis
```bash
java -cp .:soot-4.6.0-jar-with-dependencies.jar Main Test1
```

