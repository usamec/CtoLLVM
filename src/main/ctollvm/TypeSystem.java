package ctollvm;
import java.io.PrintStream;
import java.lang.StringBuffer;
import java.util.*;

public class TypeSystem {
  public static TypeSystem instance = null;

  private Map<String, Type> mapping;


  private TypeSystem() {
    ResetTypes();
  }

  public void ResetTypes() {
    mapping = new HashMap<String, Type>();
    mapping.put("int", new PrimitiveType("i32", true, false, 4, true, "int", false, false));
    mapping.put("double",
        new PrimitiveType("double", false, true, 8, true, "double", false, false));
    mapping.put("long", mapping.get("int"));
    mapping.put("short", new PrimitiveType("i16", true, false, 2, true, "int", false, false));
    mapping.put("float",
        new PrimitiveType("float", false, true, 4, true, "float", false, false));
    // LLVM nemoze mat pointre na void, tak sa interne urobia ako i8
    mapping.put("void", new PrimitiveType("i8", false, false, 0, false, "void", true, false)); 
    mapping.put("char", new PrimitiveType("i8", true, false, 1, true, "char", false, false));
    mapping.put("_Bool", new PrimitiveType("i1", true, false, 1, false, "_Bool", false, true));
  }

  public static TypeSystem getInstance() {
    if (instance == null)
      instance = new TypeSystem();
    return instance;
  }

  public boolean isValidType(String type) {
    return mapping.containsKey(type);
  }

  public Type getType(String type) {
    return mapping.get(type);
  }

  public Type getType(String type, int pointerDepth) {
    if (pointerDepth == 0)
      return getType(type);

    StringBuffer buf = new StringBuffer(type);
    for (int i = 0; i < pointerDepth; i++)
      buf.append('*');

    String type2 = buf.toString();
    if (mapping.containsKey(type2))
      return getType(type2);

    Type tp = new PointerType(getType(type, pointerDepth-1));
    mapping.put(type2, tp);

    return tp;
  }

  public Type getTypeForFunction(Type returnValue, List<Type> arguments) {
    String fType = FunctionType.buildCrepr(returnValue, arguments);
    if (!mapping.containsKey(fType)) {
      Type t = new FunctionType(returnValue, arguments);
      mapping.put(fType, t);
    }
    return mapping.get(fType);     
  }

  public Type getPointerTo(Type type) {
    String typestring = type.getCrepr() + "*";
    if (mapping.containsKey(typestring)) {
      return mapping.get(typestring);
    }
    Type tp = new PointerType(type);
    mapping.put(typestring, tp);
    return tp;
  }

  public Type dereference(Type type) {
    PointerType pt = (PointerType) type;
    return pt.pointerTo;
  }

  public EvalResult convertTo(Type new_type, EvalResult result, PrintStream out) {
    if (new_type.isBool()) {
      if (result.type.isIntegral()) {
        EvalResult res = new EvalResult(new_type);
        out.printf("%s = icmp ne %s %s, 0\n", res.getRepresentation(),
            result.type.getRepresentation(), result.getRepresentation());
        return res;
      }
      if (result.type.isPointer()) {
        String tmpvar = IdCounter.GetNewTmpVal();
        EvalResult res = new EvalResult(new_type);
        out.printf("%s = ptrtoint %s %s to i64\n", tmpvar,
            result.type.getRepresentation(), result.getRepresentation());
        out.printf("%s = icmp ne i64 %s, 0\n",
            res.getRepresentation(), tmpvar);
        return res;
      }
      if (result.type.isDouble()) {
        EvalResult res = new EvalResult(new_type);
        out.printf("%s = fcmp une %s %s, 0.0\n", res.getRepresentation(),
            result.type.getRepresentation(), result.getRepresentation());
        return res;
      }
    }

    if (new_type.isDouble() && result.type.isIntegral()) {
      EvalResult res = new EvalResult(new_type);
      if (result.type.isSigned()) {
        out.println(String.format("%s = sitofp %s %s to %s", res.getRepresentation(),
            result.type.getRepresentation(), result.getRepresentation(),
            new_type.getRepresentation()));
      } else {
        out.println(String.format("%s = uitofp %s %s to %s", res.getRepresentation(),
            result.type.getRepresentation(), result.getRepresentation(),
            new_type.getRepresentation()));
      }
      return res;
    }
    if (new_type.isIntegral() && result.type.isDouble()) {
      EvalResult res = new EvalResult(new_type);
      if (new_type.isSigned()) {
        out.println(String.format("%s = fptosi %s %s to %s", res.getRepresentation(),
            result.type.getRepresentation(), result.getRepresentation(),
            new_type.getRepresentation()));
      } else {
        out.println(String.format("%s = fptoui %s %s to %s", res.getRepresentation(),
            result.type.getRepresentation(), result.getRepresentation(),
            new_type.getRepresentation()));
      }
      return res;
    }

    if (new_type.isPointer() && result.type.isPointer()) {
      EvalResult res = new EvalResult(new_type);
      out.println(String.format("%s = bitcast %s %s to %s",
            res.getRepresentation(), result.type.getRepresentation(),
            result.getRepresentation(), new_type.getRepresentation()));
      return res;
    }

    // TODO: konverzie toho isteho s inym poctom bitov
    return null;
  }

  public EvalResult unifyTypes(EvalResult a, EvalResult b, PrintStream out) {
    if (b.type.isDouble() && a.type.isIntegral()) {
      return convertTo(b.type, a, out);
    }
    // TODO: konverzie toho isteho s inym poctom bitov
    return null;
  }
}
