class Test {
    Test f;

    public static void main(String[] args) {
        Test[] a = new Test[2];
        Test[] b = new Test[2];

        Test c = new Test();
        a[0] = c;
        b[0] = new Test();
        c = b[0];
        b[1] = c;
        a[1] = a[0];
        b[0] = b[1];
        return;
    }
}
