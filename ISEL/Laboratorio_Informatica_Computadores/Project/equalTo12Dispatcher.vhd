library IEEE;
use IEEE.std_logic_1164.all;

entity equalTo12Dispatcher is
port( D : IN STD_LOGIC_VECTOR(3 downto 0);
		F : OUT STD_LOGIC 
);
end equalTo12Dispatcher;

architecture arq_equalTo12Dispatcher of equalTo12Dispatcher is

begin

F<= D(3) AND D(2) AND NOT D(1) AND  NOT D(0);

end arq_equalTo12Dispatcher;