library IEEE;
use IEEE.std_logic_1164.all;

entity DoorController is
port( Dval,clk,reset, Sclose, Sopen, Psensor : IN STD_LOGIC;
	   OnOff, done, OpenClose : OUT STD_LOGIC;
		Din : IN STD_LOGIC_VECTOR(4 downto 0);
		Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
);
end DoorController;

architecture behavioral of DoorController is

	
type STATE_TYPE is (SYSTEM_OFF,WAITING, OPENING, CLOSING, COMPLETED);


signal CURRENT_STATE, NEXT_STATE : STATE_TYPE;

begin

--		Registo Current State

CURRENT_STATE <= SYSTEM_OFF when (reset = '1') else NEXT_STATE when rising_edge(clk);

--		Máquina de Estados

GenerateNextState:
process (CURRENT_STATE, Dval, Sclose, Sopen, Psensor, Din)
	begin
		case CURRENT_STATE is
			when SYSTEM_OFF			=> if(Dval = '1' and Din(0) = '1') then
												NEXT_STATE 		<= OPENING;
											elsif(Dval = '1' and Din(0) = '0') then
													NEXT_STATE 		<= WAITING;											else
												NEXT_STATE 		<= SYSTEM_OFF;
											end if;																																																				
			when WAITING			=> if(Psensor = '0') then
													NEXT_STATE 		<= CLOSING;
											else
													NEXT_STATE 		<= WAITING;
											end if;
			when OPENING			=> if(Sopen = '0') then
													NEXT_STATE 		<= OPENING;
											elsif(Sopen = '1' and Din(0) = '1') then
												NEXT_STATE 		<= COMPLETED;
											elsif(Sopen = '1' and Din(0) = '0') then
													NEXT_STATE 		<= SYSTEM_OFF;
											end if;
											
			when CLOSING			=> if(Psensor = '1') then
												NEXT_STATE 		<= OPENING;
											elsif(Sclose = '1') then
													NEXT_STATE 		<= COMPLETED;
											else
												NEXT_STATE 		<= CLOSING;
											end if;
																
										
			when COMPLETED			=> if(Dval = '0') then
												NEXT_STATE 		<= SYSTEM_OFF;
											else
												NEXT_STATE 		<= COMPLETED;
											end if;
												
		end case;

end process;

--		Outputs
OnOff <= '1' when (CURRENT_STATE = OPENING OR CURRENT_STATE = CLOSING) else '0';  
OpenClose <= '1' when (CURRENT_STATE = OPENING) else '0';  
done <= '1' when (CURRENT_STATE = COMPLETED) else '0';  
Dout <= Din;

end behavioral;