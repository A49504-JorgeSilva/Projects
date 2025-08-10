import random as rand


def string_to_binary(sequence):
    binary_text = ''.join(format(ord(i), '08b') for i in sequence)
    return binary_text


def binary_to_string(sequence):
    string_data = ''.join(chr(int(sequence[i:i + 8], 2)) for i in range(0, len(sequence), 8))
    return string_data


def BSC(p, sequence):
    final_sequence = ""
    for i in sequence:
        if rand.random() < p:
            final_sequence += '1' if i == '0' else '0'
        else:
            final_sequence += i
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


def binaryChannel(inputFile, ber, outputFile):
    with open(inputFile, 'r', encoding='utf-8') as f:
        text = f.read()
    input_string = string_to_binary(text)
    print("BER usada = " + str(ber))
    print("Número total de bits que passam pelo BSC: " + str(input_string.__len__()))
    bsc = BSC(ber, input_string)
    print("BER depois do BSC = " + str(berCalc(input_string, bsc)))
    output_string = binary_to_string(bsc)
    print("Número de simbolos errados entre A e B: " + str(messageComp(text, output_string)))
    with open(outputFile, 'w', encoding='utf-8') as w:
        w.write(output_string)


BER = [0.1, 0.01, 0.001, 0.0001, 0.00001]
inFile = r"E:\Documentos\Universidade\CD\CD42D-G11\Trab1\TestFilesCD\alice29.txt"
outFile = [
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\No Control\out1.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\No Control\out2.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\No Control\out3.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\No Control\out4.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\No Control\out5.txt"
]

for i in range(5):
    binaryChannel(inFile, BER[i], outFile[i])
    print("--------------------------------------------------------")
