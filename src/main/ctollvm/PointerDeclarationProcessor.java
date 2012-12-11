package ctollvm;
import java.io.PrintStream;
import java.lang.Exception;
import java.lang.StringBuffer;
import java.util.*;

public class PointerDeclarationProcessor extends DeclarationProcessor {
  public PointerDeclarationProcessor() {
    super();
  }

  public Type processType(Type type) throws Exception {
    return TypeSystem.getInstance().getPointerTo(type);
  }
}
