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
}
