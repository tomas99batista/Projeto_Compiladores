import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

public class CalculatorMain {
   public static void main(String[] args) throws Exception {

      Scanner sc = new Scanner(System.in);
      String line = "";

      while (true) {
         try {
            line = sc.nextLine();
         } catch (NoSuchElementException e) {
            System.exit(0);
         }
         // create a CharStream that reads from standard input:
         CharStream input = CharStreams.fromString(line);
         // create a lexer that feeds off of input CharStream:
         CalculatorLexer lexer = new CalculatorLexer(input);
         // create a buffer of tokens pulled from the lexer:
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         // create a parser that feeds off the tokens buffer:
         CalculatorParser parser = new CalculatorParser(tokens);
         // replace error listener:
         // parser.removeErrorListeners(); // remove ConsoleErrorListener
         // parser.addErrorListener(new ErrorHandlingListener());
         // begin parsing at program rule:
         ParseTree tree = parser.program();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            Calculator_visitor visitor0 = new Calculator_visitor();
            visitor0.visit(tree);
         }
      }
   }
}
