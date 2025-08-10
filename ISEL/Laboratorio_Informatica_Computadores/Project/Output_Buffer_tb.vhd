library IEEE;
use IEEE.std_logic_1164.all;

entity Output_Buffer_TB is
end Output_Buffer_TB;

architecture testbench of Output_Buffer_TB is
    -- Component declaration for the Output_Buffer
    component Output_Buffer
        port (
            MCLK, reset : in STD_LOGIC;
            Load, ACK : in STD_LOGIC;
            Dval, OBfree : out STD_LOGIC;
            Din : in STD_LOGIC_VECTOR (3 downto 0);
            Q : out STD_LOGIC_VECTOR (3 downto 0)
        );
    end component;

    -- Signal declarations
    signal MCLK_in, reset_in, Load_in, ACK_in : STD_LOGIC;
    signal Dval_out, OBfree_out : STD_LOGIC;
    signal Din_in, Q_out : STD_LOGIC_VECTOR (3 downto 0);

begin
    -- Instantiate the Output_Buffer
    UUT: Output_Buffer port map (
        MCLK => MCLK_in,
        reset => reset_in,
        Load => Load_in,
        ACK => ACK_in,
        Dval => Dval_out,
        OBfree => OBfree_out,
        Din => Din_in,
        Q => Q_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        MCLK_in <= '0';
        reset_in <= '1';
        Load_in <= '0';
        ACK_in <= '0';
        Din_in <= (others => '0');
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        MCLK_in <= '1';
        Load_in <= '1';
        Din_in <= "1100";
        wait for 10 ns;

        -- Apply more stimuli
        MCLK_in <= '0';
        Load_in <= '0';
        ACK_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;

end testbench;
