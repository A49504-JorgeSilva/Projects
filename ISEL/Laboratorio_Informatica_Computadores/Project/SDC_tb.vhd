library IEEE;
use IEEE.std_logic_1164.all;

entity SDC_TB is
end SDC_TB;

architecture testbench of SDC_TB is
    -- Component declarations
    component SDC is
        PORT(
            MCLK, reset : IN STD_LOGIC;
            NOT_SS, SCLK, SDX, Sclose, Sopen, Psensor : IN STD_LOGIC; --software
            OnOff, busy : OUT STD_LOGIC;
            Dout : OUT STD_LOGIC_VECTOR(4 downto 0)
        );
    end component;

    -- Signal declarations
    signal MCLK_in, reset_in, NOT_SS_in, SCLK_in, SDX_in, Sclose_in, Sopen_in, Psensor_in : std_logic;
    signal OnOff_out, busy_out : std_logic;
    signal Dout_out : std_logic_vector(4 downto 0);

begin
    -- Instantiate the SDC
    UUT: SDC port map (
        MCLK => MCLK_in,
        reset => reset_in,
        NOT_SS => NOT_SS_in,
        SCLK => SCLK_in,
        SDX => SDX_in,
        Sclose => Sclose_in,
        Sopen => Sopen_in,
        Psensor => Psensor_in,
        OnOff => OnOff_out,
        busy => busy_out,
        Dout => Dout_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        MCLK_in <= '0';
        reset_in <= '1';
        NOT_SS_in <= '1';
        SCLK_in <= '0';
        SDX_in <= '0';
        Sclose_in <= '0';
        Sopen_in <= '0';
        Psensor_in <= '0';
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        MCLK_in <= '1';
        NOT_SS_in <= '0';
        wait for 10 ns;

        SCLK_in <= '1';
        SDX_in <= '1';
        wait for 10 ns;

        Sclose_in <= '1';
        wait for 10 ns;

        Sopen_in <= '1';
        Psensor_in <= '1';
        wait for 10 ns;

        Sclose_in <= '0';
        wait for 10 ns;

        Psensor_in <= '0';
        wait for 10 ns;

        Sopen_in <= '0';
        SDX_in <= '0';
        NOT_SS_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

   
end testbench;
