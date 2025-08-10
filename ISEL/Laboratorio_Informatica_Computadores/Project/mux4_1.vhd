library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity MUX4_1 is 
	Port( 
			A,B,C,D: in STD_LOGIC;
			S: in STD_LOGIC_VECTOR(1 downto 0);

			Y:	 out STD_LOGIC);
end MUX4_1;


architecture Behavioral of MUX4_1 is 

signal not_zero, not_one, not_two, not_three : std_logic;



begin

		Y <= not ((A AND NOT S(0) AND NOT S(1)) OR (B AND S(0) AND NOT S(1)) OR (C AND NOT S(0) AND S(1)) OR (D AND S(0) AND S(1)));
				
				
end Behavioral;				