library IEEE;
use IEEE.std_logic_1164.all;

entity Dispatcher is
port( Dval,clk,reset, Eq12 : IN STD_LOGIC;
	   WrL, done, enable, clr: OUT STD_LOGIC;
		Din : IN STD_LOGIC_VECTOR(4 downto 0);
		Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
);
end Dispatcher;

architecture behavioral of Dispatcher is


	
	
type STATE_TYPE is (WAITING, COUNTING,COMPLETED);


signal CURRENT_STATE, NEXT_STATE : STATE_TYPE;

begin


	

--		Registo Current State

CURRENT_STATE <= WAITING when (reset = '1') else NEXT_STATE when rising_edge(clk);

--		Máquina de Estados

GenerateNextState:
process (CURRENT_STATE, Dval, Eq12)
	begin
		case CURRENT_STATE is
			when WAITING			=> if(Dval = '1') then
												NEXT_STATE 		<= COUNTING;
											else
												NEXT_STATE 		<= WAITING;
											end if;
																								
			when COUNTING			=> if(Eq12 = '1') then
												NEXT_STATE 		<= COMPLETED;
											else
												NEXT_STATE 		<= COUNTING;
											end if;																					
										
			when COMPLETED			=> if(Dval = '0') then
												NEXT_STATE 		<= WAITING;
											else 
												NEXT_STATE		<= COMPLETED;
											end if;	
			
		end case;

end process;

--		Outputs
WrL <= '1' when (CURRENT_STATE = COUNTING) else '0'; 
done <= '1' when (CURRENT_STATE = COMPLETED) else '0';  
clr <= '1' when (CURRENT_STATE = WAITING) else '0';  

Dout <= Din;

end behavioral;