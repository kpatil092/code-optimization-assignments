class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();
        a.f = new Test();

        Test x = a.f;
        Test y = a.f;   // redundant
        Test z = a.f;   // redundant
    }
}
