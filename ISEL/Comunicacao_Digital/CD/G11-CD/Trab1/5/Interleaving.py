def interleaving(inputFile, columns, outputFile):
    with open(inputFile, 'r') as f:
        text = f.read()
    finalstring = ""
    rows = int((len(text) / columns) + 1)
    for i in range(columns):
        for j in range(rows):
            if i + j * columns >= len(text):
                break
            finalstring += text[i + j * columns]
    with open(outputFile, "w") as o:
        o.write(finalstring)


input = r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\5\interleavingInput.txt"
output = r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\5\interleavingOutput.txt"
interleaving(input, 3, output)
