/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast;

import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.AdditionExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.AndExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.ArrayLiteralExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.BinaryExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.BooleanExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.BooleanValuedFunctionWrapper;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.ComparisonExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.ConstantExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.DoubleExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.EqualityExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.FieldAccessExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.FieldRefExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.FunctionExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.IndexedAccessExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.LogicalExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.LongExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.MapLiteralExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.MessageRefExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.MultiplicationExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.NotExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.NumericExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.OrExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.SignedExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.StringExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.UnaryExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.VarRefExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.statements.FunctionStatement;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.statements.Statement;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.statements.VarAssignStatement;

public class RuleAstBaseListener implements RuleAstListener {
    @Override
    public void enterRule(Rule rule) {

    }

    @Override
    public void exitRule(Rule rule) {

    }

    @Override
    public void enterWhen(Rule rule) {

    }

    @Override
    public void exitWhen(Rule rule) {

    }

    @Override
    public void enterThen(Rule rule) {

    }

    @Override
    public void exitThen(Rule rule) {

    }

    @Override
    public void enterStatement(Statement statement) {

    }

    @Override
    public void exitStatement(Statement statement) {

    }

    @Override
    public void enterFunctionCallStatement(FunctionStatement func) {

    }

    @Override
    public void exitFunctionCallStatement(FunctionStatement func) {

    }

    @Override
    public void enterVariableAssignStatement(VarAssignStatement assign) {

    }

    @Override
    public void exitVariableAssignStatement(VarAssignStatement assign) {

    }

    @Override
    public void enterAddition(AdditionExpression expr) {

    }

    @Override
    public void exitAddition(AdditionExpression expr) {

    }

    @Override
    public void enterAnd(AndExpression expr) {

    }

    @Override
    public void exitAnd(AndExpression expr) {

    }

    @Override
    public void enterArrayLiteral(ArrayLiteralExpression expr) {

    }

    @Override
    public void exitArrayLiteral(ArrayLiteralExpression expr) {

    }

    @Override
    public void enterBinary(BinaryExpression expr) {

    }

    @Override
    public void exitBinary(BinaryExpression expr) {

    }

    @Override
    public void enterBoolean(BooleanExpression expr) {

    }

    @Override
    public void exitBoolean(BooleanExpression expr) {

    }

    @Override
    public void enterBooleanFuncWrapper(BooleanValuedFunctionWrapper expr) {

    }

    @Override
    public void exitBooleanFuncWrapper(BooleanValuedFunctionWrapper expr) {

    }

    @Override
    public void enterComparison(ComparisonExpression expr) {

    }

    @Override
    public void exitComparison(ComparisonExpression expr) {

    }

    @Override
    public void enterConstant(ConstantExpression expr) {

    }

    @Override
    public void exitConstant(ConstantExpression expr) {

    }

    @Override
    public void enterDouble(DoubleExpression expr) {

    }

    @Override
    public void exitDouble(DoubleExpression expr) {

    }

    @Override
    public void enterEquality(EqualityExpression expr) {

    }

    @Override
    public void exitEquality(EqualityExpression expr) {

    }

    @Override
    public void enterFieldAccess(FieldAccessExpression expr) {

    }

    @Override
    public void exitFieldAccess(FieldAccessExpression expr) {

    }

    @Override
    public void enterFieldRef(FieldRefExpression expr) {

    }

    @Override
    public void exitFieldRef(FieldRefExpression expr) {

    }

    @Override
    public void enterFunctionCall(FunctionExpression expr) {

    }

    @Override
    public void exitFunctionCall(FunctionExpression expr) {

    }

    @Override
    public void enterIndexedAccess(IndexedAccessExpression expr) {

    }

    @Override
    public void exitIndexedAccess(IndexedAccessExpression expr) {

    }

    @Override
    public void enterLogical(LogicalExpression expr) {

    }

    @Override
    public void exitLogical(LogicalExpression expr) {

    }

    @Override
    public void enterLong(LongExpression expr) {

    }

    @Override
    public void exitLong(LongExpression expr) {

    }

    @Override
    public void enterMapLiteral(MapLiteralExpression expr) {

    }

    @Override
    public void exitMapLiteral(MapLiteralExpression expr) {

    }

    @Override
    public void enterMessageRef(MessageRefExpression expr) {

    }

    @Override
    public void exitMessageRef(MessageRefExpression expr) {

    }

    @Override
    public void enterMultiplication(MultiplicationExpression expr) {

    }

    @Override
    public void exitMultiplication(MultiplicationExpression expr) {

    }

    @Override
    public void enterNot(NotExpression expr) {

    }

    @Override
    public void exitNot(NotExpression expr) {

    }

    @Override
    public void enterNumeric(NumericExpression expr) {

    }

    @Override
    public void exitNumeric(NumericExpression expr) {

    }

    @Override
    public void enterOr(OrExpression expr) {

    }

    @Override
    public void exitOr(OrExpression expr) {

    }

    @Override
    public void enterSigned(SignedExpression expr) {

    }

    @Override
    public void exitSigned(SignedExpression expr) {

    }

    @Override
    public void enterString(StringExpression expr) {

    }

    @Override
    public void exitString(StringExpression expr) {

    }

    @Override
    public void enterUnary(UnaryExpression expr) {

    }

    @Override
    public void exitUnary(UnaryExpression expr) {

    }

    @Override
    public void enterVariableReference(VarRefExpression expr) {

    }

    @Override
    public void exitVariableReference(VarRefExpression expr) {

    }

    @Override
    public void enterEveryExpression(Expression expr) {

    }

    @Override
    public void exitEveryExpression(Expression expr) {

    }

    @Override
    public void enterFunctionArg(FunctionExpression functionExpression, Expression expression) {

    }

    @Override
    public void exitFunctionArg(Expression expression) {

    }
}
