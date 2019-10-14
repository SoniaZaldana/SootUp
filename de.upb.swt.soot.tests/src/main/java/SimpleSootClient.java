import com.ibm.wala.ipa.callgraph.CallGraph;
import de.upb.swt.soot.core.Project;
import de.upb.swt.soot.core.inputlocation.AnalysisInputLocation;
import de.upb.swt.soot.core.inputlocation.DefaultSourceTypeSpecifier;
import de.upb.swt.soot.core.views.View;
import de.upb.swt.soot.java.bytecode.frontend.AsmJavaClassProvider;
import de.upb.swt.soot.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import de.upb.swt.soot.java.sourcecode.inputlocation.JavaSourcePathAnalysisInputLocation;
import java.util.Collections;

/**
 * A sample application to illustrate a potential client's path through the API
 *
 * @author Linghui Luo
 * @author Ben Hermann
 */
public class SimpleSootClient {

  public static void main(String[] args) {
    String javaClassPath = "de/upb/soot/example/classes/";
    String javaSourcePath = "de/upb/soot/example/src";

    AnalysisInputLocation cpBased =
        new JavaClassPathAnalysisInputLocation(
            javaClassPath, new DefaultSourceTypeSpecifier(), new AsmJavaClassProvider());

    AnalysisInputLocation walaSource =
        new JavaSourcePathAnalysisInputLocation(
            Collections.singleton(javaSourcePath), new DefaultSourceTypeSpecifier());

    Project<AnalysisInputLocation> p = new Project<>(walaSource);

    // 1. simple case
    View fullView = p.createFullView();

    /*
        CallGraph cg = fullView.createCallGraph();
        TypeHierarchy t = fullView.typeHierarchy();
        mySootAnalysis( cg );


        // 2. advanced case with scope
        Scope s = new Scope(cpBased);
        View limitedView = p.createView(s);
        cg = limitedView.createCallGraph();
        t = limitedView.typeHierarchy();
        mySootAnalysis( cg );
    */

  }

  /** dummy method */
  static void mySootAnalysis(CallGraph cg) {
    // here goes my own analysis
  }
}
