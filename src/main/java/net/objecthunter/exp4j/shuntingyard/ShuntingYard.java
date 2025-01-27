package net.objecthunter.exp4j.shuntingyard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.tokenizer.OperatorToken;
import net.objecthunter.exp4j.tokenizer.Token;
import net.objecthunter.exp4j.tokenizer.Tokenizer;

public class ShuntingYard {
   public static Token[] convertToRPN(String expression, Map<String, Function> userFunctions, Map<String, Operator> userOperators, Set<String> variableNames, boolean implicitMultiplication) {
      Stack<Token> stack = new Stack();
      List<Token> output = new ArrayList();
      Tokenizer tokenizer = new Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication);

      Token t;
      while(tokenizer.hasNext()) {
         t = tokenizer.nextToken();
         switch (t.getType()) {
            case 1:
            case 6:
               output.add(t);
               break;
            case 2:
               while(!stack.empty() && ((Token)stack.peek()).getType() == 2) {
                  OperatorToken o1 = (OperatorToken)t;
                  OperatorToken o2 = (OperatorToken)stack.peek();
                  if (o1.getOperator().getNumOperands() == 1 && o2.getOperator().getNumOperands() == 2 || (!o1.getOperator().isLeftAssociative() || o1.getOperator().getPrecedence() > o2.getOperator().getPrecedence()) && o1.getOperator().getPrecedence() >= o2.getOperator().getPrecedence()) {
                     break;
                  }

                  output.add(stack.pop());
               }

               stack.push(t);
               break;
            case 3:
               stack.add(t);
               break;
            case 4:
               stack.push(t);
               break;
            case 5:
               while(((Token)stack.peek()).getType() != 4) {
                  output.add(stack.pop());
               }

               stack.pop();
               if (!stack.isEmpty() && ((Token)stack.peek()).getType() == 3) {
                  output.add(stack.pop());
               }
               break;
            case 7:
               while(!stack.empty() && ((Token)stack.peek()).getType() != 4) {
                  output.add(stack.pop());
               }

               if (!stack.empty() && ((Token)stack.peek()).getType() == 4) {
                  break;
               }

               throw new IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses");
            default:
               throw new IllegalArgumentException("Unknown Token type encountered. This should not happen");
         }
      }

      while(!stack.empty()) {
         t = (Token)stack.pop();
         if (t.getType() == 5 || t.getType() == 4) {
            throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression");
         }

         output.add(t);
      }

      return (Token[])((Token[])output.toArray(new Token[output.size()]));
   }
}
