import java.util.Collection;
import java.util.Collections;

import soot.*;
import soot.JastAddJ.Opt;
import soot.options.Options;

public class Main {
    public static void main(String[] args) {

        String classPath = "./testcases/" + args[0];
        //Set up arguments for Soot
        String[] sootArgs = {
            "-cp", classPath,
            "-pp",
            "-f", "J",
            "-t", "1",
            "-main-class", "Test",	// specify the main class
            "-process-dir", classPath                // list the classes to analyze
        };

        // Create transformer for analysis
        // SampleBodyTransform pass1 = new SampleBodyTransform();
       AnalysisTransformer pass2 = new AnalysisTransformer();

        // Add transformer to appropriate pack in PackManager; PackManager will run all packs when soot.Main.main is called
        // PackManager.v().getPack("jtp").add(new Transform("jtp.dfa", pass1));
       PackManager.v().getPack("jtp").add(new Transform("jtp.dfa", pass2));

       Options.v().set_keep_line_number(true);
    //    Options.v().set_prepend_classpath(true);
    //    Options.v().set_allow_phantom_refs(true);
    //    Options.v().set_process_dir(Collections.singletonList(null));

        // Call Soot's main method with arguments
        soot.Main.main(sootArgs);

        AnalysisTransformer.printResults();


        
    }
}