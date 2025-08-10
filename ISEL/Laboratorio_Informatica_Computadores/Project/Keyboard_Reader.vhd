library IEEE;
use IEEE.std_logic_1164.all;

entity Keyboard_Reader is
port( CLK, Reset, ACK : IN STD_LOGIC;
		Dval : OUT STD_LOGIC; 
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0);
		Q : out std_logic_vector(3 downto 0)

);
end Keyboard_Reader;

architecture arq_Keyboard_Reader of Keyboard_Reader is
	

component Ring_Buffer IS
	PORT( MCLK,reset : IN STD_LOGIC;
			DAV, CTS: IN STD_LOGIC;
			DAC, Wreg : OUT STD_LOGIC;
			Din : IN STD_LOGIC_VECTOR(3 downto 0);
			Q : OUT STD_LOGIC_VECTOR(3 downto 0)
  );
end component;


component Output_Buffer IS
	PORT( MCLK,reset : IN STD_LOGIC;
			Load, ACK: IN STD_LOGIC;
			Dval, OBfree : OUT STD_LOGIC;
			Din : IN STD_LOGIC_VECTOR(3 downto 0);
			Q : OUT STD_LOGIC_VECTOR(3 downto 0)
	);
end component;

component Key_Decode is
port( Kack,CLK,Reset : IN STD_LOGIC;
		Kval : OUT STD_LOGIC;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_CODE: out std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0)
		
);
end component;


  

signal WregSignal, Clk_signal, OBfreeSignal,DAVSignal , DACSignal : STD_LOGIC;
signal codeSignal,QSignal : STD_LOGIC_VECTOR(3 downto 0);

begin




uRing_Buffer : Ring_Buffer port map(
					MCLK => CLK,
					reset => reset,
					DAV => DAVSignal,
					CTS => OBfreeSignal,
					DAC => DACSignal,
					Wreg => WregSignal,
					Din => codeSignal,
					Q => QSignal
);


uOutput_Buffer : Output_Buffer port map(
					MCLK => CLK,
					reset => reset,
					Load => WregSignal,
					ACK => ACK, 
					Dval => Dval, 
					OBfree => OBfreeSignal,
					Din => QSignal,
					Q => Q
);

uKey_Decode : Key_Decode port map(
		Reset => reset,
		CLK => CLK,
		Kack => DACSignal,
		Kval => DAVSignal,
		KEYPAD_LIN => KEYPAD_LIN,
		KEYPAD_CODE => codeSignal,
		KEYPAD_COL	=> KEYPAD_COL
		
);


end arq_Keyboard_Reader;