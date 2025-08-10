import collections
import os
import unittest


def SymbolFrequency(nome_arquivo):
    with open(nome_arquivo) as f:
        counter = collections.Counter(f.read())
    symbols = sorted(counter.items(), key=lambda x: x[1])
    if len(symbols) == 0:
        return None, None
    leastFrequency = symbols[0]
    mostFrequency = symbols[-1]
    return leastFrequency, mostFrequency


class TestSymbolFrequency(unittest.TestCase):
    def test_single_character_file(self):
        file_contents = "a"
        with open("test_file.txt", "w") as f:
            f.write(file_contents)
        least_freq, most_freq = SymbolFrequency("test_file.txt")
        self.assertEqual(least_freq[0], "a")
        self.assertEqual(least_freq[1], 1)
        self.assertEqual(most_freq[0], "a")
        self.assertEqual(most_freq[1], 1)
        os.remove('test_file.txt')

    def test_multiple_characters_file(self):
        file_contents = "hello world"
        with open("test_file.txt", "w") as f:
            f.write(file_contents)
        least_freq, most_freq = SymbolFrequency("test_file.txt")
        self.assertEqual(least_freq[0], "h")
        self.assertEqual(least_freq[1], 1)
        self.assertEqual(most_freq[0], "l")
        self.assertEqual(most_freq[1], 3)
        os.remove('test_file.txt')

    def test_empty_file(self):
        file_contents = ""
        with open("test_file.txt", "w") as f:
            f.write(file_contents)
        least_freq, most_freq = SymbolFrequency("test_file.txt")
        self.assertIsNone(least_freq)
        self.assertIsNone(most_freq)
        os.remove('test_file.txt')
