class Test {
    Test f;
    Test g;

    public static void main(String[] args) {
        Test a = new Test();

        Test x = a.f.g;
        a.f = new Test();    // prefix write
        Test y = a.f.g;      // NOT redundant
    }
}
