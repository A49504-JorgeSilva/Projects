LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY MAC IS
	PORT( MCLK,reset : IN STD_LOGIC;
			putnget, incPut, incGet: IN STD_LOGIC; 
			full,empty : OUT STD_LOGIC;
			Address : OUT STD_LOGIC_VECTOR(2 downto 0)
	);
END MAC;


ARCHITECTURE arq_MAC OF MAC IS

component Counter_4bit is
    Port ( CLK : in  STD_LOGIC;
           RST : in  STD_LOGIC;
           ndecInc : in  STD_LOGIC;
           en : in  STD_LOGIC;
           Count : out  STD_LOGIC_VECTOR (3 downto 0));
end component;

component COUNTER_3 IS 
	PORT( 
		CLK : in std_logic;
		E : in std_logic;
		CLR: in std_logic;
		R : out std_logic_vector (2 downto 0) :=(others => '0')
		);
		
end component;


component MUX2_1_3bits is
    Port ( I0 : in  STD_LOGIC_VECTOR (2 downto 0);
           I1 : in  STD_LOGIC_VECTOR (2 downto 0);
           sel : in  STD_LOGIC;
           Y : out  STD_LOGIC_VECTOR (2 downto 0));
end component;


signal idxGetSignal, idxPutSignal : std_logic_vector(2 downto 0);
signal membersSignal : std_logic_vector(3 downto 0);
signal enableSignal :STD_LOGIC;

		
begin

enableSignal <= incPut OR incGet;

Ucounter_4 : Counter_4bit port map (
		CLK => MCLK,
		RST => reset,
		ndecInc =>  incPut, --0 dec 1 inc
		en => enableSignal,
		Count => membersSignal
);

Ucounter_3_putIdx : COUNTER_3 port map (
		CLK => MCLK,
		E => incPut,
		CLR => reset,
		R => idxPutSignal
);


Ucounter_3_getIdx : COUNTER_3 port map( 
		CLK => MCLK,
		E => incGet,
		CLR => reset,
		R => idxGetSignal	
);


UMux2_1 :  MUX2_1_3bits port map (
				I0 => idxGetSignal, 
				I1 => idxPutSignal,
				sel => putnget,
				Y => Address
);

full <= membersSignal(3);
empty <= not membersSignal(3) AND not membersSignal(2) AND not membersSignal(1) AND not membersSignal(0); 
 
end arq_MAC;




