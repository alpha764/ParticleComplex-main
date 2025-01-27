package com.example.particlecomplex.utils.expression_utils;

@FunctionalInterface
public interface Expression {
    float apply(float t, float x, float y, float z);

    /**
     * 将表达式转换为字符串表示。
     * @return 表达式的字符串表示
     */
    String toString();
}
