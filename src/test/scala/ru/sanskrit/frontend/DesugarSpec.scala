package ru.sanskrit.frontend

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.sanskrit.common.{Expr, Name, Rhs, Type}
import ru.sanskrit.frontend.syntax.{Expr => FExpr, Func}

class DesugarSpec extends AnyFlatSpec with Matchers:
  "desugarExpr" should "desugar literal" in {
    desugar.desugarExpr(FExpr.Lit(42)) shouldBe Some(Rhs.Val(Expr.Val.Lit(42)), List.empty)
  }

  it should "desugar variable name" in {
    desugar.desugarExpr(FExpr.Var("test", Type.Int)) shouldBe Some(Rhs.Val(Expr.Val.Var(Name("test"))), List.empty)
  }

  it should "desugar application" in {
    desugar.desugarExpr(FExpr.App(FExpr.Var("f", Type.Func(Type.Int, Type.Int)), FExpr.Var("x", Type.Int), Type.Int)) shouldBe
      Some(Rhs.App(Expr.Val.Var(Name("f")), Expr.Val.Var(Name("x"))), List.empty)
  }

  it should "desugar lambda" in {
    desugar.desugarExpr(FExpr.Lam(FExpr.Var("x", Type.Int), FExpr.Lit(42), Type.Func(Type.Int, Type.Int))) shouldBe
      Some(Rhs.Abs(Name("x"), Expr.Val.Lit(42)), List())
  }

  "desugarProgram" should "desugar simple program" in {
    desugar.desugarProgram(List(Func("main", Type.Int, FExpr.Lit(42)))) shouldBe
      Some(Expr.Let(Name("main"), Type.Int, Rhs.Val(Expr.Val.Lit(42)), Expr.Val.Var(Name("main"))))
  }

  it should "fail on program without main" in {
    desugar.desugarProgram(List.empty) shouldBe None
  }
