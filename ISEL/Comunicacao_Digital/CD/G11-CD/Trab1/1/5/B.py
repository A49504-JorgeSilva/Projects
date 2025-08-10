def mcd(a, b):
    c = a
    d = b
    if b > a:
        a, b = b, a
    while b != 0:
        r = a % b
        a, b = b, r
    print(f"O mcd({c},{d}) = {a}")
    return a


def test_mcd():
    assert mcd(10, 25) == 5
    assert mcd(16, 64) == 16
    assert mcd(14, 21) == 7
    assert mcd(36, 48) == 12


mcd(10, 25)
test_mcd()
