library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity DECODER2_3_TB is
end DECODER2_3_TB;

architecture testbench of DECODER2_3_TB is
    -- Component declaration for the decoder
    component DECODER2_3
        Port (
            S: in STD_LOGIC_VECTOR(1 downto 0);
            A, B, C, D: out STD_LOGIC
        );
    end component;

    -- Signal declarations
    signal S_in: STD_LOGIC_VECTOR(1 downto 0);
    signal A_out, B_out, C_out, D_out: STD_LOGIC;

begin
    -- Instantiate the decoder
    UUT: DECODER2_3 port map (S => S_in, A => A_out, B => B_out, C => C_out, D => D_out);

    -- Stimulus process
    stimulus: process
    begin
        -- Test case 1: S = "00"
        S_in <= "00";
        wait for 10 ns;
        
        -- Test case 2: S = "01"
        S_in <= "01";
        wait for 10 ns;
        
        -- Test case 3: S = "10"
        S_in <= "10";
        wait for 10 ns;
        
        -- Test case 4: S = "11"
        S_in <= "11";
        wait for 10 ns;
        
        -- End the simulation
        wait;
    end process;

    
end testbench;
