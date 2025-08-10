LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY SLCDC_A IS
	PORT( MCLK,reset : IN STD_LOGIC;
			NOT_SS, SCLK, SDX: IN STD_LOGIC; --software
			WrL : OUT STD_LOGIC;
			Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
	);
END SLCDC_A;


ARCHITECTURE arq_SLCDC_A OF SLCDC_A IS


component COUNTER
	PORT(  CLK : in std_logic;
		E : in std_logic;
		clr: in std_logic;
		R : out std_logic_vector (3 downto 0)
		);
end component;



component equalTo12Dispatcher 
port( D : IN STD_LOGIC_VECTOR(3 downto 0);
		F : OUT STD_LOGIC 
);
end component;



COMPONENT SerialReceiverSLCDC
	port(
		SDX, SCLK, SS, accept, MCLK, reset : in std_logic;
		DXval : out std_logic;
		data : out std_logic_vector (4 downto 0)
	);
END COMPONENT;

COMPONENT Dispatcher 
	port( Dval,clk,reset, Eq12 : IN STD_LOGIC;
			WrL, done, enable : OUT STD_LOGIC;
			Din : IN STD_LOGIC_VECTOR(4 downto 0);
			Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
	);
END COMPONENT;	

COMPONENT CLKDIV is
	generic(div: natural := 50000000);
	port ( clk_in: in std_logic;
			 clk_out: out std_logic);
END COMPONENT;

signal DXvalSignal, SCLKSignal, MCLKDivSignal, acceptDoneSignal, eq12Signal, WrLSignal,enableSignal : STD_LOGIC;
signal DataSignal : STD_LOGIC_VECTOR (4 downto 0);
signal counterSignal : STD_LOGIC_VECTOR (3 downto 0);

begin

WrL <= WrLSignal;

uCLKDIV : CLKDIV generic map(2)  port map( 
		 clk_in => MCLK,
		 clk_out => MCLKDivSignal
);

SerialReceiver: SerialReceiverSLCDC PORT MAP (
					SDX    => SDX,
					SCLK   => SCLK,
					SS 	 => NOT_SS,  
					accept => acceptDoneSignal,
					MCLK	 => MCLKDivSignal,
					reset	 => reset,
					DXval	 => DXvalSignal,
					data	 => DataSignal					
					); 
					
Dispat: Dispatcher PORT MAP (
					Dval  => DXvalSignal,
					clk	=> MCLKDivSignal,
					reset => reset,
					WrL	=> WrLSignal,
					done	=> acceptDoneSignal,
					Din	=> DataSignal, 
					Dout	=> Dout,
					Eq12 => eq12Signal,
					enable => enableSignal
					
					);					
					
					
UCOUNTER: COUNTER port map(
	Clk => MCLKDivSignal, 
	E => enableSignal, 
	clr => reset OR WrLSignal, 
	R => CounterSignal
);
uequalTo12Dispatcher : equalTo12Dispatcher port map (
					D=>counterSignal,
					F=>eq12Signal
);
								
					
end arq_SLCDC_A;					
					
					