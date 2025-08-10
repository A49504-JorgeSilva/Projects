library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity MUX4_1_TB is
end MUX4_1_TB;

architecture testbench of MUX4_1_TB is
    -- Component declaration for the multiplexer
    component MUX4_1
        Port (
            A, B, C, D: in STD_LOGIC;
            S: in STD_LOGIC_VECTOR(1 downto 0);
            Y: out STD_LOGIC
        );
    end component;

    -- Signal declarations
    signal A_in, B_in, C_in, D_in: STD_LOGIC;
    signal S_in: STD_LOGIC_VECTOR(1 downto 0);
    signal Y_out: STD_LOGIC;

begin
    -- Instantiate the multiplexer
    UUT: MUX4_1 port map (A => A_in, B => B_in, C => C_in, D => D_in, S => S_in, Y => Y_out);

    -- Stimulus process
    stimulus: process
    begin
        -- Test case 1: S = "00", A = '0', B = '1', C = '0', D = '0'
        S_in <= "00";
        A_in <= '0';
        B_in <= '1';
        C_in <= '0';
        D_in <= '0';
        wait for 10 ns;
        
        -- Test case 2: S = "01", A = '0', B = '0', C = '1', D = '0'
        S_in <= "01";
        A_in <= '0';
        B_in <= '0';
        C_in <= '1';
        D_in <= '0';
        wait for 10 ns;
        
        -- Test case 3: S = "10", A = '1', B = '0', C = '0', D = '0'
        S_in <= "10";
        A_in <= '1';
        B_in <= '0';
        C_in <= '0';
        D_in <= '0';
        wait for 10 ns;
        
        -- Test case 4: S = "11", A = '0', B = '0', C = '0', D = '1'
        S_in <= "11";
        A_in <= '0';
        B_in <= '0';
        C_in <= '0';
        D_in <= '1';
        wait for 10 ns;
        
        -- End the simulation
        wait;
    end process;

   
    
end testbench;
