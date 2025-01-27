package net.objecthunter.exp4j.operator;

public abstract class Operator {
   public static final int PRECEDENCE_ADDITION = 500;
   public static final int PRECEDENCE_SUBTRACTION = 500;
   public static final int PRECEDENCE_MULTIPLICATION = 1000;
   public static final int PRECEDENCE_DIVISION = 1000;
   public static final int PRECEDENCE_MODULO = 1000;
   public static final int PRECEDENCE_POWER = 10000;
   public static final int PRECEDENCE_UNARY_MINUS = 5000;
   public static final int PRECEDENCE_UNARY_PLUS = 5000;
   public static final char[] ALLOWED_OPERATOR_CHARS = new char[]{'+', '-', '*', '/', '%', '^', '!', '#', '§', '$', '&', ';', ':', '~', '<', '>', '|', '='};
   protected final int numOperands;
   protected final boolean leftAssociative;
   protected final String symbol;
   protected final int precedence;

   public Operator(String symbol, int numberOfOperands, boolean leftAssociative, int precedence) {
      this.numOperands = numberOfOperands;
      this.leftAssociative = leftAssociative;
      this.symbol = symbol;
      this.precedence = precedence;
   }

   public static boolean isAllowedOperatorChar(char ch) {
      char[] var1 = ALLOWED_OPERATOR_CHARS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char allowed = var1[var3];
         if (ch == allowed) {
            return true;
         }
      }

      return false;
   }

   public boolean isLeftAssociative() {
      return this.leftAssociative;
   }

   public int getPrecedence() {
      return this.precedence;
   }

   public abstract double apply(double... var1);

   public String getSymbol() {
      return this.symbol;
   }

   public int getNumOperands() {
      return this.numOperands;
   }
}
