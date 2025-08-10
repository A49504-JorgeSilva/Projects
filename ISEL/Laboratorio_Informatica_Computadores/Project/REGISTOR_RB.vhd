LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY REGISTOR_RB IS 
	PORT( R : in std_logic_vector(2 downto 0);
		CLR : in std_logic;
		CL : in std_logic;
		E : in std_logic;
		TC : out std_logic;
		F : out std_logic_vector (2 downto 0)
		);
END REGISTOR_RB;


ARCHITECTURE arq_REGISTOR OF REGISTOR_RB IS

COMPONENT FFD
	PORT(	CLK : in std_logic;
		RESET : in STD_LOGIC;
		SET : in std_logic;
		D : IN STD_LOGIC;
		EN : IN STD_LOGIC;
		Q : out std_logic
		);
END COMPONENT;

SIGNAL Res: std_logic_vector(2 downto 0);


BEGIN

F <= Res;

TC <= not Res(0) and not Res(1) and not Res(2);

U0FFD : FFD port map(
	CLK => CL,
	RESET => CLR,
	SET => '0',
	D => R(0),
	EN => E,
	Q => Res(0)
	);

U1FFD : FFD port map(
	CLK => CL,
	RESET => CLR,
	SET => '0',
	D => R(1),
	EN => E,
	Q => Res(1)
	);

U2FFD : FFD port map(
	CLK => CL,
	RESET => CLR,
	SET => '0',
	D => R(2),
	EN => E,
	Q => Res(2)
	);


END arq_REGISTOR;