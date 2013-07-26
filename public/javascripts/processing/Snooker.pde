/****************************************************************************

Copyright (c) 2013, Boussejra Malik Olivier from the Ecole Centrale de Nantes
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
* Neither the name of the copyright holder nor the names of its contributors
  may be used to endorse or promote products derived from this software
  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

******************************************************************************/


Ball [] b = new Ball[16];
int fallen = 0;

void setup() {
  size(840, 420);
  smooth();
  create_balls();
}

void draw() {
  board();
  moveballs();
  //collisions entre boules
  for (int i = 0; i<b.length; i++) {
    for (int j = i+1 ; j<b.length; j++) {
      if (collision(b[i], b[j])==0) {
        float th = atan2(b[j].y-b[i].y, b[j].x-b[i].x);
        float ui=b[i].speedX*cos(th)+b[i].speedY*sin(th);
        float vi=-b[i].speedX*sin(th)+b[i].speedY*cos(th);
        float uj=b[j].speedX*cos(th)+b[j].speedY*sin(th);
        float vj=-b[j].speedX*sin(th)+b[j].speedY*cos(th);
        float nvi=vi;
        float nvj=vj;
        float nui=uj;
        float nuj=ui;
        b[i].speedX=nui*cos(th)-nvi*sin(th);
        b[i].speedY=nui*sin(th)+nvi*cos(th);
        b[j].speedX=nuj*cos(th)-nvj*sin(th);
        b[j].speedY=nuj*sin(th)+nvj*cos(th);
        //séparer boules collées (cela arrive quand ça va trop vite
        while (collision (b[i], b[j])==0) {
          if(b[i].x>b[j].x){
            b[i].x++;
          }else{
            b[i].x--;
          }
          if(b[i].y>b[j].y){
            b[i].y++;
          }else{
            b[i].y--;
          }
        }
      }
    }
  }
}

class Ball {
  float x = 0;
  float y = 0;
  float r = 255;
  float g = 255;
  float b = 255;
  float speedX = 0;
  float speedY = 0;
  int white = 1;
  int trou = 0;
  float strength;
  
  Ball(float _x, float _y, int colour) {
    x=_x;
    y=_y;
    if (colour==1) {
      r=0;
      g=0;
      b=0;
      white=0;
    }
    else if (colour==2) {
      r=255;
      g=0;
      b=0;
      white=0;
    }
    else if (colour==3) {
      r=255;
      g=255;
      b=0;
      white=0;
    }
  }

  void run() {
    display();
    move();
    fall();
    if (white==1) {
      shove();
      if (trou==1) {
        x=210;
        y=210;
        trou=0;
      }
    }
    fr();
    bounce();
    if (trou==0) {
      unstick();
    }
  }

  void display() {
    fill(r, g, b);
    ellipse(x, y, 20.32, 20.32);
  }

  void move() {
    x+=speedX;
    y+=speedY;
  }

  void shove() {
    if (hover(x, y)==0 && mousePressed) { //la souris est au-dessus de la boule)
      speedX=speedMX(x);
      speedY=speedMY(y);
    } else if(mousePressed){
      strength=20;
      speedX=strength*(mouseX-x)/sqrt((mouseY-y)*(mouseY-y)+(mouseX-x)*(mouseX-x));
      speedY=strength*(mouseY-y)/sqrt((mouseY-y)*(mouseY-y)+(mouseX-x)*(mouseX-x));
    }
  }

  void fr() {
    speedX*=0.99;
    speedY*=0.99;
    if (abs(speedX)+abs(speedY)<0.1) {
      speedX=0;
      speedY=0;
    }
    else if (abs(speedX)+abs(speedY)<1) {
      speedX*=0.95;
      speedY*=0.95;
    }
  }

  void bounce() {
    if (x > width-20.32/2-7 || x<20.32/2+7) {
      speedX=-0.95*speedX;
      speedY=0.95*speedY;
    }
    if (y > height-20.32/2-7 || y<20.32/2+7) {
      speedY=-0.95*speedY;
      speedX=0.95*speedX;
    }
  }

  void unstick() {
    if (x > width-20.32/2-7) {
      x=width-20.32/2-7;
    }
    else if (x<20.32/2+7) {
      x=20.32/2+7;
    }
    if (y > height-20.32/2-7) {
      y=height-20.32/2-7;
    }
    else if (y<20.32/2+7) {
      y=20.32/2+7;
    }
  }

  void fall() {
    if (distance2(x, y, 1, 1)<1089/1.5 ||distance2(x, y, 420, 1)<1089/1.5 ||distance2(x, y, 839, 1)<1089/1.5 ||distance2(x, y, 1, 419)<1089/3 ||distance2(x, y, 420, 419)<1089/3 ||distance2(x, y, 839, 419)<1089/1.5) {
      if (trou == 0 && white==0) {
        x=30+22*fallen;
        y=-4;
        fallen++;
      }
      trou=1;
      speedX=0;
      speedY=0;
    }
  }
}

void board() {
  background(0, 255, 0);
  stroke(255);
  line(210, 0, 210, 420);
  noFill();
  arc(210, 210, 210, 210, PI/2, 3*PI/2);
  fill(150, 255, 150);
  rect(0, 0, 840, 7);
  rect(0, 0, 7, 420);
  rect(833, 0, 840, 420);
  rect(0, 413, 840, 420);
  noStroke();
  fill(0);
  ellipse(210, 210, 5, 5);
  ellipse(630, 210, 5, 5);
  stroke(20, 255, 30);
  ellipse(1, 1, 40, 40);
  ellipse(420, 1, 40, 40);
  ellipse(839, 1, 40, 40);
  ellipse(1, 419, 40, 40);
  ellipse(420, 419, 33, 33);
  ellipse(839, 419, 33, 33);
}


int testMovement(Ball[] b) {  //si rien ne bouge, renvoie 0
  int c = 0;
  for (int i = 0; i<b.length; i++) {
    if (b[i].speedX!=0 || b[i].speedY!=0) {
      c++;
    }
  }
  return c;
}

float distance2(float x1, float y1, float x2, float y2) {
  float a = (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1);
  return a;
}

int collision(Ball b1, Ball b2) {
  if (distance2(b1.x, b1.y, b2.x, b2.y)<20.32*20.32) {
    return 0;
  } 
  else { 
    return 1;
  }
}

int hover(float x, float y) {
  if (distance2(mouseX, mouseY, x, y)<20.32*20.32) { //on devrait diviser par 4, mais on préfère être au dessus d'un grand espace autour de la boule
    return 0;
  }
  else {
    return 1;
  }
}

float speedMX(float x) {
  return (mouseX-x);
}

float speedMY(float y) {
  return (mouseY-y);
}

void create_balls() {
  b[0] = new Ball(210, 210, 0);
  b[1] = new Ball(630, 210, 1);
  b[2] = new Ball(630, 189, 2);
  b[3] = new Ball(630, 231, 3);
  b[4] = new Ball(612, 199, 3);
  b[5] = new Ball(612, 220, 2);
  b[6] = new Ball(594, 210, 2);
  b[7] = new Ball(648, 178, 3);
  b[8] = new Ball(648, 199, 2);
  b[9] = new Ball(648, 220, 3);
  b[10] = new Ball(648, 241, 2);
  b[11] = new Ball(666, 168, 2);
  b[12] = new Ball(666, 189, 2);
  b[13] = new Ball(666, 210, 3);
  b[14] = new Ball(666, 231, 2);
  b[15] = new Ball(666, 252, 3);
}

void moveballs() {
  for (int i=0;i<b.length;i++) {
    b[i].run();
  }
}

