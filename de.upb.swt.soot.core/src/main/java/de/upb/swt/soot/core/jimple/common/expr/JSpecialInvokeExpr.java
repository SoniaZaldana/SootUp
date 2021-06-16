package de.upb.swt.soot.core.jimple.common.expr;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999-2020 Patrick Lam, Markus Schmidt, Linghui Luo and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import de.upb.swt.soot.core.jimple.Jimple;
import de.upb.swt.soot.core.jimple.basic.Immediate;
import de.upb.swt.soot.core.jimple.basic.JimpleComparator;
import de.upb.swt.soot.core.jimple.basic.Local;
import de.upb.swt.soot.core.jimple.visitor.ExprVisitor;
import de.upb.swt.soot.core.signatures.MethodSignature;
import de.upb.swt.soot.core.util.Copyable;
import de.upb.swt.soot.core.util.printer.StmtPrinter;
import java.util.List;
import javax.annotation.Nonnull;

/** An expression that invokes a special method (e.g. private methods). */
public final class JSpecialInvokeExpr extends AbstractInstanceInvokeExpr implements Copyable {

  public JSpecialInvokeExpr(
      @Nonnull Local base, @Nonnull MethodSignature method, @Nonnull List<Immediate> args) {
    super(base, method, args.toArray(new Immediate[args.size()]));
  }

  @Override
  public boolean equivTo(Object o, @Nonnull JimpleComparator comparator) {
    return comparator.caseSpecialInvokeExpr(this, o);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder
        .append(Jimple.SPECIALINVOKE + " ")
        .append(getBase().toString())
        .append(".")
        .append(getMethodSignature())
        .append("(");
    argsToString(builder);
    builder.append(")");

    return builder.toString();
  }

  /** Converts a parameter of type StmtPrinter to a string literal. */
  @Override
  public void toString(@Nonnull StmtPrinter up) {
    up.literal(Jimple.SPECIALINVOKE);
    up.literal(" ");
    getBase().toString(up);
    up.literal(".");
    up.methodSignature(getMethodSignature());
    up.literal("(");
    argsToPrinter(up);
    up.literal(")");
  }

  @Override
  public void accept(@Nonnull ExprVisitor v) {
    v.caseSpecialInvokeExpr(this);
  }

  @Override
  @Nonnull
  public JSpecialInvokeExpr withBase(@Nonnull Local base) {
    return new JSpecialInvokeExpr(base, getMethodSignature(), getArgs());
  }

  @Override
  @Nonnull
  public JSpecialInvokeExpr withMethodSignature(@Nonnull MethodSignature methodSignature) {
    return new JSpecialInvokeExpr(getBase(), methodSignature, getArgs());
  }

  @Override
  @Nonnull
  public JSpecialInvokeExpr withArgs(@Nonnull List<Immediate> args) {
    return new JSpecialInvokeExpr(getBase(), getMethodSignature(), args);
  }
}
