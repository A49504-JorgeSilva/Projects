import serial.tools.list_ports


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
    command = input("CLICK ENTER TO START OR WRITE EXIT TO LEAVE")
    if command == 'EXIT':
        exit()
    for i in range(0, 5):
        res = arduino.readline()
        res = str(res, 'utf-8')
        res = res.strip('\r\n').strip('.00')
        print("Valor Recebido: " + res)
