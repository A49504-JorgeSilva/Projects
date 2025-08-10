library IEEE;
use IEEE.std_logic_1164.all;

entity Dispatcher_TB is
end Dispatcher_TB;

architecture testbench of Dispatcher_TB is
    -- Component declarations
    component Dispatcher is
        port (
            Dval, clk, reset, Eq12 : in std_logic;
            WrL, done, enable, clr : out std_logic;
            Din : in std_logic_vector(4 downto 0);
            Dout : out std_logic_vector(4 downto 0)
        );
    end component;

    -- Signal declarations
    signal Dval_in, clk_in, reset_in, Eq12_in : std_logic;
    signal WrL_out, done_out, enable_out, clr_out : std_logic;
    signal Din_in, Dout_out : std_logic_vector(4 downto 0);

begin
    -- Instantiate the Dispatcher
    UUT: Dispatcher port map (
        Dval => Dval_in,
        clk => clk_in,
        reset => reset_in,
        Eq12 => Eq12_in,
        WrL => WrL_out,
        done => done_out,
        enable => enable_out,
        clr => clr_out,
        Din => Din_in,
        Dout => Dout_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        Dval_in <= '0';
        clk_in <= '0';
        reset_in <= '1';
        Eq12_in <= '0';
        Din_in <= "00000";
        wait for 10 ns;

        -- Release reset
        reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        Dval_in <= '1';
        clk_in <= '1';
        wait for 10 ns;

        -- Apply more stimuli
        Eq12_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;



end testbench;
