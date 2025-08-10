library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity MUX2_1 is
    Port ( I0 : in  STD_LOGIC_VECTOR (3 downto 0);
           I1 : in  STD_LOGIC_VECTOR (3 downto 0);
           sel : in  STD_LOGIC;
           Y : out  STD_LOGIC_VECTOR (3 downto 0));
end MUX2_1;

architecture Behavioral of MUX2_1 is
	
begin

	Y <= I0 when sel='0' else I1;

end Behavioral;
