import webmo.*;

Webmo webmo;

void setup() {
  // webmo.localに接続
  webmo = new Webmo();
  webmo.rotate(500);
}

void draw() {
  println(webmo.rotation());
}

void exit() {
  webmo.stop(false);
  super.exit();
}