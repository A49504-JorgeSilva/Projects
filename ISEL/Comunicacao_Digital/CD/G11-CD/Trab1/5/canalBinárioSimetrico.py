import random as rand


def string_to_binary(sequence):
    binary_text = ''.join(format(ord(i), '08b') for i in sequence)
    return binary_text


def binary_to_string(sequence):
    string_data = ''.join(chr(int(sequence[i:i + 8], 2)) for i in range(0, len(sequence), 8))
    return string_data


def binarySymmetricChannel(p, sequence):
    finalSequence = ""
    for i in sequence:
        if rand.random() < p:
            finalSequence += '1' if i == '0' else '0'
        else:
            finalSequence += i
    return finalSequence


def interleaving(inputFile, columns):
    with open(inputFile, 'r') as f:
        text = f.read()
    finalstring = ""
    rows = int((len(text) / columns) + 1)
    for i in range(columns):
        for j in range(rows):
            if i + j * columns >= len(text):
                break
            finalstring += text[i + j * columns]
    return finalstring


def deInterleaving(file, columns, sequence):
    decoded = ""
    rows = int((len(sequence) / columns) + 1)
    blankSpaces = (rows * columns) - len(sequence)
    for i in range(rows):
        start = 0
        while i + start <= len(sequence) - 1 and len(decoded) < len(sequence):
            decoded += sequence[i + start]
            if start < rows * (columns - blankSpaces):
                start += rows
            else:
                start += rows - 1
    with open(file, 'w') as o:
        o.write(decoded)


def binaryChannel(inputFile, BER, matrix, outputFile):
    inLeaving = interleaving(inputFile, matrix)
    print("Ponto A: " + inLeaving)
    stringToBinary = string_to_binary(inLeaving)
    bsc = binarySymmetricChannel(BER, stringToBinary)
    binaryToString = binary_to_string(bsc)
    print("Ponto B: " + binaryToString)
    deInterleaving(outputFile, matrix, binaryToString)


inFile = r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\5\interleavingInput.txt"
outFile = r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\5\deInterleavingOutput.txt"

binaryChannel(inFile, 0.15, 4, outFile)
