#include <stdio.h>
#include <assert.h>
#include <string.h>

#define MAX_NUMBER_SYMBOLS 128

typedef struct
{
    char symb;
    int freq;
} Frequency;

char most_frequent_symbol(FILE *input_file)
{
    Frequency freq[MAX_NUMBER_SYMBOLS];
    Frequency most_common;
    char c;
    int i;
    int size = 0;
    while ((c = fgetc(input_file)) != EOF)
    {
        for (i = 0; i < size; i++)
        {
            if (freq[i].symb == c)
            {
                freq[i].freq++;
                break;
            }
        }
        if (i == size)
        {
            freq[size].symb = c;
            freq[size].freq = 1;
            size++;
        }
    }
    most_common = freq[0];
    for (i = 1; i < size; i++)
    {
        if (most_common.freq < freq[i].freq)
        {
            most_common = freq[i];
        }
    }

    c = most_common.symb;

    return c;
}

void test_most_frequent_symbol()
{
    char *test_input_file = "test_input.txt";
    FILE *input_file = fopen(test_input_file, "w");
    fprintf(input_file, "File For Testing\nmost_frequent_symbol Function");
    fclose(input_file);
    input_file = fopen(test_input_file, "r");
    char actualSymbol = most_frequent_symbol(input_file);
    char expectedSymbol = 'e';
    assert(actualSymbol == expectedSymbol);
    fclose(input_file);
    remove(test_input_file);
}

void main(int argc, char *argv[])
{
    if (argc < 2)
    {
        printf("Please provide a file name.\n");
        return;
    }
    FILE *input_file;
    input_file = fopen(argv[1], "r");
    if (input_file == NULL)
    {
        printf("Error opening file.\n");
        return;
    }
    char rsp = most_frequent_symbol(input_file);
    if (rsp == '\n')
    {
        printf("Most common symbol is 'ENTER'\n", rsp);
    }
    else
    {
        printf("Most common symbol is '%c'\n", rsp);
    }
    fclose(input_file);
    test_most_frequent_symbol();
    printf("All tests passed.\n");
}