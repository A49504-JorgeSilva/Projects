library IEEE;
use IEEE.std_logic_1164.all;

entity Ring_Buffer_TB is
end Ring_Buffer_TB;

architecture testbench of Ring_Buffer_TB is
    -- Component declaration for the Ring_Buffer
    component Ring_Buffer
        port (
            MCLK, reset : in STD_LOGIC;
            DAV, CTS : in STD_LOGIC;
            DAC, Wreg : out STD_LOGIC;
            Din : in STD_LOGIC_VECTOR(3 downto 0);
            Q : out STD_LOGIC_VECTOR(3 downto 0)
        );
    end component;

    -- Signal declarations
    signal MCLK_in, reset_in, DAV_in, CTS_in : STD_LOGIC;
    signal DAC_out, Wreg_out : STD_LOGIC;
    signal Din_in : STD_LOGIC_VECTOR(3 downto 0);
    signal Q_out : STD_LOGIC_VECTOR(3 downto 0);

begin
    -- Instantiate the Ring_Buffer
    UUT: Ring_Buffer port map (
        MCLK => MCLK_in,
        reset => reset_in,
        DAV => DAV_in,
        CTS => CTS_in,
        DAC => DAC_out,
        Wreg => Wreg_out,
        Din => Din_in,
        Q => Q_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        MCLK_in <= '0';
        reset_in <= '1';
        DAV_in <= '0';
        CTS_in <= '0';
        Din_in <= "0000";
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        MCLK_in <= '1';
        DAV_in <= '1';
        CTS_in <= '1';
        Din_in <= "1100";
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

   

end testbench;
