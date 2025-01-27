package com.example.particlecomplex.utils.expression_utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


import java.util.ArrayList;
import java.util.List;


public class EquationSolver {

    private final String equation;
    private final double tolerance;  // 容差，用于判断是否接近 0

    public EquationSolver(String equation, double tolerance) {
        this.equation = equation;
        this.tolerance = tolerance;
    }

    public List<double[]> solve(double start, double end, double step) {
        List<double[]> points = new ArrayList<>();

        // 创建解析表达式
        Expression expression = new ExpressionBuilder(equation)
                .variables("x", "y", "z")
                .build();

        for (double x = start; x <= end; x += step) {
            for (double y = start; y <= end; y += step) {
                for (double z = start; z <= end; z += step) {
                    // 设置变量值
                    expression.setVariable("x", x);
                    expression.setVariable("y", y);
                    expression.setVariable("z", z);

                    // 计算表达式的值
                    double result = expression.evaluate();

                    // 检查结果是否接近 0
                    if (Math.abs(result) < tolerance) {
                        points.add(new double[]{x, y, z});
                    }
                }
            }
        }

        return points;
    }

    public static void main(String[] args) {
        // 输入方程：X^2 + Y^2 + Z^2 - 1 = 0
        String equation = "x^2 + y^2 + z^2 - 1";
        double tolerance = 0.01;  // 设置容差

        EquationSolver solver = new EquationSolver(equation, tolerance);

        // 定义采样的范围和步长
        double start = -1.0;
        double end = 1.0;
        double step = 0.1;

        // 解决方程并得到符合条件的点
        List<double[]> points = solver.solve(start, end, step);

        // 打印出符合条件的点
        for (double[] point : points) {
            System.out.printf("x=%.2f, y=%.2f, z=%.2f%n", point[0], point[1], point[2]);
        }

    }
}
