package ctollvm;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

import java.util.*;

public class Scope {
  class Variable {
    public Type type;
    public String name;

    // Pouzivane pre funkcie (ci je uz definovana predtym)
    public boolean defined = false;
    public Variable(Type type, String name) {
      this.type = type;
      this.name = name;
    }
  }

  private Scope parent;
  private Map<String, Variable> variables;
  private Stack<String> breakLabels;
  private Stack<String> continueLabels;

  // Aby sme pri returne z funkcie vedeli typ funkcie
  private Type functionReturnType = null;


  public Scope() {
    this(null);
  }

  public Scope(Scope p) {
    parent = p;
    variables = new HashMap<String, Variable>();
    breakLabels = new Stack<String>();
    continueLabels = new Stack<String>();
  }

  public Scope parent() {
    return parent;
  }

  public void pushBreakLabel(String label) {
    breakLabels.push(label);
  }

  public void popBreakLabel() {
    breakLabels.pop();
  }

  public String getBreakLabel() {
    if (breakLabels.empty()) {
      if (parent != null)
        return parent.getBreakLabel();
      return null;
    }
    return breakLabels.peek();
  }

  public void pushContinueLabel(String label) {
    continueLabels.push(label);
  }

  public void popContinueLabel() {
    continueLabels.pop();
  }

  public String getContinueLabel() {
    if (continueLabels.empty()) {
      if (parent != null)
        return parent.getContinueLabel();
      return null;
    }
    return continueLabels.peek();
  }

  public void setFunctionReturnType(Type t) {
    functionReturnType = t;
  }

  public Type getFunctionReturnType() {
    if (functionReturnType != null)
      return functionReturnType;
    if (parent != null)
      return parent.getFunctionReturnType();
    return null;
  }

  public boolean isGlobal() {
    if (parent == null)
      return true;
    else
      return false;
  }

  public Variable findInCurrentScope(String name) {
    if (hasInCurrentScope(name))
      return variables.get(name);
    return null;
  }

  public boolean hasInCurrentScope(String name) {
    return variables.containsKey(name);
  }

  public boolean hasInScope(String name) {
    Scope s = this;
    while (s != null) {
      if (s.hasInCurrentScope(name))
        return true;
      s = s.parent;
    }
    return false;
  }

  public Variable findInScope(String name) {
    Scope s = this;
    while (s != null) {
      Variable v = s.findInCurrentScope(name);
      if (v != null)
        return v;
      s = s.parent;
    }
    return null;
  }

  public Variable addVariable(String name, Type type) {
    String newname;
    if (type.isFunction()) {
      newname = name;
    } else if (isGlobal()) {
      newname = String.format("@%s.%d", name, IdCounter.GetNewId());
    } else {
      newname = String.format("%%%s.%d", name, IdCounter.GetNewId());
    }
    Variable v = new Variable(type, newname);
    variables.put(name, v);
    return v;
  }
}
