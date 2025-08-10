#include <stdio.h>

#define MAX_NUMBER_SYMBOLS 128

typedef struct {
    char symb;
    int freq;
} Frequency;

char most_frequent_symbol(FILE * input_file){
    Frequency freq[MAX_NUMBER_SYMBOLS];
    Frequency most_common;
    char c;
    int i;
    int size = 0;

    while ((c = fgetc(input_file)) != EOF) {
        	// Check if symbol is already in the array
        for (i = 0; i < size; i++) {
            if (freq[i].symb == c) {
                freq[i].freq++;
                break;
            }
        }
        // Add symbol to array if not already present
        if (i == size) {
            freq[size].symb = c;
            freq[size].freq = 1;
            size++;
        }
    }

    most_common = freq[0];

    for (i = 1; i < size; i++) {
        if(most_common.freq<freq[i].freq){
            most_common = freq[i];
        }
    }

    c = most_common.symb;

    return c;
}

void main(int argc, char *argv[]){
    if (argc < 2) {
        printf("Please provide a file name.\n");
        return;
    }

    FILE *input_file;
    input_file = fopen(argv[1], "r");
    if (input_file == NULL) {
        printf("Error opening file.\n");
        return;
    }
    char rsp = most_frequent_symbol(input_file);
    if(rsp == '\n'){
        printf("Most common symbol is 'ENTER'",rsp); 
    }
    else {
        printf("Most common symbol is '%c'",rsp);
    }
    
    fclose(input_file);
}