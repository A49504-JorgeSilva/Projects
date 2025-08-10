import random

import serial.tools.list_ports


def get_fletcher32(data: str):
    """
        Accepts a string as input.
        Returns the Fletcher32 checksum value in decimal format.
        16-bit implementation (32-bit checksum)
        """
    sum1 = 0
    sum2 = 0
    data = data.encode()
    for d in data:
        sum1 = (sum1 + d) & 0xFFFF
        sum2 = (sum2 + sum1) & 0xFFFF
    result = (sum2 << 16)
    result2 = result | sum1
    return hex(result2)


def createError(data):
    if random.random() < 0.40:  # 40% chance de dar erro
        binary = bin(int(data))[2:].zfill(32)
        error_bit = random.randint(0, 31)  # calcular qual o bit de inicio dos erros
        bits_list = list(binary)
        number_wrong_bits = random.randint(0, 31 - error_bit) # calcular o numero de bits que vão ser alterados
        if number_wrong_bits > 0:
            print("Número de bits errados: " + str(number_wrong_bits))
        while number_wrong_bits > 0:
            bits_list[error_bit] = '0' if bits_list[error_bit] == '1' else '1'
            error_bit += 1
            number_wrong_bits -= 1
        bits = ''.join(bits_list)
        bits = str(int(bits, 2))
        return get_fletcher32(bits)
    else:
        return get_fletcher32(data)


ports = serial.tools.list_ports.comports()  # recebe todos os ports disponíveis
arduino = serial.Serial()

portList = []

print("Available ports:")
for onePort in ports:
    portList.append(str(onePort))
    print(str(onePort))

chosenPort = ""
val = input("select Port: COM")

for x in range(0, len(portList)):
    if portList[x].startswith("COM" + str(val)):
        chosenPort = "COM" + str(val)
        print(portList[x])

arduino.baudrate = 9600
arduino.port = chosenPort
arduino.open()

while True:
    arduino.flush()
    command = input("CLICK ENTER TO START OR WRITE EXIT TO LEAVE: ")
    if command == 'EXIT':
        exit()
    for i in range(0, 5):
        print("--------------------------------------------------------------------------------")
        res = arduino.readline()
        res = str(res, 'utf-8')
        res = res.strip('\r\n').strip('.00')
        print("Valor recebido: " + res)
        checksum_recebido = get_fletcher32(res)
        print("Checksum do valor recebido: " + str(checksum_recebido))
        checksum_error = createError(res)
        print("Checksum do valor recebido com erro: " + str(checksum_error))
        if checksum_recebido == checksum_error:
            print("Não houve erro")
        else:
            print("Houve erro")
