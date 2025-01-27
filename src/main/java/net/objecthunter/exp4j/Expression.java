package net.objecthunter.exp4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.function.Functions;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.tokenizer.FunctionToken;
import net.objecthunter.exp4j.tokenizer.NumberToken;
import net.objecthunter.exp4j.tokenizer.OperatorToken;
import net.objecthunter.exp4j.tokenizer.Token;
import net.objecthunter.exp4j.tokenizer.VariableToken;

public class Expression {
   private final Token[] tokens;
   private final Map<String, Double> variables;
   private final Set<String> userFunctionNames;

   private static Map<String, Double> createDefaultVariables() {
      Map<String, Double> vars = new HashMap(4);
      vars.put("pi", Math.PI);
      vars.put("π", Math.PI);
      vars.put("φ", 1.61803398874);
      vars.put("e", Math.E);
      return vars;
   }

   public Expression(Expression existing) {
      this.tokens = (Token[])Arrays.copyOf(existing.tokens, existing.tokens.length);
      this.variables = new HashMap();
      this.variables.putAll(existing.variables);
      this.userFunctionNames = new HashSet(existing.userFunctionNames);
   }

   Expression(Token[] tokens) {
      this.tokens = tokens;
      this.variables = createDefaultVariables();
      this.userFunctionNames = Collections.emptySet();
   }

   Expression(Token[] tokens, Set<String> userFunctionNames) {
      this.tokens = tokens;
      this.variables = createDefaultVariables();
      this.userFunctionNames = userFunctionNames;
   }

   public Expression setVariable(String name, double value) {
      this.checkVariableName(name);
      this.variables.put(name, value);
      return this;
   }

   private void checkVariableName(String name) {
      if (this.userFunctionNames.contains(name) || Functions.getBuiltinFunction(name) != null) {
         throw new IllegalArgumentException("The variable name '" + name + "' is invalid. Since there exists a function with the same name");
      }
   }

   public Expression setVariables(Map<String, Double> variables) {
      Iterator var2 = variables.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Double> v = (Map.Entry)var2.next();
         this.setVariable((String)v.getKey(), (Double)v.getValue());
      }

      return this;
   }

   public Set<String> getVariableNames() {
      Set<String> variables = new HashSet();
      Token[] var2 = this.tokens;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Token t = var2[var4];
         if (t.getType() == 6) {
            variables.add(((VariableToken)t).getName());
         }
      }

      return variables;
   }

   public ValidationResult validate(boolean checkVariablesSet) {
      List<String> errors = new ArrayList(0);
      int var5;
      if (checkVariablesSet) {
         Token[] var3 = this.tokens;
         int var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            Token t = var3[var5];
            if (t.getType() == 6) {
               String var = ((VariableToken)t).getName();
               if (!this.variables.containsKey(var)) {
                  errors.add("The setVariable '" + var + "' has not been set");
               }
            }
         }
      }

      int count = 0;
      Token[] var12 = this.tokens;
      var5 = var12.length;

      for(int var13 = 0; var13 < var5; ++var13) {
         Token tok = var12[var13];
         switch (tok.getType()) {
            case 1:
            case 6:
               ++count;
               break;
            case 2:
               Operator op = ((OperatorToken)tok).getOperator();
               if (op.getNumOperands() == 2) {
                  --count;
               }
               break;
            case 3:
               Function func = ((FunctionToken)tok).getFunction();
               int argsNum = func.getNumArguments();
               if (argsNum > count) {
                  errors.add("Not enough arguments for '" + func.getName() + "'");
               }

               if (argsNum > 1) {
                  count -= argsNum - 1;
               } else if (argsNum == 0) {
                  ++count;
               }
            case 4:
            case 5:
         }

         if (count < 1) {
            errors.add("Too many operators");
            return new ValidationResult(false, errors);
         }
      }

      if (count > 1) {
         errors.add("Too many operands");
      }

      return errors.size() == 0 ? ValidationResult.SUCCESS : new ValidationResult(false, errors);
   }

   public ValidationResult validate() {
      return this.validate(true);
   }

   public Future<Double> evaluateAsync(ExecutorService executor) {
      return executor.submit(new Callable<Double>() {
         public Double call() throws Exception {
            return Expression.this.evaluate();
         }
      });
   }

   public double evaluate() {
      ArrayStack output = new ArrayStack();

      for(int i = 0; i < this.tokens.length; ++i) {
         Token t = this.tokens[i];
         if (t.getType() == 1) {
            output.push(((NumberToken)t).getValue());
         } else if (t.getType() == 6) {
            String name = ((VariableToken)t).getName();
            Double value = (Double)this.variables.get(name);
            if (value == null) {
               throw new IllegalArgumentException("No value has been set for the setVariable '" + name + "'.");
            }

            output.push(value);
         } else if (t.getType() == 2) {
            OperatorToken op = (OperatorToken)t;
            if (output.size() < op.getOperator().getNumOperands()) {
               throw new IllegalArgumentException("Invalid number of operands available for '" + op.getOperator().getSymbol() + "' operator");
            }

            double arg;
            if (op.getOperator().getNumOperands() == 2) {
               arg = output.pop();
               double leftArg = output.pop();
               output.push(op.getOperator().apply(leftArg, arg));
            } else if (op.getOperator().getNumOperands() == 1) {
               arg = output.pop();
               output.push(op.getOperator().apply(arg));
            }
         } else if (t.getType() == 3) {
            FunctionToken func = (FunctionToken)t;
            int numArguments = func.getFunction().getNumArguments();
            if (output.size() < numArguments) {
               throw new IllegalArgumentException("Invalid number of arguments available for '" + func.getFunction().getName() + "' function");
            }

            double[] args = new double[numArguments];

            for(int j = numArguments - 1; j >= 0; --j) {
               args[j] = output.pop();
            }

            output.push(func.getFunction().apply(args));
         }
      }

      if (output.size() > 1) {
         throw new IllegalArgumentException("Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function.");
      } else {
         return output.pop();
      }
   }
}
