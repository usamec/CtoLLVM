package ctollvm;
import java.util.*;

public class StructType implements Type {
  boolean incomplete = true;
  String internalName;
  List<DeclaredVariable> declaredVariables;
  Map<String, Integer> variablePositions;

  public StructType(String internalName) {
    this.incomplete = true;
    this.internalName = internalName;
    this.variablePositions = new HashMap<String, Integer>();
  }

  public void setDeclaredVariables(List<DeclaredVariable> declaredVariables) {
    this.incomplete = false;
    this.declaredVariables = declaredVariables;
    int ind = 0;
    for (DeclaredVariable d : this.declaredVariables) {
      variablePositions.put(d.name, ind);
      ind++;
    }
  }

  public int getMemberIndex(String member) throws Exception {
    if (!variablePositions.containsKey(member)) {
      throw new Exception("Unknown member name");
    }
    return variablePositions.get(member);
  }

  public Type getMemberType(int index) {
    return declaredVariables.get(index).type;
  }

  public boolean isIncomplete() {
    return incomplete;
  }

  public String getRepresentation() {
    return String.format("%%%s", internalName);
  }

  public String getTypeRepresentation() {
    if (incomplete) {
      return "type opaque";
    } else {
      StringBuffer buf = new StringBuffer();
      buf.append("{");
      boolean first = true;
      for (DeclaredVariable v : declaredVariables) {
        if (!first) buf.append(", ");
        first = false;
        buf.append(v.type.getRepresentation());
      }
      buf.append("}");
      return buf.toString();
    }
  }

  public String getCrepr() {
    return String.format("struct %s", internalName);
  }

  public boolean isIntegral() {
    return false;
  }

  public boolean isDouble() {
    return false;
  }
  
  public boolean isSigned() {
    return false;
  }
  
  public int sizeof() {
    // TODO: ked je complete, tak vyratat
    return 0;
  }

  public boolean isPointer() {
    return false;
  }

  public boolean isVoid() {
    return incomplete;
  }

  public boolean isFunction() {
    return false;
  }

  public boolean isBool() {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isTypedef() {
    return false;
  }
}
