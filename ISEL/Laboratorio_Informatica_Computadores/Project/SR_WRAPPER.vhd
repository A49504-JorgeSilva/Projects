LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY SR_WRAPPER IS
	PORT( 
	   accept, MCLK, reset : in std_logic;
		DXval : out std_logic;
		data : out std_logic_vector (4 downto 0)
);
END SR_WRAPPER;


ARCHITECTURE arq_SR_WRAPPER OF SR_WRAPPER IS

COMPONENT UsbPort
			PORT
				(
					inputPort:  IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
					outputPort :  OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
				);
END COMPONENT;


COMPONENT SerialReceiverSLCDC
	port(
		SDX, SCLK, SS, accept, MCLK, reset : in std_logic;
		DXval : out std_logic;
		data : out std_logic_vector (4 downto 0)
	);
END COMPONENT;


signal UsbPortInputSignal, UsbPortOutputSignal : STD_LOGIC_VECTOR (7 downto 0);
signal WrLSignal : STD_LOGIC;
signal DoutSignal : STD_LOGIC_VECTOR (4 downto 0);

begin

uUsbPort: UsbPort PORT MAP(
			inputPort 	=> UsbPortInputSignal,
			outputPort	=> UsbPortOutputSignal			
);


uSR_SLCDC: SerialReceiverSLCDC PORT MAP(
			MCLK	 	=>	MCLK,
			reset 	=> reset,
			accept	=> accept,
			SS			=> UsbPortOutputSignal(3),  --0x08
			SCLK		=> UsbPortOutputSignal(1),	 --0x02
			SDX		=> UsbPortOutputSignal(0),  --0x01
			data		=> data,
			DXval 	=> DXval
			
);

end arq_SR_WRAPPER;

