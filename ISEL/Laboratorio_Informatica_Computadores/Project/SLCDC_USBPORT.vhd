LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY SLCDC_USBPORT IS
	PORT( 
			
			MCLK,reset : IN STD_LOGIC;
			WrL , WrLED, SDX,SCLK : OUT STD_LOGIC;
			Dout : OUT STD_LOGIC_VECTOR(4 downto 0);
			LDout : OUT STD_LOGIC_VECTOR(4 downto 0)

			
	);
END SLCDC_USBPORT;




ARCHITECTURE arq_SLCDC_USBPORT OF SLCDC_USBPORT IS

COMPONENT UsbPort
			PORT
				(
					inputPort:  IN  STD_LOGIC_VECTOR(7 DOWNTO 0);
					outputPort :  OUT  STD_LOGIC_VECTOR(7 DOWNTO 0)
				);
END COMPONENT;

COMPONENT SLCDC
			PORT( MCLK,reset : IN STD_LOGIC;
					NOT_SS, SCLK, SDX: IN STD_LOGIC; --software
					WrL  : OUT STD_LOGIC;
					Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
			);
END COMPONENT;


COMPONENT CLKDIV is
	generic(div: natural := 50000000);
	port ( clk_in: in std_logic;
			 clk_out: out std_logic);
END COMPONENT;



signal UsbPortInputSignal, UsbPortOutputSignal : STD_LOGIC_VECTOR (7 downto 0);
signal MCLKDivSignal, WrLSignal: STD_LOGIC;
signal DoutSignal : STD_LOGIC_VECTOR (4 downto 0);

begin

--uCLKDIV : CLKDIV   port map(  
	--	 clk_in => MCLK,
		-- clk_out => MCLKDivSignal
--);


uUsbPort: UsbPort PORT MAP(
			inputPort 	=> UsbPortInputSignal,
			outputPort	=> UsbPortOutputSignal			
);


uSLCDC: SLCDC PORT MAP(
			MCLK	 	=>	MCLK,
			reset 	=> reset,
			NOT_SS	=> UsbPortOutputSignal(3), --0x08
			SCLK		=> UsbPortOutputSignal(1), --0x02
			SDX		=> UsbPortOutputSignal(0), --0x01
			WrL		=> WrLSignal,
			Dout		=> DoutSignal
			
);

SDX <= UsbPortOutputSignal(0); --0x01;
SCLK <= UsbPortOutputSignal(1); --0x02;
LDout <= DoutSignal;
Dout <= DoutSignal;
WrL <= WrLSignal;
WrLED <= WrLSignal;

end arq_SLCDC_USBPORT;