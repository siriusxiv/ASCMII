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


Ball [] b = new Ball[50];
SmallBall [] sb = new SmallBall[b.length];
int [] swit = new int[b.length];
float bsize = 40; //diamètre des boules
float fr=0.995;   //frottements (1=pas de frottement, 0=frottements infinis)
void setup() {
  size(630, 300);
  smooth();
  for ( int i = 0; i<b.length; i++) {
    b[i] = new Ball(random(bsize/2, width-bsize/2), random(bsize/2, height-bsize/2));
    swit[i]=1;
    while (superposition (b, i)==0) {
      b[i] = new Ball(random(bsize/2, width-bsize/2), random(bsize/2, height-bsize/2));
    }
  }
}

void draw() {
  background(0);
  for ( int i = 0; i<b.length; i++) {
    b[i].run();
    if (b[i].versrouge>255) {
      if (swit[i]==1) {
        sb[i] = new SmallBall(b[i].x, b[i].y, abs(b[i].speedX)+abs(b[i].speedY)+1, abs(b[i].speedX)+abs(b[i].speedY)+1);
        swit[i]=0;
        b[i].x=-500; 
        b[i].y=-500;
        b[i].speedX=0; 
        b[i].speedY=0;
      }
      sb[i].run();
    }
  }
  for (int i = 0; i<b.length; i++) {
    for (int j = i+1 ; j<b.length; j++) {
      if (i!=j) {
        if (collision(b[i], b[j])==0) {
          float th = atan2(b[j].y-b[i].y, b[j].x-b[i].x);
          float ui=b[i].speedX*cos(th)+b[i].speedY*sin(th);
          float vi=-b[i].speedX*sin(th)+b[i].speedY*cos(th);
          float uj=b[j].speedX*cos(th)+b[j].speedY*sin(th);
          float vj=-b[j].speedX*sin(th)+b[j].speedY*cos(th);
          b[i].versrouge+=abs(ui)+abs(uj);
          b[j].versrouge+=abs(ui)+abs(uj);
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
            if (b[i].x>b[j].x) {
              b[i].x++;
            }
            else {
              b[i].x--;
            }
            if (b[i].y>b[j].y) {
              b[i].y++;
            }
            else {
              b[i].y--;
            }
          }
        }
      }
    }
  }
}

class Ball {
  float x = 0;
  float y = 0;
  float speedX = 0; 
  float speedY = 0;  
  float versrouge = 0;
  int stop = 1;

  Ball(float _x, float _y) {
    x=_x;
    y=_y;
    run();
  }

  void run() {
    display();
    move();
    shove();
    bounce();
    friction();
    unstick();
  }

  void display() {
    fill(255, 255-versrouge, 255-versrouge);
    ellipse(x, y, bsize, bsize);
  }

  void move() {
    x+=speedX;
    y+=speedY;
  }
  void shove() {
    if (hover(x, y)==0 && mousePressed) { //la souris est au-dessus de la boule)
      speedX=speedMX(x);
      speedY=speedMY(y);
    }
  }
  void bounce() {
    if (x > width-bsize/2 || x<bsize/2) {
      speedX=-speedX;
      versrouge+=abs(speedX);
    }
    if (y > height-bsize/2 || y<bsize/2) {
      speedY=-speedY;
      versrouge+=abs(speedY);
    }
    if (versrouge>255) {
      stop=0;
    }
  }
  void friction() {
    speedX=speedX*fr;
    speedY=speedY*fr;
  }
  void unstick() {
    if (stop==1){
      if (x > width-bsize/2) {
        x=width-bsize/2;
      }
      else if (x<bsize/2) {
        x=bsize/2;
      }
      if (y > height-bsize/2) {
        y=height-bsize/2;
      }
      else if (y<bsize/2) {
        y=bsize/2;
      }
    }
  }
}

float distance2(float x1, float y1, float x2, float y2){
  float a = (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1);
  return a;
}

int hover(float x,float y){
  if(distance2(mouseX, mouseY, x,y)<bsize*bsize/4){
    return 0;}
  else{return 1;}
}

float speedMX(float x){
  return (mouseX-x);
}

float speedMY(float y){
  return (mouseY-y);
}

int collision(Ball b1,Ball b2){
  if(distance2(b1.x,b1.y,b2.x,b2.y)<bsize*bsize){
    return 0;
  } else{ return 1;}
}

int superposition(Ball[] b,int i){
  int c = 1;
  for(int j = 0; j<i; j++){
    c*=collision(b[i],b[j]);
  }
  return c;
}

class SmallBall{
 float xi = 0;
 float yi = 0;
 float speedX = 0;
 float speedY = 0;
 float x = 0;
 float y = 0;
 
 SmallBall(float _x,float _y, float _sX, float _sY){
  xi=_x;
  yi=_y;
  speedX=_sX;
  speedY=_sY;
  run();
 }
 
 void run(){
   display();
   move();
 }
 
 void display(){
   fill(255,0,0);
   ellipse(xi+x,yi+y,bsize/2,bsize/2);
   ellipse(xi+x,yi-y,bsize/2,bsize/2);
   ellipse(xi-x,yi+y,bsize/2,bsize/2);
   ellipse(xi-x,yi-y,bsize/2,bsize/2);
 }
 
 void move(){
   x+=speedX;
   y+=speedY;
 }
}