class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();

        if (args.length > 0) {
            Test x = a.f;
        }

        Test y = a.f;   // NOT redundant
    }
}
