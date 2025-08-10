#include <stdio.h>
//a)
unsigned int count_ones(unsigned int i){
    int num = 0;
    while(i > 0){
        num += i & 1;
        i = i >> 1;
    }
    return num;
}

unsigned int count_zeros(unsigned int i){
    int num = 0;
    while(i > 0){
        num +=  (i&1)^1;
        i = i >> 1;
    }
    return num;
}

int main(){
    int x = 6;
    printf("Count Ones: %d\n",count_ones(x));
    printf("Count Zeros: %d\n",count_zeros(x));
    return 0;
}