package net.objecthunter.exp4j.operator;

public abstract class Operators {
   private static final int INDEX_ADDITION = 0;
   private static final int INDEX_SUBTRACTION = 1;
   private static final int INDEX_MUTLIPLICATION = 2;
   private static final int INDEX_DIVISION = 3;
   private static final int INDEX_POWER = 4;
   private static final int INDEX_MODULO = 5;
   private static final int INDEX_UNARYMINUS = 6;
   private static final int INDEX_UNARYPLUS = 7;
   private static final Operator[] builtinOperators = new Operator[8];

   public static Operator getBuiltinOperator(char symbol, int numArguments) {
      switch (symbol) {
         case '%':
            return builtinOperators[5];
         case '*':
            return builtinOperators[2];
         case '+':
            if (numArguments != 1) {
               return builtinOperators[0];
            }

            return builtinOperators[7];
         case '-':
            if (numArguments != 1) {
               return builtinOperators[1];
            }

            return builtinOperators[6];
         case '/':
            return builtinOperators[3];
         case '^':
            return builtinOperators[4];
         default:
            return null;
      }
   }

   static {
      builtinOperators[0] = new Operator("+", 2, true, 500) {
         public double apply(double... args) {
            return args[0] + args[1];
         }
      };
      builtinOperators[1] = new Operator("-", 2, true, 500) {
         public double apply(double... args) {
            return args[0] - args[1];
         }
      };
      builtinOperators[6] = new Operator("-", 1, false, 5000) {
         public double apply(double... args) {
            return -args[0];
         }
      };
      builtinOperators[7] = new Operator("+", 1, false, 5000) {
         public double apply(double... args) {
            return args[0];
         }
      };
      builtinOperators[2] = new Operator("*", 2, true, 1000) {
         public double apply(double... args) {
            return args[0] * args[1];
         }
      };
      builtinOperators[3] = new Operator("/", 2, true, 1000) {
         public double apply(double... args) {
            if (args[1] == 0.0) {
               throw new ArithmeticException("Division by zero!");
            } else {
               return args[0] / args[1];
            }
         }
      };
      builtinOperators[4] = new Operator("^", 2, false, 10000) {
         public double apply(double... args) {
            return Math.pow(args[0], args[1]);
         }
      };
      builtinOperators[5] = new Operator("%", 2, true, 1000) {
         public double apply(double... args) {
            if (args[1] == 0.0) {
               throw new ArithmeticException("Division by zero!");
            } else {
               return args[0] % args[1];
            }
         }
      };
   }
}
