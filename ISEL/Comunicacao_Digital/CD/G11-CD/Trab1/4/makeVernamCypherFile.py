import collections
import math

import matplotlib.pyplot as plt


def checkSymbol(symbol):
    if symbol == "\n":
        return r"\n"
    else:
        return symbol


def FileInformation(FileName):
    with open(FileName, 'r') as f:
        counter = collections.Counter(f.read())

    # HISTOGRAM
    symbols = list(map(lambda k: checkSymbol(k), counter.keys()))

    plt.bar(symbols, list(counter.values()))
    plt.xlabel('Símbolos')
    plt.ylabel('Frequência')
    plt.title('Frequência de Símbolos')
    plt.show()

    total = sum(counter.values())
    probabilities = [count / total for count in counter.values()]
    selfInfo = list(map(lambda p: -math.log2(p), probabilities))

    # ENTROPIA
    entropy = 0
    for i in range(len(probabilities)):
        entropy += probabilities.__getitem__(i) * selfInfo.__getitem__(i)
    print("A entropia do ficheiro é: %.4f" % entropy)


def makeVernamCypher(plainText, theKey):
    cypheredTxt = ""
    idx = 0
    for char in plainText:
        cypheredTxt += chr(ord(char) ^ ord(theKey[idx]))
        idx += 1
        if idx == len(theKey):
            idx = 0
    return cypheredTxt


def makeVernamCypherFile(file, theKey, outFile):
    with open(file, 'r') as f:
        data = f.read()
    cypheredText = makeVernamCypher(data, theKey)
    with open(outFile, 'w') as w:
        w.write(''.join(s for s in cypheredText))


pathFile = r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\TestFilesCD\alice29.txt"
key = "3333333"
randomKey = "jhdsfbskdjsdg"

print("Cypher for constant key:")
makeVernamCypherFile(pathFile, key, 'cypher.txt')
FileInformation("cypher.txt")
makeVernamCypherFile("cypher.txt", key, 'decipher.txt')
FileInformation("decipher.txt")

print("\n-----------------")
print("Cypher for random key:")
makeVernamCypherFile(pathFile, randomKey, 'randomCypher.txt')
FileInformation("randomCypher.txt")
makeVernamCypherFile("randomCypher.txt", randomKey, 'randomDecipher.txt')
FileInformation("randomDecipher.txt")
