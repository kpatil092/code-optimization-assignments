class Test {
    Test f;

    void foo() { }

    public static void main(String[] args) {
        Test a = new Test();

        Test x = a.f;
        a.foo();       // kills a.*
        Test y = a.f;  // NOT redundant
    }
}
