library IEEE;
use IEEE.std_logic_1164.all;

entity RingBufferControl_TB is
end RingBufferControl_TB;

architecture testbench of RingBufferControl_TB is
    -- Component declaration for the RingBufferControl
    component RingBufferControl
        port (
            DAV, clk, reset, CTS, full, empty: in STD_LOGIC;
            Wreg, Wr, selPnG, DAC, incPut, incGet: out STD_LOGIC
        );
    end component;

    -- Signal declarations
    signal DAV_in, clk_in, reset_in, CTS_in, full_in, empty_in: STD_LOGIC;
    signal Wreg_out, Wr_out, selPnG_out, DAC_out, incPut_out, incGet_out: STD_LOGIC;

begin
    -- Instantiate the RingBufferControl
    UUT: RingBufferControl port map (
        DAV => DAV_in, clk => clk_in, reset => reset_in, CTS => CTS_in, full => full_in, empty => empty_in,
        Wreg => Wreg_out, Wr => Wr_out, selPnG => selPnG_out, DAC => DAC_out, incPut => incPut_out, incGet => incGet_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Test case 1
        DAV_in <= '0';
        clk_in <= '0';
        reset_in <= '1';
        CTS_in <= '0';
        full_in <= '0';
        empty_in <= '0';
        wait for 10 ns;

        -- Test case 2
        DAV_in <= '0';
        clk_in <= '1';
        reset_in <= '0';
        CTS_in <= '1';
        full_in <= '0';
        empty_in <= '1';
        wait for 10 ns;

        -- Test case 3
        DAV_in <= '1';
        clk_in <= '1';
        reset_in <= '0';
        CTS_in <= '1';
        full_in <= '1';
        empty_in <= '0';
        wait for 10 ns;

        -- Test case 4
        DAV_in <= '1';
        clk_in <= '0';
        reset_in <= '0';
        CTS_in <= '0';
        full_in <= '0';
        empty_in <= '0';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

    
end testbench;
