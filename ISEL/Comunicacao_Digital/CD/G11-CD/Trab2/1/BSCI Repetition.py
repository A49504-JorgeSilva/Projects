import random as rand


def string_to_binary(sequence):
    binary_text = ''.join(format(ord(i), '08b') for i in sequence)
    return binary_text


def binary_to_string(sequence):
    string_data = ''.join(chr(int(sequence[i:i + 8], 2)) for i in range(0, len(sequence), 8))
    return string_data


def interleaving(sequence, columns):
    finalstring = ""
    rows = int((len(sequence) / columns) + 1)
    for i in range(columns):
        for j in range(rows):
            if i + j * columns >= len(sequence):
                break
            finalstring += sequence[i + j * columns]
    return finalstring


def deInterleaving(columns, sequence):
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
    return decoded


def BSC(p, sequence):
    final_sequence = ""
    for i in sequence:
        if rand.random() < p:
            final_sequence += '1' if i == '0' else '0'
        else:
            final_sequence += i
    return final_sequence


def repetition(p, sequence):
    final_sequence = ""
    for i in sequence:
        count1 = 0
        count0 = 0
        for j in range(3):
            if rand.random() < p:
                if i == '0':
                    count1 += 1
                else:
                    count0 += 1
            else:
                if i == '0':
                    count0 += 1
                else:
                    count1 += 1
        final_sequence += '1' if count1 > count0 else '0'
    return final_sequence


def berCalc(inBits, outBits):
    wrong = 0
    for i in range(len(inBits)):
        if inBits[i] != outBits[i]:
            wrong += 1
    return wrong / len(inBits)


def messageComp(inMessage, outMessage):
    wrong = 0
    for i in range(len(inMessage)):
        if inMessage[i] != outMessage[i]:
            wrong += 1
    return wrong


def binaryChannel(inputFile, ber, columns, outputFile):
    with open(inputFile, 'r', encoding='utf-8') as f:
        text = f.read()
    afterInt = interleaving(text, columns)
    input_string = string_to_binary(afterInt)
    print("BER usada = " + str(ber))
    print("Número total de bits que passam pelo BSC: " + str(input_string.__len__()))
    bsc = repetition(ber, input_string)
    print("BER depois do BSC = " + str(berCalc(input_string, bsc)))
    output_string = binary_to_string(bsc)
    print("Número de simbolos errados entre A e B: " + str(messageComp(afterInt, output_string)))
    deInt = deInterleaving(columns, output_string)
    print("Número de simbolos errados entre TX e RX: " + str(messageComp(text, deInt)))
    with open(outputFile, 'w', encoding='utf-8') as w:
        w.write(deInt)


BER = [0.1, 0.01, 0.001, 0.0001, 0.00001]
inFile = r"E:\Documentos\Universidade\CD\CD42D-G11\Trab1\TestFilesCD\alice29.txt"
outFile = [
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\Interleaving\Repetição\out1.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\Interleaving\Repetição\out2.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\Interleaving\Repetição\out3.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\Interleaving\Repetição\out4.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\Interleaving\Repetição\out5.txt"
]

for i in range(5):
    binaryChannel(inFile, BER[i], 4, outFile[i])
    print("--------------------------------------------------------")
