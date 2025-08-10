LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY KEY_CONTROL IS 
	PORT( 
		CLK : in std_logic;
		Kack : in std_logic;
		Kpress: in std_logic;
		Reset: in std_logic;
		Kval : out std_logic;
		Kscan : out std_logic
		);
END KEY_CONTROL;

architecture behavioral of KEY_CONTROL is

type STATE_TYPE is (STATE_NOT_WRITING, STATE_WRITING, STATE_WRITING_COMPLETE);

signal CurrentState, NextState : STATE_TYPE;

begin

-- Flip-Flop`s
CurrentState <= STATE_NOT_WRITING when Reset = '1' else NextState when rising_edge(CLK);

--Generate Next State
GenerateNextState:
process(CurrentState, Kack, Kpress)
	begin
		case CurrentState is 
		
			when STATE_NOT_WRITING		=> if (Kpress = '1') then NextState <= STATE_WRITING;
													else
														NextState <= STATE_NOT_WRITING;
													end if;
													
			when STATE_WRITING			=> if (Kack = '1') then NextState <= STATE_WRITING_COMPLETE;
													else
															NextState <= STATE_WRITING;
													end if;
			when STATE_WRITING_COMPLETE=> if (Kack = '0' and kpress = '0') then NextState <= STATE_NOT_WRITING;
                                       else
                                            NextState <= STATE_WRITING_COMPLETE;
                                       end if;		
			
		
	end case;
end process;

--Generate outputs

Kscan <= '1' when (CurrentState = STATE_NOT_WRITING) else '0';

Kval <= '1' when (CurrentState = STATE_WRITING) else '0';

end behavioral;
