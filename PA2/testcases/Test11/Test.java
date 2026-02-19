class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();

        Test x = a.f;
        a.f = new Test();
        Test y = a.f;   // NOT redundant
    }
}
