#include <assert.h>
#include <stdio.h>
#include <math.h>
#include <string.h>

void negative_file(char *input_file_name, char *output_file_name)
{
    char c;
    char inverted_c;
    int size;
    FILE *input_file, *output_file;
    input_file = fopen(input_file_name, "r");
    output_file = fopen(output_file_name, "w");

    if (input_file == NULL || output_file == NULL)
    {
        printf("Error opening file.\n");
        return;
    }

    while ((c = fgetc(input_file)) != EOF)
    {
        inverted_c = 0;
        size = log2(c) + 1;
        for (size; size > 0; size--)
        {
            inverted_c += ~(c >> size - 1) & 1;
            if (size != 1)
            {
                inverted_c *= 2;
            }
        }
        fputc(inverted_c, output_file);
    }
    fclose(input_file);
    fclose(output_file);
}

void test_negative_file()
{
    char *test_input_file = "test_input.txt";
    char *test_output_file = "test_output.txt";
    char *expected_output_file = "test_negative_expected.txt";
    FILE *input_file = fopen(test_input_file, "w");
    fprintf(input_file, "File For Testing\nnegative_file Function");
    fclose(input_file);
    negative_file(test_input_file, test_output_file);
    FILE *output_file = fopen(test_output_file, "r");
    FILE *expected_file = fopen(expected_output_file, "r");
    int output_char = fgetc(output_file);
    int expected_char = fgetc(expected_file);
    while (output_char != EOF && expected_char != EOF)
    {
        assert(output_char == expected_char);
        output_char = fgetc(output_file);
        expected_char = fgetc(expected_file);
    }
    assert(output_char == EOF && expected_char == EOF);
    fclose(output_file);
    fclose(expected_file);
    remove(test_input_file);
    remove(test_output_file);
}

void main(int argc, char *argv[])
{
    if (argc < 3)
    {
        printf("Please provide the two file names.\n");
        return;
    }
    negative_file(argv[1], argv[2]);
    test_negative_file();
    printf("All tests passed!\n");
}
