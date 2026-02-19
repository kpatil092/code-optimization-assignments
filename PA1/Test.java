public class Test {
    public static void main(String[] args) {
       Animal a = new Animal();
       System.out.println(a.x + " " + a.y);
       a.sound();

//        Animal b = new Monkey();
//        System.out.println(b.x + " " + b.y);
//        System.out.println(b.x + " " + ((Monkey) b).y + " " + ((Monkey) b).z);


       Animal b = new Monkey();
       b.tail();
       ((Animal) b).tail();
       b.sound();
    }
}

class Animal {
    int x;
    int y;
    Animal() {
        x = y = 10;
    }
    public void sound() {
        System.out.println("Animal's sound: " + y);
    }
    public void tail() {
        System.out.println("Animal's tail");
    }
}

class Monkey extends Animal {
    int y;
    int z;
    Monkey() {
        //super();
        x = 20;
        y = 20;
        z = 20;
    }
    public void tail() {
        System.out.println("Monkey's tail");
    }
}
