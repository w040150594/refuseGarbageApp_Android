package com.example.refuseclassification.Database;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Knowledge extends LitePalSupport implements Serializable {

  private int id;
  private String name;
  private String kind;
  private String answer;
  private String tips;

  public Knowledge() {
  }

  public Knowledge(String name, String kind, String tips) {
    this.name = name;
    this.kind = kind;
    this.tips = tips;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getTips() {
    return tips;
  }
}
