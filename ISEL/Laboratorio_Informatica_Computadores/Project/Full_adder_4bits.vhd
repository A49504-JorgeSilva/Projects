LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.all;

ENTITY FULL_ADDER_4BITS IS
PORT (
	A : in std_logic;
	B : in std_logic;
	CI : in std_logic;
	CO : out std_logic;
	R : out std_logic
	);
END FULL_ADDER_4BITS;

ARCHITECTURE Behavioral OF FULL_ADDER_4BITS IS 

BEGIN

R <= (A xor B) xor CI;

CO <= (A and B) or (CI and (A xor B));

END Behavioral;