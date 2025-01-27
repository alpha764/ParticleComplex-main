package net.objecthunter.exp4j.function;

public class Functions {
   private static final int INDEX_SIN = 0;
   private static final int INDEX_COS = 1;
   private static final int INDEX_TAN = 2;
   private static final int INDEX_COT = 3;
   private static final int INDEX_LOG = 4;
   private static final int INDEX_LOG1P = 5;
   private static final int INDEX_ABS = 6;
   private static final int INDEX_ACOS = 7;
   private static final int INDEX_ASIN = 8;
   private static final int INDEX_ATAN = 9;
   private static final int INDEX_CBRT = 10;
   private static final int INDEX_CEIL = 11;
   private static final int INDEX_FLOOR = 12;
   private static final int INDEX_SINH = 13;
   private static final int INDEX_SQRT = 14;
   private static final int INDEX_TANH = 15;
   private static final int INDEX_COSH = 16;
   private static final int INDEX_POW = 17;
   private static final int INDEX_EXP = 18;
   private static final int INDEX_EXPM1 = 19;
   private static final int INDEX_LOG10 = 20;
   private static final int INDEX_LOG2 = 21;
   private static final int INDEX_SGN = 22;
   private static final Function[] builtinFunctions = new Function[23];

   public static Function getBuiltinFunction(String name) {
      if (name.equals("sin")) {
         return builtinFunctions[0];
      } else if (name.equals("cos")) {
         return builtinFunctions[1];
      } else if (name.equals("tan")) {
         return builtinFunctions[2];
      } else if (name.equals("cot")) {
         return builtinFunctions[3];
      } else if (name.equals("asin")) {
         return builtinFunctions[8];
      } else if (name.equals("acos")) {
         return builtinFunctions[7];
      } else if (name.equals("atan")) {
         return builtinFunctions[9];
      } else if (name.equals("sinh")) {
         return builtinFunctions[13];
      } else if (name.equals("cosh")) {
         return builtinFunctions[16];
      } else if (name.equals("tanh")) {
         return builtinFunctions[15];
      } else if (name.equals("abs")) {
         return builtinFunctions[6];
      } else if (name.equals("log")) {
         return builtinFunctions[4];
      } else if (name.equals("log10")) {
         return builtinFunctions[20];
      } else if (name.equals("log2")) {
         return builtinFunctions[21];
      } else if (name.equals("log1p")) {
         return builtinFunctions[5];
      } else if (name.equals("ceil")) {
         return builtinFunctions[11];
      } else if (name.equals("floor")) {
         return builtinFunctions[12];
      } else if (name.equals("sqrt")) {
         return builtinFunctions[14];
      } else if (name.equals("cbrt")) {
         return builtinFunctions[10];
      } else if (name.equals("pow")) {
         return builtinFunctions[17];
      } else if (name.equals("exp")) {
         return builtinFunctions[18];
      } else if (name.equals("expm1")) {
         return builtinFunctions[19];
      } else {
         return name.equals("signum") ? builtinFunctions[22] : null;
      }
   }

   static {
      builtinFunctions[0] = new Function("sin") {
         public double apply(double... args) {
            return Math.sin(args[0]);
         }
      };
      builtinFunctions[1] = new Function("cos") {
         public double apply(double... args) {
            return Math.cos(args[0]);
         }
      };
      builtinFunctions[2] = new Function("tan") {
         public double apply(double... args) {
            return Math.tan(args[0]);
         }
      };
      builtinFunctions[3] = new Function("cot") {
         public double apply(double... args) {
            double tan = Math.tan(args[0]);
            if (tan == 0.0) {
               throw new ArithmeticException("Division by zero in cotangent!");
            } else {
               return 1.0 / Math.tan(args[0]);
            }
         }
      };
      builtinFunctions[4] = new Function("log") {
         public double apply(double... args) {
            return Math.log(args[0]);
         }
      };
      builtinFunctions[21] = new Function("log2") {
         public double apply(double... args) {
            return Math.log(args[0]) / Math.log(2.0);
         }
      };
      builtinFunctions[20] = new Function("log10") {
         public double apply(double... args) {
            return Math.log10(args[0]);
         }
      };
      builtinFunctions[5] = new Function("log1p") {
         public double apply(double... args) {
            return Math.log1p(args[0]);
         }
      };
      builtinFunctions[6] = new Function("abs") {
         public double apply(double... args) {
            return Math.abs(args[0]);
         }
      };
      builtinFunctions[7] = new Function("acos") {
         public double apply(double... args) {
            return Math.acos(args[0]);
         }
      };
      builtinFunctions[8] = new Function("asin") {
         public double apply(double... args) {
            return Math.asin(args[0]);
         }
      };
      builtinFunctions[9] = new Function("atan") {
         public double apply(double... args) {
            return Math.atan(args[0]);
         }
      };
      builtinFunctions[10] = new Function("cbrt") {
         public double apply(double... args) {
            return Math.cbrt(args[0]);
         }
      };
      builtinFunctions[12] = new Function("floor") {
         public double apply(double... args) {
            return Math.floor(args[0]);
         }
      };
      builtinFunctions[13] = new Function("sinh") {
         public double apply(double... args) {
            return Math.sinh(args[0]);
         }
      };
      builtinFunctions[14] = new Function("sqrt") {
         public double apply(double... args) {
            return Math.sqrt(args[0]);
         }
      };
      builtinFunctions[15] = new Function("tanh") {
         public double apply(double... args) {
            return Math.tanh(args[0]);
         }
      };
      builtinFunctions[16] = new Function("cosh") {
         public double apply(double... args) {
            return Math.cosh(args[0]);
         }
      };
      builtinFunctions[11] = new Function("ceil") {
         public double apply(double... args) {
            return Math.ceil(args[0]);
         }
      };
      builtinFunctions[17] = new Function("pow", 2) {
         public double apply(double... args) {
            return Math.pow(args[0], args[1]);
         }
      };
      builtinFunctions[18] = new Function("exp", 1) {
         public double apply(double... args) {
            return Math.exp(args[0]);
         }
      };
      builtinFunctions[19] = new Function("expm1", 1) {
         public double apply(double... args) {
            return Math.expm1(args[0]);
         }
      };
      builtinFunctions[22] = new Function("signum", 1) {
         public double apply(double... args) {
            if (args[0] > 0.0) {
               return 1.0;
            } else {
               return args[0] < 0.0 ? -1.0 : 0.0;
            }
         }
      };
   }
}
