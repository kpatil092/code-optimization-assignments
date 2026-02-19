class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();
        a.f = new Test();

        Test x = a.f;
        a.f.f = new Test();  // deeper write
        Test y = a.f;        // redundant
    }
}
