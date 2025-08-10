#include <stdio.h>
#include <math.h>

int d1 = 1000;
int u = 3;
int r = 2;
int n = 5;

void setup() {
  Serial.begin(9600);
}

void loop() {
    for (int i = 0; i < n; i++) {
        Serial.println(u * pow(r, i));
        delay(d1);
    }
}
