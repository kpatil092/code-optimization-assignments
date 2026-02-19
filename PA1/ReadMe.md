# Programming Assignment 1 (PA1)
## CS6004 — Code Optimization for Object-Oriented Languages


## Objective

For all application classes in the input Java program, print structural information about each class including:

- Fields
- Object size
- Methods

The output must follow a **strictly defined format**.

---

## Output Order

Classes must be processed and printed in **lexicographical order of class names**.

---

## Output Format

For each class, print:

```
CLASS <ClassName>

FIELDS
<DeclaringClass>:<Type> <FieldName>
...

OBJECT_SIZE <size>

METHODS
<DeclaringClass>: <returnType> <methodName>(<parameterTypeArgsList>)
...

END_CLASS
```


---

## Fields

For a class `C`, print all fields that **contribute to the object size**.

- Field ordering must follow **declaration order**.
- Each field must be printed as:

```
<DeclaringClass>:<Type> <FieldName>
```


---

## Methods

For a class `C`, methods must be printed:

- In the order they appear in the **virtual table** of `C`
- Relative to method declaration order
- **Ignore constructors and static methods**

Each method must be printed as:

```
<DeclaringClass>: <returnType> <methodName>(<parameterTypeArgsList>)
```


---

## Object Size

Object size must include:

- Default **12-byte object header**
- Memory contributed by object fields

### Assumptions

Ignore:
- Padding
- Alignment

### Type Sizes

| Type            | Size (bytes) |
|-----------------|-------------|
| byte, boolean   | 1 |
| short, char     | 2 |
| int, float      | 4 |
| long, double    | 8 |
| object reference| 4 |

---

## Important Notes

- Output must **exactly match** the specified format.
- Do **not print extra output**.
- Remove debugging or auxiliary print statements before submission.


