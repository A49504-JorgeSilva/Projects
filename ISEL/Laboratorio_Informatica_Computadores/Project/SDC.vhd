LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY SDC IS
	PORT( MCLK,reset : IN STD_LOGIC;
			NOT_SS, SCLK, SDX, Sclose, Sopen, Psensor: IN STD_LOGIC; --software
			OnOff, busy : OUT STD_LOGIC;
			Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
	);
END SDC;


ARCHITECTURE arq_SDC OF SDC IS



COMPONENT SerialReceiver
	port(
		SDX, SCLK, SS, accept, MCLK, reset : in std_logic;
		DXval, busy : out std_logic;
		data : out std_logic_vector (4 downto 0)
	);
END COMPONENT;

COMPONENT DoorController 
	port( Dval,clk,reset, Sclose, Sopen, Psensor : IN STD_LOGIC;
	   OnOff, done, OpenClose : OUT STD_LOGIC;
		Din : IN STD_LOGIC_VECTOR(4 downto 0);
		Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
	);
END COMPONENT;	



signal DXvalSignal, SCLKSignal, acceptDoneSignal: STD_LOGIC;
signal DataSignal, DoutSignal : STD_LOGIC_VECTOR (4 downto 0);

begin



uSerialReceiver: SerialReceiver PORT MAP (
					SDX    => SDX,
					SCLK   => SCLK,
					SS 	 => NOT_SS,  
					accept => acceptDoneSignal,
					MCLK	 => MCLK,
					reset	 => reset,
					DXval	 => DXvalSignal,
					data	 => DataSignal,
					busy	 => busy
					); 
					
uDoorController: DoorController PORT MAP (
					Dval  => DXvalSignal,
					clk	=> MCLK,
					reset => reset,
					done	=> acceptDoneSignal,
					Din	=> DataSignal, 
					Dout	=> Dout,
					Sclose => Sclose,
					Sopen => Sopen,
					OnOff =>OnOff,
					Psensor => Psensor
					);
					
end arq_SDC;					
					
					