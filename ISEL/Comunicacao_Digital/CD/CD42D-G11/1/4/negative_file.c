#include <stdio.h>
#include <math.h>

void negative_file(char * input_file_name, char * output_file_name){
    char c;
    char inverted_c;
    int size;
    FILE *input_file, *output_file;
    input_file = fopen(input_file_name,"r");
    output_file = fopen(output_file_name,"w");

    if (input_file == NULL || output_file == NULL) {
        printf("Error opening file.\n");
        return;
    }

    while ((c = fgetc(input_file)) != EOF) {
        inverted_c = 0;
        size = log2(c) + 1;
        for (size; size > 0; size--){   
            inverted_c += ~(c >> size - 1) & 1;
            if(size!=1){
                inverted_c *= 2;
            }
        }
        fputc(inverted_c, output_file);
    }
    fclose(input_file);
    fclose(output_file);
}

void main(int argc, char *argv[]) {
    if (argc < 3) {
        printf("Please provide the two file names.\n");
        return;
    }
    negative_file(argv[1],argv[2]);
    //negative_file("file.txt","out.txt");
}