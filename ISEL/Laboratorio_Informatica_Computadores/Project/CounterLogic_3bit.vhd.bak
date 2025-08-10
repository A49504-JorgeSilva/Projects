library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity CounterLogic_3bit is
    Port ( en : in  STD_LOGIC;
           ndecInc : in  STD_LOGIC;
           operandA : in  STD_LOGIC_VECTOR (2 downto 0);
           R : out  STD_LOGIC_VECTOR (2 downto 0));
end CounterLogic_3bit;

architecture Structural of CounterLogic_3bit is

	COMPONENT adder3bit
	PORT(
		A : IN std_logic_vector(2 downto 0);
		B : IN std_logic_vector(2 downto 0);
		Cin : IN std_logic;          
		S : OUT std_logic_vector(2 downto 0);
		Cout : OUT std_logic
		);
	END COMPONENT;
	
	COMPONENT MUX2_1
	PORT(
		I0 : IN std_logic_vector(2 downto 0);
		I1 : IN std_logic_vector(2 downto 0);
		sel : IN std_logic;          
		Y : OUT std_logic_vector(2 downto 0)
		);
	END COMPONENT;
	
	signal operandB : STD_LOGIC_VECTOR(2 downto 0) ;
	signal increment : STD_LOGIC_VECTOR(2 downto 0) ;
	
begin

	U2_adder3bit: adder3bit PORT MAP(
		A => operandA,
		B => operandB,
		Cin => '0',
		S => R,
		Cout => open
	);
	
	U0_MUX2_1: MUX2_1 PORT MAP(
		I0 => "111",
		I1 => "001",
		sel => ndecInc,
		Y => increment
	);
	
	U1_MUX2_1: MUX2_1 PORT MAP(
		I0 => "000",
		I1 => increment,
		sel => en,
		Y => operandB
	);

end Structural;
