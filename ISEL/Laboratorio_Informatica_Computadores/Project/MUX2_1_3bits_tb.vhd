library IEEE;
use IEEE.std_logic_1164.all;

entity MUX2_1_3bits_TB is
end MUX2_1_3bits_TB;

architecture testbench of MUX2_1_3bits_TB is
    -- Component declaration for the MUX2_1_3bits
    component MUX2_1_3bits
        port (
            I0, I1 : in STD_LOGIC_VECTOR(2 downto 0);
            sel : in STD_LOGIC;
            Y : out STD_LOGIC_VECTOR(2 downto 0)
        );
    end component;

    -- Signal declarations
    signal I0_in, I1_in: STD_LOGIC_VECTOR(2 downto 0);
    signal sel_in: STD_LOGIC;
    signal Y_out: STD_LOGIC_VECTOR(2 downto 0);

begin
    -- Instantiate the MUX2_1_3bits
    UUT: MUX2_1_3bits port map (
        I0 => I0_in, I1 => I1_in, sel => sel_in, Y => Y_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        I0_in <= "000";
        I1_in <= "000";
        sel_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        I0_in <= "101";
        I1_in <= "010";
        sel_in <= '0';
        wait for 10 ns;

        sel_in <= '1';
        wait for 10 ns;

        I0_in <= "110";
        I1_in <= "001";
        sel_in <= '1';
        wait for 10 ns;

        sel_in <= '0';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

  

end testbench;
