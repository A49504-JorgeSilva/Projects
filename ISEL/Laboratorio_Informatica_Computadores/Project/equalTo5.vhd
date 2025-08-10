library IEEE;
use IEEE.std_logic_1164.all;

entity equalTo5 is
port( D : IN STD_LOGIC_VECTOR(2 downto 0);
		F : OUT STD_LOGIC 
);
end equalTo5;

architecture arq_equalTo5 of equalTo5 is

begin

F<= D(2) AND NOT D(1) AND D(0);

end arq_equalTo5;