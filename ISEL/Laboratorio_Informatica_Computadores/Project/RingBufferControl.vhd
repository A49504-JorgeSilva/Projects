library IEEE;
use IEEE.std_logic_1164.all;

entity RingBufferControl is
port( DAV,clk,reset, CTS, full, empty : IN STD_LOGIC;
		Wreg, Wr, selPnG, DAC, incPut, incGet : OUT STD_LOGIC
);
end RingBufferControl;

architecture arq_RingBufferControl of RingBufferControl is

type STATE_TYPE is (WAITING, WAIT_FOR_READING, READING, ADDRESSING, WRITING, INCRPUT, INCRGET, COMPLETED);

signal CURRENT_STATE, NEXT_STATE : STATE_TYPE;

begin

--		Registo Current State

CURRENT_STATE <= WAITING when (reset = '1') else NEXT_STATE when rising_edge(clk);

--		Máquina de Estados

GenerateNextState:
process (CURRENT_STATE, DAV, CTS, full, empty)
	begin
		case CURRENT_STATE is
			when WAITING			=> if(DAV = '0' and CTS = '1' and empty = '0') then
												NEXT_STATE 		<= READING;
											elsif(DAV = '1' and full = '1') then
												NEXT_STATE 		<= WAIT_FOR_READING;
											elsif(DAV = '1' and full = '0') then
												NEXT_STATE 		<= ADDRESSING;
											else
												NEXT_STATE 		<= WAITING;
											end if;																																																				
			when ADDRESSING   	=> NEXT_STATE	 		<= WRITING;
									
		   when WRITING	      => NEXT_STATE	 		<= INCRPUT;
			when INCRPUT			   => NEXT_STATE	 		<= COMPLETED;

			when WAIT_FOR_READING=>if(CTS = '1') then
												NEXT_STATE 		<= READING;
											else
												NEXT_STATE 		<= WAIT_FOR_READING;
											end if;

			when READING			=>if(CTS = '0') then
												NEXT_STATE 		<= INCRGET;
											else
												NEXT_STATE 		<= READING;
											end if;	
			when INCRGET			=>--if(DAV = '1') then
											--	NEXT_STATE 		<= ADDRESSING;
											--else
												NEXT_STATE 		<= WAITING;
											--end if;																			
			when COMPLETED	=> if(DAV = '0') then
												NEXT_STATE 		<= WAITING;
											else
												NEXT_STATE 		<= COMPLETED;
											end if;

					end case;
end process;

--		Outputs

 
incGet   <= '1' when (CURRENT_STATE = INCRGET) else '0';
selPnG<= '1' when (CURRENT_STATE = ADDRESSING  OR CURRENT_STATE = WRITING )   else '0';
Wreg	<= '1' when (CURRENT_STATE = READING)   else '0';
Wr <= '1' when (CURRENT_STATE = WRITING)   else '0';
incPut <= '1' when (CURRENT_STATE = INCRPUT ) else '0';
DAC <= '1' when (CURRENT_STATE = COMPLETED) else '0';


end arq_RingBufferControl;