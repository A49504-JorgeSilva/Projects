#include <stdio.h>
#include <math.h>

void print_bits(int val)
{
    int size = log2(val) + 1;
    printf("The binary code of %d is: ", val);
    if (val != 0)
    {
        for (size; size > 0; size--)
        {
            printf("%d", (val >> size - 1) & 1);
        }
    }
    else
        printf("0");
    printf("\n");
}

void main()
{
    int x = 6;
    print_bits(x);
    printf("::     Starting Tests     :::\n");
    printf("::     Testing with 0     :::\n");
    print_bits(0);
    printf("\n");
    printf("::     Testing with 1     :::\n");
    print_bits(1);
    printf("\n");
    printf("::     Testing with 2     :::\n");
    print_bits(2);
    printf("\n");
    printf("::     Testing with 10     :::\n");
    print_bits(10);
    printf("\n");
    printf("::     Testing with 255     :::\n");
    print_bits(255);
    printf("\n");
}