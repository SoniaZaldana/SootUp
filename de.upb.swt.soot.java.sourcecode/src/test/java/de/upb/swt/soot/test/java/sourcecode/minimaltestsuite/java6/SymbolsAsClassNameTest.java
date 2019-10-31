/** @author: Kaustubh Kelkar */
package de.upb.swt.soot.test.java.sourcecode.minimaltestsuite.java6;

import de.upb.swt.soot.core.model.SootClass;
import de.upb.swt.soot.core.signatures.MethodSignature;
import de.upb.swt.soot.test.java.sourcecode.minimaltestsuite.MinimalTestSuiteBase;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class SymbolsAsClassNameTest extends MinimalTestSuiteBase {
  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        "αρετηAsClassName",
        getDeclaredClassSignature(),
        "void",
        Collections.emptyList());
  }

  @Override
  public void defaultTest() {
    super.defaultTest();
    SootClass sootClass = loadClass(getDeclaredClassSignature());
    System.out.println("dummy");
    //System.out.println(sootClass.getClassSource().getClassType().getClassName());
    //assertTrue(sootClass.getClass().getName().equals("\u03b1\u03c1\u03b5\u03c4\u03b7"));
  }

  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "r0 := @this: \\u03b1\\u03c1\\u03b5\\u03c4\\u03b7",
            "$r1 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $r1.<java.io.PrintStream: void println(java.lang.String)>(\"this is \\u03b1\\u03c1\\u03b5\\u03c4\\u03b7 class\")",
            "return")
        .collect(Collectors.toList());
  }
}
