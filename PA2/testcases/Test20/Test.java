class A {
    B f;
}

class B {
    void mutate() { }
}

class Test {
    public static void main(String[] args) {
        A a = new A();
        Test t = new Test();

        B x = a.f;
        t.call(a.f);
        B y = a.f;   // NOT redundant
    }

    void call(B b) {
        b.mutate();
    }
}
