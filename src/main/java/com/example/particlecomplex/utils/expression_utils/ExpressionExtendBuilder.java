package com.example.particlecomplex.utils.expression_utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.Random;

/**
 * exp4j支持的原本函数以及扩展
 * <p>
 * 支持的基本函数包括：
 * <ul>
 *   <li><b>幂和根</b>：
 *     <ul>
 *       <li><code>pow(x, y)</code> 或 <code>x^y</code> (幂运算)</li>
 *       <li><code>sqrt(x)</code> (平方根)</li>
 *     </ul>
 *   </li>
 *   <li><b>三角函数</b>：
 *     <ul>
 *       <li><code>sin(x)</code> (正弦)</li>
 *       <li><code>cos(x)</code> (余弦)</li>
 *       <li><code>tan(x)</code> (正切)</li>
 *       <li><code>asin(x)</code> (反正弦)</li>
 *       <li><code>acos(x)</code> (反余弦)</li>
 *       <li><code>atan(x)</code> (反正切)</li>
 *       <li><code>atan2(x)</code> (反正切2)</li>
 *       <li><code>sinh(x)</code> </li>
 *       <li><code>cosh(x)</code> </li>
 *       <li><code>tanh(x)</code> </li>
 *     </ul>
 *   </li>
 *   <li><b>对数函数</b>：
 *     <ul>
 *       <li><code>log(x)</code> (自然对数，底数为 e)</li>
 *       <li><code>log10(x)</code> (以 10 为底的对数)</li>
 *       <li><code>logN(x, base)</code> (任意底数对数)</li>
 *     </ul>
 *   </li>
 *   <li><b>指数函数</b>：
 *     <ul>
 *       <li><code>exp(x)</code> (以 e 为底的指数函数)</li>
 *     </ul>
 *   </li>
 *   <li><b>绝对值和其他数学函数</b>：
 *     <ul>
 *       <li><code>abs(x)</code> (绝对值)</li>
 *       <li><code>ceil(x)</code> (向上取整)</li>
 *       <li><code>floor(x)</code> (向下取整)</li>
 *       <li><code>truncate(x)</code> (取整)</li>
 *       <li><code>random()</code>(随机)</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * 支持的常量包括：
 * <ul>
 *   <li><code>pi</code> (圆周率 π)</li>
 *   <li><code>e</code> (自然对数的底数 e)</li>
 * </ul>
 *
 * 支持的条件运算函数：
 * <ul>
 *   <li><code>max(x, y)</code> (返回 x 和 y 中的最大值)</li>
 *   <li><code>min(x, y)</code> (返回 x 和 y 中的最小值)</li>
 * </ul>
 */
public class ExpressionExtendBuilder extends ExpressionBuilder {
    public static void main(String[] args){
        for (float i = 0; i < 1; i+=0.01f) {
            Expression e = new ExpressionExtendBuilder("(if(pt,0,0.5)*((2pt)^2)+if(pt,0.5,1)*((2-2pt)^2))*5")
                    .variables("t","y", "x","pt")
                    .build();
            e.setVariable("pt",i);
            double result = e.evaluate(); // result = π/4 (0.7853981633974483)
            System.out.println(result);
        }

    }

    public ExpressionExtendBuilder(String expression) {
        super(expression);
        addCustomFunctions();
    }

    private void addCustomFunctions() {
        this.function(new RandomFunction()); // 添加 random() 函数
        this.function(new CeilFunction());//向上取整
        this.function(new FloorFunction());
        this.function(new TruncateFunction());//直接取整
        this.function(new MaxFunction());//两个参数的max
        this.function(new MinFunction());
        this.function(new CoshFunction());
        this.function(new SinhFunction());
        this.function(new TanhFunction());
        this.function(new LogNFunction());
        this.function(new threshold_function());
        this.function(new if_function());
        this.function(new sgn());
        this.function(new BezierCurve3Function());
        this.function(new BezierCurve4Function());
    }
}

class RandomFunction extends Function {

    public RandomFunction() {
        super("random", 0);
    }

    @Override
    public double apply(double... args) {
        Random random = new Random();
        return random.nextDouble(); // 返回 0 到 1 之间的随机数
    }

}
class CeilFunction extends Function {
    public CeilFunction() {
        super("ceil", 1); // ceil 函数需要一个参数
    }

    @Override
    public double apply(double... args) {
        return Math.ceil(args[0]); // 向上取整
    }
}
class FloorFunction extends Function {
    public FloorFunction() {
        super("floor", 1); // floor 函数需要一个参数
    }

    @Override
    public double apply(double... args) {
        return Math.floor(args[0]); // 向下取整
    }
}
class TruncateFunction extends Function {
    public TruncateFunction() {
        super("truncate", 1); // truncate 函数需要一个参数
    }

    @Override
    public double apply(double... args) {
        return (int) args[0]; // 去掉小数部分，保留整数部分
    }
}
class MaxFunction extends Function {
    public MaxFunction() {
        super("max",2); // max 函数接受2个数量的参数
    }

    @Override
    public double apply(double... args) {
        double max = args[0];
        for (double arg : args) {
            if (arg > max) {
                max = arg;
            }
        }
        return max;
    }
}

class MinFunction extends Function {
    public MinFunction() {
        super("min",2); // min 函数接受2个数量的参数
    }

    @Override
    public double apply(double... args) {
        double min = args[0];
        for (double arg : args) {
            if (arg < min) {
                min = arg;
            }
        }
        return min;
    }
}
//截断函数,小于0=0,>=0输出原函数
class threshold_function extends Function{
    public threshold_function(){super("threshold",1);}

    @Override
    public double apply(double... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("截断函数需要一个参数");
        }
        return Math.max(0,args[0]);
    }
}
//在范围内输出1,否则出0
class if_function extends Function{
    public if_function(){super("if",3);}

    @Override
    public double apply(double... args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("if需要三个参数");
        }
        double input=args[0];
        double left=args[1];
        double right=args[2];
        if(left<=input&&input<=right){return 1;}
        else return 0;
    }
}


class SinhFunction extends Function {
    public SinhFunction() {
        super("sinh", 1); // sinh 函数接受一个参数
    }

    @Override
    public double apply(double... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("sinh函数需要一个参数");
        }
        return Math.sinh(args[0]);
    }
}
//当参数大于0时，返回1；等于0时返回0；小于0时返回-1
class sgn extends Function {
    public sgn() {
        super("sgn", 1); // sinh 函数接受一个参数
    }

    @Override
    public double apply(double... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("sgn函数需要一个参数");
        }
        if(args[0]>0) return 1;
        if(args[0]<0) return -1;
        else return 0;

    }
}

class CoshFunction extends Function {
    public CoshFunction() {
        super("cosh", 1); // cosh 函数接受一个参数
    }

    @Override
    public double apply(double... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("cosh函数需要一个参数");
        }
        return Math.cosh(args[0]);
    }
}
class TanhFunction extends Function {
    public TanhFunction() {
        super("tanh", 1); // tanh 函数接受一个参数
    }

    @Override
    public double apply(double... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("tanh函数需要一个参数");
        }
        return Math.tanh(args[0]);
    }
}
class LogNFunction extends Function {
    public LogNFunction() {
        super("logN", 2);}

    @Override
    public double apply(double... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("log(x,y)函数需要两个参数");
        }
         return Math.log(args[0]) / Math.log(args[1]);
    }
}
class BezierCurve3Function extends Function{
    public BezierCurve3Function(){
        super("BezierCurve3",4);
    }

    @Override
    public double apply(double... args){
        if (args.length != 4) {
            throw new IllegalArgumentException("BezierCurve(y0,y1,y2,t)函数需要四个参数");
        }
        double y0=args[0];
        double y1=args[1];
        double y2=args[2];
        double t=args[3];
        return Math.pow(1 - t, 2) * y0 + 2 * (1 - t) * t * y1 + Math.pow(t, 2) * y2;
    }
}
class BezierCurve4Function extends Function {
    public BezierCurve4Function() {
        super("BezierCurve4", 5); // 5个参数
    }

    @Override
    public double apply(double... args) {
        if (args.length != 5) {
            throw new IllegalArgumentException("BezierCurve4(y0,y1,y2,y3,t)函数需要五个参数");
        }
        double y0 = args[0];
        double y1 = args[1];
        double y2 = args[2];
        double y3 = args[3];
        double t = args[4];

        // 计算四次贝塞尔曲线的 y 值
        return Math.pow(1 - t, 3) * y0 +
                3 * Math.pow(1 - t, 2) * t * y1 +
                3 * (1 - t) * Math.pow(t, 2) * y2 +
                Math.pow(t, 3) * y3;
    }
}
