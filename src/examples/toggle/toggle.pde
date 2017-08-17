import webmo.*;

Webmo webmo;
boolean isRotating = false;

void setup() {
  // webmo.localに接続
  webmo = new Webmo();  
}

void draw() {
}

void mousePressed() {
  if(!isRotating) {
    webmo.rotate(500);
  } else {
    webmo.stop(false);
  }
  isRotating = !isRotating;
}