package ctollvm;
public class IdCounter {
  private static int id = 0;
  public static int GetNewId() {
    id++;
    return id;
  }

  public static String GetNewTmpVal() {
    return String.format("%%tmp.val.%d", GetNewId());
  }
  
  public static String GetNewLabel() {
    return String.format("label%d", GetNewId());
  }

  public static String GetNewStructName() {
    return String.format("struct.%d", GetNewId());
  }
}
