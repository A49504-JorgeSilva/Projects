library IEEE;
use IEEE.std_logic_1164.all;

entity Key_Decode is
port( Kack, CLK, Reset : IN STD_LOGIC;
		Kval : OUT STD_LOGIC;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_CODE: out std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0)
		
);
end;

architecture arq_KeyDecode of Key_Decode is

component KEY_SCAN
port( Kscan, Clk, Clr : IN STD_LOGIC;
		Kpress : OUT STD_LOGIC;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_CODE: out std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0)

		
);
end component;

COMPONENT CLKDIV is
	generic(div: natural := 250000);
	port ( clk_in: in std_logic;
			 clk_out: out std_logic);
END COMPONENT;


component KEY_CONTROL
port( Kack,Kpress,CLK,Reset : IN STD_LOGIC;
		Kval, Kscan : OUT STD_LOGIC
);
end component;




signal KscanSignal,KpressSignal, MCLKDivSignal : STD_LOGIC;

begin


uCLKDIV : CLKDIV   port map( 
	 clk_in => CLK,
		clk_out => MCLKDivSignal
);


uKeyScan : KEY_SCAN port map(
					Kscan => KscanSignal,
					Kpress => KpressSignal,
					Clr => Reset,
					Clk => MCLKDivSignal,
					KEYPAD_LIN => KEYPAD_LIN, 
					KEYPAD_CODE => KEYPAD_CODE,
					KEYPAD_COL =>	KEYPAD_COL 

);

uKeyControL : KEY_CONTROL port map(
					Reset => Reset,
					Kack => Kack,
					Kpress => KpressSignal,
					Clk => MCLKDivSignal,
					Kval => Kval,
					Kscan => KscanSignal
);


end arq_KeyDecode;