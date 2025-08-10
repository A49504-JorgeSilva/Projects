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


def hamming_encode(message, k):
    out_message = ""
    temp_message = ""
    for i in range(len(message)):
        temp_message += message[i]
        if len(temp_message) == k:
            aux = temp_message + makeParityBits(temp_message)
            out_message += aux
            temp_message = ""
    if len(temp_message) != 0:
        out_message += temp_message
    return out_message


def makeParityBits(message):
    bits = ""
    bits += str(int(message[1]) ^ int(message[2]) ^ int(message[3]))
    bits += str(int(message[0]) ^ int(message[1]) ^ int(message[3]))
    bits += str(int(message[0]) ^ int(message[2]) ^ int(message[3]))
    return bits


def errorCheck(original, received):
    original_parity = ""
    received_parity = ""
    for i in range(3):
        original_parity += original[4 + i]
        received_parity += received[4 + i]
    aux = int(original_parity, 2) ^ int(received_parity, 2)
    return correction(received, aux)


def correction(message, case):
    out_message = list(message)
    if case == 1:
        out_message[6] = '1' if out_message[6] == '0' else '0'
    elif case == 2:
        out_message[5] = '1' if out_message[5] == '0' else '0'
    elif case == 3:
        out_message[0] = '1' if out_message[0] == '0' else '0'
    elif case == 4:
        out_message[4] = '1' if out_message[4] == '0' else '0'
    elif case == 5:
        out_message[2] = '1' if out_message[2] == '0' else '0'
    elif case == 6:
        out_message[1] = '1' if out_message[1] == '0' else '0'
    elif case == 7:
        out_message[3] = '1' if out_message[3] == '0' else '0'
    return ''.join(out_message)


def hamming_decode(original_message, received_message):
    out_message = ""
    temp_message_original = ""
    temp_message_received = ""
    for i in range(len(received_message)):
        temp_message_original += original_message[i]
        temp_message_received += received_message[i]
        if len(temp_message_received) == 7:
            out_message += checkSeven(temp_message_original, temp_message_received)
            temp_message_original = ""
            temp_message_received = ""
    if len(temp_message_received) != 0:
        out_message += temp_message_received
    return out_message


def checkSeven(original, received):
    out_message = ""
    aux = errorCheck(original, received)
    for i in range(4):
        out_message += aux[i]
    return out_message


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


def hamming(ber, input_string):
    encoded_message = hamming_encode(input_string, 4)
    received_message = BSC(ber, encoded_message)
    decoded_message = hamming_decode(encoded_message, received_message)
    return decoded_message


def binaryChannel(inputFile, ber, outputFile):
    with open(inputFile, 'r', encoding='utf-8') as f:
        text = f.read()
    input_string = string_to_binary(text)
    print("BER usada = " + str(ber))
    print("Número total de bits que passam pelo BSC: " + str(input_string.__len__()))
    bsc = hamming(ber, input_string)
    print("BER depois do BSC = " + str(berCalc(input_string, bsc)))
    output_string = binary_to_string(bsc)
    print("Número de simbolos errados entre A e B: " + str(messageComp(text, output_string)))
    with open(outputFile, 'w', encoding='utf-8') as w:
        w.write(output_string)


BER = [0.1, 0.01, 0.001, 0.0001, 0.00001]
inFile = r"E:\Documentos\Universidade\CD\CD42D-G11\Trab1\TestFilesCD\alice29.txt"
outFile = [
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\Hamming\out1.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\Hamming\out2.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\Hamming\out3.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\Hamming\out4.txt",
    r"E:\Documentos\Universidade\CD\CD42D-G11\Trab2\1\Alice Testing\NoInterleaving\Hamming\out5.txt"
]

for i in range(5):
    binaryChannel(inFile, BER[i], outFile[i])
    print("--------------------------------------------------------")
