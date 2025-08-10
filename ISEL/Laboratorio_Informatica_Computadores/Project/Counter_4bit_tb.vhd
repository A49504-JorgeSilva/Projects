library IEEE;
use IEEE.std_logic_1164.all;

entity Counter_4bit_TB is
end Counter_4bit_TB;

architecture testbench of Counter_4bit_TB is
    -- Component declaration for the Counter_4bit
    component Counter_4bit
        port (
            CLK, RST, ndecInc, en: in STD_LOGIC;
            Count: out STD_LOGIC_VECTOR(3 downto 0)
        );
    end component;

    -- Signal declarations
    signal CLK_in, RST_in, ndecInc_in, en_in: STD_LOGIC;
    signal Count_out: STD_LOGIC_VECTOR(3 downto 0);

begin
    -- Instantiate the Counter_4bit
    UUT: Counter_4bit port map (
        CLK => CLK_in, RST => RST_in, ndecInc => ndecInc_in, en => en_in, Count => Count_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        CLK_in <= '0';
        RST_in <= '1';
        ndecInc_in <= '0';
        en_in <= '1';
        wait for 10 ns;

        -- Deassert reset
        RST_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        ndecInc_in <= '1';
        wait for 10 ns;

        ndecInc_in <= '0';
        wait for 10 ns;

        ndecInc_in <= '1';
        wait for 10 ns;

        ndecInc_in <= '0';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;



end testbench;
