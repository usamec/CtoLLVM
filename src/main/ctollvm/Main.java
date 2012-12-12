package ctollvm;

import ctollvm.parser.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import java.io.*;

public class Main {
  public static void main(String[] args) throws Exception {
    // create an instance of the lexer
    projektLexer lexer = new projektLexer(new ANTLRFileStream("test.c"));
        
    // wrap a token-stream around the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);
        
    // create the parser
    projektParser parser = new projektParser(tokens);
  
    // invoke the entry point of our parser and generate a DOT image of the tree
    CommonTree tree = (CommonTree)parser.parse().getTree();
    DOTTreeGenerator gen = new DOTTreeGenerator();
    StringTemplate st = gen.toDOT(tree);
    System.out.println(st);
/*    CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
    projektWalker walker = new projektWalker(nodes);
    PNode node = walker.walk();
    PrintStream output = new PrintStream(new FileOutputStream("test.ll"));
    if (node != null) {
      node.produceOutput(output);
    }*/
  }
}
