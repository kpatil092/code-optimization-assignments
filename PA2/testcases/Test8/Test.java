class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();
        Test b = a;
        Test x = a.f;
        b.f = new Test();
        Test y = a.f;   // NOT redundant
    }
}
