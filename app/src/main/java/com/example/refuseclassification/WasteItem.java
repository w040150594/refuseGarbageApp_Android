package com.example.refuseclassification;

public class WasteItem implements Comparable<WasteItem> {
  private final String name;
  private final int type;
  private final int index;

  public WasteItem(String name, int type, int index) {
    this.name = name;
    this.type = type;
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public int getIndex() {
    return index;
  }

  // 实现Comparable接口以便排序
  public int compareTo(WasteItem other) {
    return Integer.compare(other.index, this.index); // 降序排序
  }

  public String getTypeName() {
    switch (type) {
      case 0:
        return "可回收";
      case 1:
        return "有害";
      case 2:
        return "厨余(湿)";
      case 3:
        return "其他(干)";
      default:
        return "未知";
    }
  }
}
