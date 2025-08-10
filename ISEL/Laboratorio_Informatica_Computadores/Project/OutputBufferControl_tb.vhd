library IEEE;
use IEEE.std_logic_1164.all;

entity OutputBufferControl_TB is
end OutputBufferControl_TB;

architecture testbench of OutputBufferControl_TB is
    -- Component declaration for the OutputBufferControl
    component OutputBufferControl
        port (
            clk, reset, Load, ACK : in STD_LOGIC;
            Wreg, OBfree, Dval : out STD_LOGIC
        );
    end component;

    -- Signal declarations
    signal clk_in, reset_in, Load_in, ACK_in : STD_LOGIC;
    signal Wreg_out, OBfree_out, Dval_out : STD_LOGIC;

begin
    -- Instantiate the OutputBufferControl
    UUT: OutputBufferControl port map (
        clk => clk_in,
        reset => reset_in,
        Load => Load_in,
        ACK => ACK_in,
        Wreg => Wreg_out,
        OBfree => OBfree_out,
        Dval => Dval_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        clk_in <= '0';
        reset_in <= '1';
        Load_in <= '0';
        ACK_in <= '0';
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        clk_in <= '1';
        Load_in <= '1';
        wait for 10 ns;

        -- Apply more stimuli
        clk_in <= '0';
        Load_in <= '0';
        ACK_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

  
end testbench;
