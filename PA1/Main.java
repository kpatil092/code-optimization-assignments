import soot.PackManager;
import soot.Transform;

public class Main {
    public static void main(String[] args) {
        //Set up arguments for Soot
        String[] sootArgs = {
            "-cp", ".", "-pp",
            "-f", "J",
            "-w",
            "-main-class", "Test",	// specify the main class
            "Test", "Animal", "Monkey"                  // list the classes to analyze
        };

        // Create transformer for analysis
        // SampleBodyTransform pass1 = new SampleBodyTransform();
       SampleSceneTransform pass2 = new SampleSceneTransform();

        // Add transformer to appropriate pack in PackManager; PackManager will run all packs when soot.Main.main is called
        // PackManager.v().getPack("jtp").add(new Transform("jtp.dfa", pass1));
       PackManager.v().getPack("wjtp").add(new Transform("wjtp.idfa", pass2));

        // Call Soot's main method with arguments
        soot.Main.main(sootArgs);
    }
}
