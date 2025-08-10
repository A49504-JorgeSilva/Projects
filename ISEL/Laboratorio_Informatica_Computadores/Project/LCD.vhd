LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY LCD IS
	PORT( 
			Din : IN STD_LOGIC_VECTOR (3 downto 0);
			MCLK,reset, enable, RS : IN STD_LOGIC;
			Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
	);
END LCD;


ARCHITECTURE arq_LCD OF LCD IS



begin


Dout(0) <= RS; 
Dout(1) <= Din(0);
Dout(2) <= Din(1);
Dout(3) <= Din(2);
Dout(4) <= Din(3);

end arq_LCD;