import numpy as np


def makeFile(password,M,N=None):
    if not password:
        symbols = np.random.choice(list(M.keys()), size=N, p=list(M.values()))
        print(symbols)
        with open('symbols.txt', 'w') as w:
            w.write(''.join(str(s) for s in symbols))
    else:
        passwords = []
        for i in range(5):
            length = np.random.randint(8, 13)
            symbols = np.random.choice(list(M.keys()), size=length, p=list(M.values()))
            passwords.append(''.join(symbols))
        return passwords


z = {'a': 0.2, 'b': 0.05, 'c': 0.25, 'd': 0.15, 'e': 0.10, 'f': 0.05, 'g': 0.025,'h': 0.125,'i': 0.05}
makeFile(False, z, 12)
#ret = makeFile(True, z)
#print(f"Passwords: {ret}")
