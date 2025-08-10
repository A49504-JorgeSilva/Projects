import io
import unittest
from contextlib import redirect_stdout
from math import isclose

def GeometricProgression(N,u,r):
    for i in range(0,N):
        curr_Term = u * pow(r,i) 
        print("Termo",i,":", curr_Term)

def test_GeometricProgression(N, u, r, expected_output):
    with io.StringIO() as buf, redirect_stdout(buf):
        GeometricProgression(N, u, r)
        actual_output = buf.getvalue().strip()
        for i, term in enumerate(actual_output.split('\n')):
            expected_term = expected_output[i]
            assert isclose(float(term.split(':')[1]), expected_term, rel_tol=1e-9)

class TestGeometricProgression(unittest.TestCase):
    def test_output(self):
        expected_output = [3.0, 6.0, 12.0, 24.0, 48.0]
        test_input = (5, 3, 2)
        test_GeometricProgression(*test_input, expected_output)

GeometricProgression(5,3,2)