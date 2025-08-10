library IEEE;
use IEEE.std_logic_1164.all;

entity equalTo5SerialReceiverSLCDC is
port( D : IN STD_LOGIC_VECTOR(2 downto 0);
		F : OUT STD_LOGIC 
);
end equalTo5SerialReceiverSLCDC;

architecture arq_equalTo5SerialReceiverSLCDC of equalTo5SerialReceiverSLCDC is

begin

F<= D(2) AND NOT D(1) AND D(0);

end arq_equalTo5SerialReceiverSLCDC;