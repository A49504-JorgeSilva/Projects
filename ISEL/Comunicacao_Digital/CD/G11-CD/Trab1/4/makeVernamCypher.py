
def makeVernamCypher(plainText, theKey):
    cypheredTxt = ""
    idx = 0
    for char in plainText:
        cypheredTxt += chr(ord(char) ^ ord(theKey[idx]))
        idx += 1
        if idx == len(theKey):
            idx = 0
    return cypheredTxt

plainText = "abcabcd"
cypherKey = "3333333"
cypherText = makeVernamCypher(plainText, cypherKey)
print(f"CypheredText = {cypherText}")
cypherText = makeVernamCypher(cypherText, cypherKey)
print(f"DecipheredText = {cypherText}")
