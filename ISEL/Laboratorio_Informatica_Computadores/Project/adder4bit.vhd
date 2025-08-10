library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity adder4bit is
    Port ( A : in  STD_LOGIC_VECTOR (3 downto 0);
           B : in  STD_LOGIC_VECTOR (3 downto 0);
           CI : in  STD_LOGIC;
           R : out  STD_LOGIC_VECTOR (3 downto 0);
           CO : out  STD_LOGIC);
end adder4bit;

architecture Structural of adder4bit is
component FULL_ADDER_4BITS is
    Port ( A : in  STD_LOGIC;
           B : in  STD_LOGIC;
           CI : in  STD_LOGIC;
           R : out  STD_LOGIC;
           CO : out  STD_LOGIC);
end component;

	Signal carry : Std_logic_vector(3 downto 1);

begin

U0: FULL_ADDER_4BITS port map(A => A(0), B => B(0), CI => CI, R => R(0), CO => carry(1));

U1: FULL_ADDER_4BITS port map(A => A(1), B => B(1), CI => carry(1), R => R(1), CO => carry(2));

U2: FULL_ADDER_4BITS port map(A => A(2), B => B(2), CI => carry(2), R => R(2), CO => carry(3));

U3: FULL_ADDER_4BITS port map(A => A(3), B => B(3), CI => carry(3), R => R(3), CO => CO);

end Structural;

