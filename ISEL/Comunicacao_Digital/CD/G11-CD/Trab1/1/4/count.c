#include <stdio.h>
#include <assert.h>

unsigned int count_ones(unsigned int i)
{
    int num = 0;
    while (i > 0)
    {
        num += i & 1;
        i = i >> 1;
    }
    return num;
}

unsigned int count_zeros(unsigned int i)
{
    int num = 0;
    if (i == 0)
    {
        return 1;
    }

    while (i > 0)
    {
        num += (i & 1) ^ 1;
        i = i >> 1;
    }
    return num;
}

void test_count_ones()
{
    assert(count_ones(0) == 0);
    assert(count_ones(1) == 1);
    assert(count_ones(2) == 1);
    assert(count_ones(3) == 2);
    assert(count_ones(4) == 1);
    assert(count_ones(5) == 2);
    assert(count_ones(6) == 2);
    assert(count_ones(7) == 3);
    assert(count_ones(8) == 1);
}

void test_count_zeros()
{
    assert(count_zeros(0) == 1);
    assert(count_zeros(1) == 0);
    assert(count_zeros(2) == 1);
    assert(count_zeros(3) == 0);
    assert(count_zeros(4) == 2);
    assert(count_zeros(5) == 1);
    assert(count_zeros(6) == 1);
    assert(count_zeros(7) == 0);
    assert(count_zeros(8) == 3);
}

int main()
{
    int x = 6;
    printf("Count Ones: %d\n", count_ones(x));
    printf("Count Zeros: %d\n", count_zeros(x));
    test_count_ones();
    test_count_zeros();
    printf("All tests passed!");
    return 0;
}