library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity adder3bit is
    Port ( A : in  STD_LOGIC_VECTOR (2 downto 0);
           B : in  STD_LOGIC_VECTOR (2 downto 0);
           Cin : in  STD_LOGIC;
           S : out  STD_LOGIC_VECTOR (2 downto 0);
           Cout : out  STD_LOGIC);
end adder3bit;

architecture Structural of adder3bit is
component FA is
    Port ( A : in  STD_LOGIC;
           B : in  STD_LOGIC;
           Cin : in  STD_LOGIC;
           S : out  STD_LOGIC;
           Cout : out  STD_LOGIC);
end component;

	signal carry : std_logic_vector(2 downto 1);

begin

U0: FA port map(A => A(0), B => B(0), Cin => Cin, S => S(0), Cout => carry(1));

U1: FA port map(A => A(1), B => B(1), Cin => carry(1), S => S(1), Cout => carry(2));

U2: FA port map(A => A(2), B => B(2), Cin => carry(2), S => S(2), Cout => Cout);


end Structural;

