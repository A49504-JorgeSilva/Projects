library IEEE;
use IEEE.std_logic_1164.all;

entity Key_Decode_test is
port( Kscan,Clk,Clr : IN STD_LOGIC;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_CODE: out std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0);
		Kpress : OUT STD_LOGIC
	
);
end;

architecture arq_KeyDecode_test of Key_Decode_test is

component KEY_SCAN
port( Kscan, Clk, Clr : IN STD_LOGIC;
		Kpress : OUT STD_LOGIC;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_CODE: out std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0)

		
);
end component;

component CLKDIV is
generic(div: natural := 50000000);
port ( clk_in: in std_logic;
		 clk_out: out std_logic);
end component;
  

signal KpressSignal, KscanSignal, Clk_signal : STD_LOGIC;


begin

uCLKDIV : CLKDIV port map(
			clk_in => Clk,
			clk_out => Clk_signal
);

--KscanSignal = not Kpress;
--kpress <= KpressSignal;

uKeyScan : KEY_SCAN port map(
					Kscan => Kscan,
					Kpress => Kpress,
					Clr => Clr,
					Clk => Clk_signal,
					KEYPAD_LIN => KEYPAD_LIN, 
					KEYPAD_CODE => KEYPAD_CODE,
					KEYPAD_COL =>	KEYPAD_COL 

);

end arq_KeyDecode_test;