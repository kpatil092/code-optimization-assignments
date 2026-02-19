class Test {
    Test f;

    static void foo(Test t) { }

    public static void main(String[] args) {
        Test a = new Test();
        Test b = new Test();

        Test x = a.f;
        foo(b);        // unrelated
        Test y = a.f;  // redundant
    }
}
