LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY KEY_SCAN IS 
	PORT( Kscan: in std_logic;
		Clr: in std_logic;
		Clk: in std_logic;
		Kpress : out std_logic;
		KEYPAD_LIN : in std_logic_vector(3 downto 0);
		KEYPAD_CODE: out std_logic_vector(3 downto 0);
		KEYPAD_COL : out std_logic_vector(3 downto 0)
		
		);
END KEY_SCAN;

ARCHITECTURE arq_KEY_SCAN OF KEY_SCAN IS

component DECODER2_3
	PORT( S: in STD_LOGIC_VECTOR(1 downto 0);
			A,B,C,D:	 out STD_LOGIC
		);
end component;

component MUX4_1
	PORT( A,B,C,D: in STD_LOGIC;
			S: in STD_LOGIC_VECTOR(1 downto 0);
			Y:	 out STD_LOGIC
		);
end component;

component COUNTER
	PORT(  CLK : in std_logic;
		E : in std_logic;
		clr: in std_logic;
		R : out std_logic_vector (3 downto 0)
		);
end component;

Signal Coluna, Linha: Std_Logic_Vector (1 downto 0);

Signal A , B : Std_Logic;

BEGIN

Kpress <=  B;
A <= Kscan and (not B); -- 1 and not 0 => 1 , 1 and 0

KEYPAD_CODE(0) <= Linha(0);
KEYPAD_CODE(1) <= Linha(1);
KEYPAD_CODE(2) <= Coluna(0);
KEYPAD_CODE(3) <= Coluna(1);

UDECODER: DECODER2_3 port map( 
	S(0) => Coluna(0), 
	S(1) => Coluna(1), 
	A => KEYPAD_COL(0), 
	B => KEYPAD_COL(1), 
	C => KEYPAD_COL(2),
	D => KEYPAD_COL(3)
	);

UMUX4_1: MUX4_1 port map(
	S(0) => Linha(0), 
	S(1) => Linha(1), 
	A => KEYPAD_LIN(0), 
	B => KEYPAD_LIN(1), 
	C => KEYPAD_LIN(2), 
	D => KEYPAD_LIN(3), 
	Y => B);

UCOUNTER: COUNTER port map(
	Clk => Clk, 
	E => A, 
	clr => Clr, 
	R(0) => Linha(0), 
	R(1) => Linha(1), 
	R(2) => Coluna(0), 
	R(3) => Coluna(1));
	
end arq_KEY_SCAN;