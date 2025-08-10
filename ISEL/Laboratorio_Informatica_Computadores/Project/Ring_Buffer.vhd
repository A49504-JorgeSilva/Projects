LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY Ring_Buffer IS
	PORT( MCLK,reset : IN STD_LOGIC;
			DAV, CTS: IN STD_LOGIC;
			DAC, Wreg : OUT STD_LOGIC;
			Din : IN STD_LOGIC_VECTOR(3 downto 0);
			Q : OUT STD_LOGIC_VECTOR(3 downto 0)
	);
END Ring_Buffer;


ARCHITECTURE arq_Ring_Buffer OF Ring_Buffer IS

COMPONENT MAC IS
	PORT( MCLK,reset : IN STD_LOGIC;
			putnget, incPut, incGet: IN STD_LOGIC; 
			full,empty : OUT STD_LOGIC;
			Address : OUT STD_LOGIC_VECTOR(2 downto 0)
	);
END COMPONENT;

COMPONENT RingBufferControl is
port( DAV,clk,reset, CTS, full, empty : IN STD_LOGIC;
		Wreg, Wr, selPnG, DAC, incPut, incGet : OUT STD_LOGIC
);
END COMPONENT;	


COMPONENT RAM is
	generic(
		ADDRESS_WIDTH : natural := 3;
		DATA_WIDTH : natural := 4
	);
	port(
		address : in std_logic_vector(ADDRESS_WIDTH - 1 downto 0);
		wr: in std_logic;
		din: in std_logic_vector(DATA_WIDTH - 1 downto 0);
		dout: out std_logic_vector(DATA_WIDTH - 1 downto 0)
	);
end COMPONENT;



signal addressSignal : std_logic_vector(2 downto 0);
signal dataSignal : std_logic_vector(3 downto 0);
signal incGetSignal, incPutSignal, putngetSignal, fullSignal, emptySignal, wrSignal :STD_LOGIC;


begin 

UMAC : MAC port map (
		MCLK => MCLK,
		reset => reset,
		putnget => putngetSignal,
		incPut => incPutSignal,
		incGet =>  incGetSignal,
		full => fullSignal,
		empty => emptySignal,
		Address => addressSignal		
);



URingBufferControl : RingBufferControl port map(
		DAV => DAV,
		clk => MCLK,
		reset => reset,
		CTS => CTS,
		full => fullSignal,
		empty => emptySignal,
		Wreg => Wreg,
		Wr => wrSignal,
		selPnG => putngetSignal,
		incPut => incPutSignal,
		incGet => incGetSignal,
		DAC => DAC
);


URAM : RAM port map(
		address => addressSignal,
		wr => wrSignal,
		din => Din,
		dout => Q
);


end arq_Ring_Buffer;