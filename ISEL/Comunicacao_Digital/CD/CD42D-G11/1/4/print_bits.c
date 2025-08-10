#include <stdio.h>
#include <math.h>

//b)
void print_bits(int val)
{
    int size = log2(val) + 1;
    printf("The binary code of %d is: ", val);
    for (size; size > 0; size--)
    {
        printf("%d ", (val >> size - 1) & 1);
    }
}

int main(){
    int x = 6;
    print_bits(x);
    return 0;
}