library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity register_D_R is
	 Generic (WIDTH : POSITIVE := 1);
    Port (CLK : in  STD_LOGIC;
			 RST : in STD_LOGIC;
          D : in  STD_LOGIC_VECTOR(WIDTH-1 downto 0);
          Q : out  STD_LOGIC_VECTOR(WIDTH-1 downto 0));
end register_D_R;

architecture Behavioral of register_D_R is

begin

	Q <= (others => '0') when RST = '1' else D when rising_edge(clk);

end Behavioral;
