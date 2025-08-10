library IEEE;
use IEEE.std_logic_1164.all;

entity Keyboard_ReaderUSBPORT is
port( CLK, Reset : IN STD_LOGIC;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0)
		
) ;
end Keyboard_ReaderUSBPORT;

architecture arq_Keyboard_Reader of Keyboard_ReaderUSBPORT is

COMPONENT UsbPort IS
		PORT
		(
		inputPort:  IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
    	outputPort :  OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
		);
END COMPONENT;				


component Keyboard_Reader is
port( CLK, Reset, ACK : IN STD_LOGIC;
		Dval : OUT STD_LOGIC; 
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0);
		Q : out std_logic_vector(3 downto 0)

);
end component;

component CLKDIV is
generic(div: natural := 50000000);
port ( clk_in: in std_logic;
		 clk_out: out std_logic);
end component;
  

signal WregSignal, Clk_signal, OBfreeSignal,DAVSignal , DACSignal : STD_LOGIC;
signal codeSignal,QSignal : STD_LOGIC_VECTOR(3 downto 0);
signal UsbPortInputSignal, UsbPortOutputSignal : STD_LOGIC_VECTOR (7 downto 0);

begin


uUsbPort: UsbPort PORT MAP(
			inputPort 	=> UsbPortInputSignal,
			outputPort	=> UsbPortOutputSignal			
);



uKeyboardReader : Keyboard_Reader port map(
	CLK => CLK,
	Reset => reset,
	ACK => UsbPortOutputSignal(7), --ver no KBD
	Dval => UsbPortInputSignal(4), --ver no KBD
	KEYPAD_LIN => KEYPAD_LIN,
	KEYPAD_COL => KEYPAD_COL,
	Q => UsbPortInputSignal(3 downto 0) --ver no KBD
);


end arq_Keyboard_Reader;