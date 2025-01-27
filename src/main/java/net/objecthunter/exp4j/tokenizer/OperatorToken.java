package net.objecthunter.exp4j.tokenizer;

import net.objecthunter.exp4j.operator.Operator;

public class OperatorToken extends Token {
   private final Operator operator;

   public OperatorToken(Operator op) {
      super(2);
      if (op == null) {
         throw new IllegalArgumentException("Operator is unknown for token.");
      } else {
         this.operator = op;
      }
   }

   public Operator getOperator() {
      return this.operator;
   }
}
