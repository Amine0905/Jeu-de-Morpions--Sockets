package ex2;

import java.io.Serializable;

public class MorpionData implements Serializable{
  private int posX;
  private int posY;

  public MorpionData(int posX, int posY) {
    this.posX = posX;
    this.posY = posY;
  }

  public int getPosX() {
    return posX;
  }

  public int getPosY() {
    return posY;
  }
}
