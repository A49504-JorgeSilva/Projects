library IEEE;
use IEEE.std_logic_1164.all;

entity ShiftRegister_SR is
port( Sin, CLK, enable, RST: in STD_LOGIC;
		D : OUT STD_LOGIC_VECTOR(4 downto 0) 
);
end ShiftRegister_SR;

architecture arq_ShiftRegister_SR of ShiftRegister_SR is

component FFD

port(CLK : in std_logic;
		RESET : in STD_LOGIC;
		SET : in std_logic;
		D : IN STD_LOGIC;
		EN : IN STD_LOGIC;
		Q : out std_logic
);
end component;

signal DSignal: STD_LOGIC_VECTOR(4 downto 0);

begin

D(0)<=DSignal(4);
D(1)<=DSignal(3);
D(2)<=DSignal(2);
D(3)<=DSignal(1);
D(4)<=DSignal(0);

Uffd0 : FFD port map (
			CLK => CLK,
			RESET => RST,
			SET => '0',
			D => Sin,
			EN => enable,
			Q => DSignal(0)
);
				
Uffd1 : FFD port map (
			CLK => CLK,
			RESET => RST,
			SET => '0',
			D => DSignal(0),
			EN => enable,
			Q => DSignal(1)
);

Uffd2 : FFD port map (
			CLK => CLK,
			RESET => RST,
			SET => '0',
			D => DSignal(1),
			EN => enable,
			Q => DSignal(2)
);
				
Uffd3 : FFD port map (
			CLK => CLK,
			RESET => RST,
			SET => '0',
			D => DSignal(2),
			EN => enable,
			Q => DSignal(3)
);

Uffd4 : FFD port map (
			CLK => CLK,
			RESET => RST,
			SET => '0',
			D => DSignal(3),
			EN => enable,
			Q => DSignal(4)
);
				

end arq_ShiftRegister_SR;