library IEEE;
use IEEE.std_logic_1164.all;

entity SerialControl_TB is
end SerialControl_TB;

architecture testbench of SerialControl_TB is
    -- Component declarations
    component SerialControl is
        port (
            SS, clk, accept, eq5, reset : in std_logic;
            clr, wr, DXval, busy : out std_logic
        );
    end component;

    -- Signal declarations
    signal SS_in, clk_in, accept_in, eq5_in, reset_in : std_logic;
    signal clr_out, wr_out, DXval_out, busy_out : std_logic;

begin
    -- Instantiate the SerialControl
    UUT: SerialControl port map (
        SS => SS_in,
        clk => clk_in,
        accept => accept_in,
        eq5 => eq5_in,
        reset => reset_in,
        clr => clr_out,
        wr => wr_out,
        DXval => DXval_out,
        busy => busy_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        SS_in <= '0';
        clk_in <= '0';
        accept_in <= '0';
        eq5_in <= '0';
        reset_in <= '1';
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        SS_in <= '1';
        clk_in <= '1';
        accept_in <= '1';
        eq5_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;


end testbench;
