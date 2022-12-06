package de.upb.swt.soot.test.java.bytecode.interceptors.typeresolving;

import categories.Java8Test;
import de.upb.swt.soot.core.jimple.basic.Value;
import de.upb.swt.soot.core.jimple.common.expr.*;
import de.upb.swt.soot.core.jimple.common.ref.JCaughtExceptionRef;
import de.upb.swt.soot.core.jimple.common.ref.JFieldRef;
import de.upb.swt.soot.core.jimple.common.ref.JThisRef;
import de.upb.swt.soot.core.jimple.common.stmt.Stmt;
import de.upb.swt.soot.core.types.PrimitiveType;
import de.upb.swt.soot.core.types.Type;
import de.upb.swt.soot.core.types.VoidType;
import de.upb.swt.soot.java.bytecode.interceptors.typeresolving.AugEvalFunction;
import de.upb.swt.soot.java.bytecode.interceptors.typeresolving.Typing;
import java.util.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/** @author Zun Wang */
@Category(Java8Test.class)
public class AugEvalFunctionTest extends TypeAssignerTestSuite {

  Typing typing = new Typing(new ArrayList<>());
  AugEvalFunction evalFunction;

  @Before
  public void setup() {
    String baseDir = "../shared-test-resources/TypeResolverTestSuite/AugEvalFunctionTest/";
    String className = "AugEvalFunctionDemos";
    buildView(baseDir, className);
    evalFunction = new AugEvalFunction(view);
  }

  @Test
  public void testImmediate() {
    Type actual, expected;
    Stmt stmt = null;
    Value value = null;
    setMethodBody("constant", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      String sn = s.toString();
      switch (sn) {
        case "l1 = 127":
          value = s.getUses().get(0);
          stmt = s;
          expected = PrimitiveType.getInteger127();
          actual = evalFunction.evaluate(typing, value, stmt, body);
          Assert.assertEquals(expected, actual);
          break;
        case "l1 = 32111":
          value = s.getUses().get(0);
          stmt = s;
          expected = PrimitiveType.getInteger32767();
          actual = evalFunction.evaluate(typing, value, stmt, body);
          Assert.assertEquals(expected, actual);
          break;
        case "l1 = -129":
          value = s.getUses().get(0);
          stmt = s;
          expected = PrimitiveType.getShort();
          actual = evalFunction.evaluate(typing, value, stmt, body);
          Assert.assertEquals(expected, actual);
          break;
        case "l2 = 1.0":
          value = s.getUses().get(0);
          stmt = s;
          expected = PrimitiveType.getDouble();
          actual = evalFunction.evaluate(typing, value, stmt, body);
          Assert.assertEquals(expected, actual);
          break;
        case "l4 = \"example\"":
          value = s.getUses().get(0);
          stmt = s;
          expected = identifierFactory.getClassType("java.lang.String");
          actual = evalFunction.evaluate(typing, value, stmt, body);
          Assert.assertEquals(expected, actual);
          break;
        default:
      }
    }
    setMethodBody("reflection", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l1 = class \"LA;\"")) {
        value = s.getUses().get(0);
        stmt = s;
        break;
      }
    }
    expected = identifierFactory.getClassType("java.lang.Class");
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testExpr() {
    Type actual;
    Type expected = PrimitiveType.getBoolean();
    Stmt stmt = null;
    Value value = null;
    setMethodBody("condition", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("if l1 >= l2")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractConditionExpr) {
            value = use;
            stmt = s;
          }
        }
      }
    }
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);

    setMethodBody("shift", "void", Collections.emptyList());
    Map map = new HashMap();
    map.put("l1", PrimitiveType.getByte());
    map.put("l2", PrimitiveType.getLong());
    Typing specTyping = createTyping(map);

    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l4 = l2 << l1")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractIntLongBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getLong();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }

      if (s.toString().equals("l6 = l1 << $stack7")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractIntLongBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getInt();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }
    }

    setMethodBody("xor", "void", Collections.emptyList());
    map.clear();
    map.put("l1", PrimitiveType.getBoolean());
    map.put("l2", PrimitiveType.getBoolean());
    map.put("l4", PrimitiveType.getLong());
    specTyping = createTyping(map);
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l3 = l2 ^ l1")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractIntLongBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getBoolean();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }

      if (s.toString().equals("l6 = $stack8 ^ l4")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractIntLongBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getLong();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }
    }

    map.clear();
    map.put("l1", PrimitiveType.getInteger1());
    map.put("l2", PrimitiveType.getByte());
    specTyping = createTyping(map);

    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l3 = l2 ^ l1")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractIntLongBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getByte();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }
    }

    setMethodBody("add", "void", Collections.emptyList());
    map.clear();
    map.put("l1", PrimitiveType.getInteger1());
    map.put("l2", PrimitiveType.getFloat());
    specTyping = createTyping(map);

    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l3 = l2 + $stack4")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractFloatBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getFloat();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }

      if (s.toString().equals("l1 = l1 + 1")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractFloatBinopExpr) {
            value = use;
            stmt = s;
          }
        }
        expected = PrimitiveType.getInt();
        actual = evalFunction.evaluate(specTyping, value, stmt, body);
        Assert.assertEquals(expected, actual);
      }
    }

    setMethodBody("length", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l2 = lengthof l1")) {
        for (Value use : s.getUses()) {
          if (use instanceof JLengthExpr) {
            value = use;
            stmt = s;
          }
        }
      }
    }
    expected = PrimitiveType.getInt();
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);

    setMethodBody("instanceOf", "boolean", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("$stack3 = l1 instanceof A")) {
        for (Value use : s.getUses()) {
          if (use instanceof JInstanceOfExpr) {
            value = use;
            stmt = s;
          }
        }
      }
    }
    expected = PrimitiveType.getBoolean();
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);

    setMethodBody("newArrayExpr", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l1 = newmultiarray (A)[3][3]")) {
        for (Value use : s.getUses()) {
          if (use instanceof JNewMultiArrayExpr) {
            value = use;
            stmt = s;
          }
        }
      }
    }
    expected = identifierFactory.getArrayType(identifierFactory.getClassType("A"), 2);
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);

    setMethodBody("invokeExpr", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("specialinvoke $stack2.<A: void <init>()>()")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractInvokeExpr) {
            value = use;
            stmt = s;
            expected = VoidType.getInstance();
            actual = evalFunction.evaluate(typing, value, stmt, body);
            Assert.assertEquals(expected, actual);
          }
        }
      } else if (s.toString().equals("$stack3 = virtualinvoke l1.<A: B method()>()")) {
        for (Value use : s.getUses()) {
          if (use instanceof AbstractInvokeExpr) {
            value = use;
            stmt = s;
            expected = identifierFactory.getClassType("B");
            actual = evalFunction.evaluate(typing, value, stmt, body);
            Assert.assertEquals(expected, actual);
          }
        }
      }
    }
  }

  @Test
  public void testRef() {
    Type actual;
    Type expected = identifierFactory.getClassType("java.lang.ArithmeticException");
    Stmt stmt = null;
    Value value = null;
    setMethodBody("caughtException1", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("$stack2 := @caughtexception")) {
        for (Value use : s.getUses()) {
          if (use instanceof JCaughtExceptionRef) {
            value = use;
            stmt = s;
          }
        }
      }
    }
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);

    setMethodBody("caughtException2", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("$stack2 := @caughtexception")) {
        for (Value use : s.getUses()) {
          if (use instanceof JCaughtExceptionRef) {
            value = use;
            stmt = s;
          }
        }
      }
    }
    expected = identifierFactory.getClassType("java.lang.RuntimeException");
    actual = evalFunction.evaluate(typing, value, stmt, body);
    Assert.assertEquals(expected, actual);

    setMethodBody("fieldRef", "void", Collections.emptyList());
    for (Stmt s : body.getStmts()) {
      if (s.toString().equals("l1 = l0.<ByteCodeTypeTest: A field>")) {
        for (Value use : s.getUses()) {
          if (use instanceof JFieldRef) {
            value = use;
            stmt = s;
            expected = identifierFactory.getClassType("A");
            actual = evalFunction.evaluate(typing, value, stmt, body);
            Assert.assertEquals(expected, actual);
          }
        }
      } else if (s.toString().equals("l0 := @this: ByteCodeTypeTest")) {
        for (Value use : s.getUses()) {
          if (use instanceof JThisRef) {
            value = use;
            stmt = s;
            expected = identifierFactory.getClassType("ByteCodeTypeTest");
            actual = evalFunction.evaluate(typing, value, stmt, body);
            Assert.assertEquals(expected, actual);
          }
        }
      }
    }
  }
}
