/*
 * */
package com.synectiks.process.common.events.conditions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = Expression.FIELD_EXPR,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Expr.True.class, name = Expr.True.EXPR),
        @JsonSubTypes.Type(value = Expr.And.class, name = Expr.And.EXPR),
        @JsonSubTypes.Type(value = Expr.Or.class, name = Expr.Or.EXPR),
        @JsonSubTypes.Type(value = Expr.Not.class, name = Expr.Not.EXPR),
        @JsonSubTypes.Type(value = Expr.Equal.class, name = Expr.Equal.EXPR),
        @JsonSubTypes.Type(value = Expr.Greater.class, name = Expr.Greater.EXPR),
        @JsonSubTypes.Type(value = Expr.GreaterEqual.class, name = Expr.GreaterEqual.EXPR),
        @JsonSubTypes.Type(value = Expr.Lesser.class, name = Expr.Lesser.EXPR),
        @JsonSubTypes.Type(value = Expr.LesserEqual.class, name = Expr.LesserEqual.EXPR),
        @JsonSubTypes.Type(value = Expr.NumberValue.class, name = Expr.NumberValue.EXPR),
        @JsonSubTypes.Type(value = Expr.NumberReference.class, name = Expr.NumberReference.EXPR),
        @JsonSubTypes.Type(value = Expr.Group.class, name = Expr.Group.EXPR),
})
public interface Expression<T> {
    String FIELD_EXPR = "expr";

    @JsonProperty(FIELD_EXPR)
    String expr();

    @JsonIgnore
    T accept(ExpressionVisitor visitor);
}
