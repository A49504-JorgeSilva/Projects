LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY COUNTER_3 IS 
	PORT( 
		CLK : in std_logic;
		E : in std_logic;
		CLR: in std_logic;
		R : out std_logic_vector (2 downto 0) :=(others => '0')
		);
END COUNTER_3;


ARCHITECTURE arq_COUNTER OF COUNTER_3 IS

	
	COMPONENT REGISTOR_RB IS 
	PORT( R : in std_logic_vector(2 downto 0);
		CLR : in std_logic;
		CL : in std_logic;
		E : in std_logic;
		TC : out std_logic;
		F : out std_logic_vector (2 downto 0)
		);
	END COMPONENT;


COMPONENT SOMADOR3
	PORT( A : in std_logic_vector(2 downto 0):=(others => '0');
			B : in std_logic_vector(2 downto 0);
			CI : in std_logic;
			R : out std_logic_vector (2 downto 0):=(others => '0')
			);
END COMPONENT;

SIGNAL  SR: std_logic_vector(2 downto 0):=(others => '0');
SIGNAL RS: std_logic_vector(2 downto 0):=(others => '0');

	

BEGIN
	
 
USOMADOR : SOMADOR3 port map (
	A => RS,
	B(0) => '1',
	B(1) => '0',
	B(2) => '0',
	CI => '0',
	R => SR
	);
	
	
UREGISTOR : REGISTOR_RB port map (
	R => SR,
	CL => CLK,
	E => E,
	F => RS,
	CLR => CLR
	);

R <= RS;	
END arq_COUNTER;