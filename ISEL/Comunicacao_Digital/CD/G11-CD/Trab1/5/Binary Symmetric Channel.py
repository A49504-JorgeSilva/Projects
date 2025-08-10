import random as rand


def binarySymmetricChannel(file, p):
    with open(file, 'r') as f:
        sequence = f.read()
    finalSequence = ""
    for i in sequence:
        if rand.random() < p:
            finalSequence += '1' if i == '0' else '0'
        else:
            finalSequence += i
    with open('output.txt', 'w') as out:
        out.write(finalSequence)


pathFile = r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\5\binFile.txt"

binarySymmetricChannel(pathFile, 0.2)
