class Test {
    Test f;

    void foo() {
        Test x = this.f;
        Test y = this.f;   // redundant (NOT as this will be deadcode)
    }

    void bar() {
        Test x = this.f;
        this.foo();
        Test y = this.f;   // NOT redundant
    }

    public static void main(String[] args) {
        Test a = new Test();
        a.bar();
    }
}
