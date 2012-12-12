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
    /* PrimitiveType(repr, integral, double, size, signed, crepr, isVoid, isBool) */
    // Viacslovne nazvy su usortene podla abecedy
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

  public String typeSpecifiersToString(List<String> typeSpecifiers) {
    java.util.Collections.sort(typeSpecifiers);
    StringBuffer buf = new StringBuffer();
    boolean first = true;
    for (String s : typeSpecifiers) {
      if (!first) buf.append(" ");
      first = false;
      buf.append(s);
    }
    return buf.toString();
  }

  public boolean isValidType(List<String> typeSpecifiers) {
    return isValidType(typeSpecifiersToString(typeSpecifiers));
  }

  public Type getType(List<String> typeSpecifiers) {
    return getType(typeSpecifiersToString(typeSpecifiers));
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

  public Type getArrayType(Type type, int count) {
    String crep = String.format("%s[%d]", type.getCrepr(), count);
    if (mapping.containsKey(crep))
      return getType(crep);
    Type tp = new ArrayType(type, count);
    mapping.put(crep, tp);
    return tp;
  }

  public Type getPointerType(Type type) {
    String crep = String.format("%s*", type.getCrepr());
    if (mapping.containsKey(crep))
      return getType(crep);
    Type tp = new PointerType(type);
    mapping.put(crep, tp);
    return tp;
    
  }

  public Type getTypeForFunction(Type returnValue, List<Type> arguments) {
    return getTypeForFunction(returnValue, arguments, false);
  }

  public Type getTypeForFunction(Type returnValue, List<Type> arguments, boolean varArgs) {
    String fType = FunctionType.buildCrepr(returnValue, arguments, varArgs);
    if (!mapping.containsKey(fType)) {
      Type t = new FunctionType(returnValue, arguments, varArgs);
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
    PointingType pt = (PointingType) type;
    return pt.getPointerTo();
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

    if (new_type.isPointer() && result.type.isIntegral()) {
      EvalResult res = new EvalResult(new_type);
      out.printf("%s = inttoptr %s %s to %s\n",
          res.getRepresentation(), result.type.getRepresentation(),
          result.getRepresentation(), new_type.getRepresentation());
      return res;
    }

    if (new_type.isDouble() && result.type.isDouble()) {
      EvalResult res = new EvalResult(new_type);
      if (new_type.sizeof() > result.type.sizeof()) {
        out.printf("%s = fpext %s %s to %s\n",
            res.getRepresentation(), result.type.getRepresentation(),
            result.getRepresentation(), new_type.getRepresentation());
      } else {
        out.printf("%s = fptrunc %s %s to %s\n",
            res.getRepresentation(), result.type.getRepresentation(),
            result.getRepresentation(), new_type.getRepresentation());
      }
      return res;
    }
    if (new_type.isIntegral() && result.type.isIntegral()) {
      EvalResult res = new EvalResult(new_type);
      // TODO: toto cele aj pre unsigned
      if (new_type.sizeof() > result.type.sizeof()) {
        out.printf("%s = sext %s %s to %s\n",
            res.getRepresentation(), result.type.getRepresentation(),
            result.getRepresentation(), new_type.getRepresentation());
      } else {
        out.printf("%s = trunc %s %s to %s\n",
            res.getRepresentation(), result.type.getRepresentation(),
            result.getRepresentation(), new_type.getRepresentation());
      }
      return res;
    }
    // TODO: konverzie signed -> unsigned a opacne
    return null;
  }

  // Ak pre unifikaciu treba skonvertoval typ A na typ B, tak vrati novy result pre A
  // Cize ak toho vlezie (double, int), tak vrati null
  // Ale ak vlezie (int, double), tak vrati prvy skonvertovany na double
  public EvalResult unifyTypes(EvalResult a, EvalResult b, PrintStream out) {
    if (b.type.isDouble() && a.type.isIntegral()) {
      return convertTo(b.type, a, out);
    }
    if (b.type.isDouble() && a.type.isDouble() && b.type.sizeof() > a.type.sizeof()) {
      return convertTo(b.type, a, out);
    }
    if (b.type.isIntegral() && a.type.isIntegral() && b.type.sizeof() > a.type.sizeof()) {
      return convertTo(b.type, a, out);
    }
    // TODO: konverzie signed -> unsigned a opacne
    return null;
  }
}
