library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity DECODER2_3 is 
	Port( 
			S: in STD_LOGIC_VECTOR(1 downto 0);
		A,B,C,D : out std_logic);
		end DECODER2_3;


architecture arq_DECODER of DECODER2_3 is

begin
	
		A <= not (not S(1) and not S(0));
		B <= not (not S(1) and S(0));
		C <= not (S(1) and not S(0));
		D <= not (S(1) and S(0));

	

	  
end arq_DECODER;