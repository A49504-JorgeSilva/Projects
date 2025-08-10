library IEEE;
use IEEE.std_logic_1164.all;

entity MAC_TB is
end MAC_TB;

architecture testbench of MAC_TB is
    -- Component declaration for the MAC
    component MAC
        port (
            MCLK, reset : in STD_LOGIC;
            putnget, incPut, incGet : in STD_LOGIC;
            full, empty : out STD_LOGIC;
            Address : out STD_LOGIC_VECTOR(2 downto 0)
        );
    end component;

    -- Signal declarations
    signal MCLK_in, reset_in, putnget_in, incPut_in, incGet_in : STD_LOGIC;
    signal full_out, empty_out : STD_LOGIC;
    signal Address_out : STD_LOGIC_VECTOR(2 downto 0);

begin
    -- Instantiate the MAC
    UUT: MAC port map (
        MCLK => MCLK_in,
        reset => reset_in,
        putnget => putnget_in,
        incPut => incPut_in,
        incGet => incGet_in,
        full => full_out,
        empty => empty_out,
        Address => Address_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        MCLK_in <= '0';
        reset_in <= '1';
        putnget_in <= '0';
        incPut_in <= '0';
        incGet_in <= '0';
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        MCLK_in <= '1';
        wait for 10 ns;

        putnget_in <= '1';
        incPut_in <= '1';
        wait for 10 ns;

        incPut_in <= '0';
        incGet_in <= '1';
        wait for 10 ns;

        putnget_in <= '0';
        incPut_in <= '1';
        wait for 10 ns;

        incPut_in <= '0';
        incGet_in <= '0';
        wait for 10 ns;

        putnget_in <= '1';
        incGet_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

   

end testbench;
