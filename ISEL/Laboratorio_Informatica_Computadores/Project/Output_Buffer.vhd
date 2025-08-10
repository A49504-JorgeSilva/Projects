LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY Output_Buffer IS
	PORT( MCLK,reset : IN STD_LOGIC;
			Load, ACK: IN STD_LOGIC;
			Dval, OBfree : OUT STD_LOGIC;
			Din : IN STD_LOGIC_VECTOR(3 downto 0);
			Q : OUT STD_LOGIC_VECTOR(3 downto 0)
	);
END Output_Buffer;


ARCHITECTURE arq_Output_Buffer OF Output_Buffer IS



COMPONENT OutputBufferControl is
port( clk,reset, Load, ACK : IN STD_LOGIC;
		Wreg, OBfree, Dval : OUT STD_LOGIC
);
END COMPONENT;	


COMPONENT REGISTOR IS 
	PORT( R : in std_logic_vector(3 downto 0);
		CLR : in std_logic;
		CL : in std_logic;
		E : in std_logic;
		TC : out std_logic;
		F : out std_logic_vector (3 downto 0)
		);
END COMPONENT;	
		
signal  WregSignal :STD_LOGIC;
		
begin 

UOutputBufferControl : OutputBufferControl port map(
	clk 		=> MCLK,
	reset 	=> reset,
	Load 		=> Load,
	ACK 		=> ACK,
	Wreg 		=> WregSignal,
	OBfree 	=> OBfree,
	Dval		=> Dval
);

UREGISTOR : REGISTOR port map(
		 R 	=> Din,
		CLR 	=> reset,
		CL 	=> MCLK,
		E 		=> WregSignal,
		F 		=> Q
);

end arq_Output_Buffer;