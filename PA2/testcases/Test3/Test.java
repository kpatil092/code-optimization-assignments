public class Test {
    Test f;
    public static void main(String[] args) {
        Test a = new Test();
        a.f = new Test();
        a.f.f = new Test(); 
        a.f.f.f = new Test(); 
        Test c = a.f;
        (a.f.f).foo();
        Test d = a.f;  // Redundant 
    }
    public void foo() {
        return;
    }
}
