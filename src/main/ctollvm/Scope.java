package ctollvm;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

import java.util.HashMap;
import java.util.Map;

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


  public Scope() {
    this(null);
  }

  public Scope(Scope p) {
    parent = p;
    variables = new HashMap<String, Variable>();
  }

  public Scope parent() {
    return parent;
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
    if (type.isFunction())
      newname = name;
    else
      newname = String.format("%%%s.%d", name, IdCounter.GetNewId());
    Variable v = new Variable(type, newname);
    variables.put(name, v);
    return v;
  }
}
