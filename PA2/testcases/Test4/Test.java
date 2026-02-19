class Test {
  Test f;
  void main() {
    Test a = new Test();
    Test x = a.f;
    Test y = a.f;    // redudant 
  }
}
