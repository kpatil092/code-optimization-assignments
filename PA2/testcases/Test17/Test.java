class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();
        Test x;

        if (args.length > 0) {
            x = a.f;
        } else {
            x = a.f;
        }

        Test y = a.f;   // redundant
    }
}
