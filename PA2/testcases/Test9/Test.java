class Test {
    Test f;

    public static void main(String[] args) {
        Test a = new Test();
        Test x = null;

        for (int i = 0; i < 10; i++) {
            x = a.f;   // redundant after first iteration (Not redudant)
        }
    }
}
