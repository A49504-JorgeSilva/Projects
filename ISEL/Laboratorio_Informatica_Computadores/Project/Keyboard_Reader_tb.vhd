library IEEE;
use IEEE.std_logic_1164.all;

entity Keyboard_Reader_TB is
end Keyboard_Reader_TB;

architecture testbench of Keyboard_Reader_TB is
    -- Component declarations
    component Keyboard_Reader is
        port (
            CLK, Reset, ACK : in STD_LOGIC;
            KEYPAD_LIN : in STD_LOGIC_VECTOR (3 downto 0);
            KEYPAD_COL : out STD_LOGIC_VECTOR (3 downto 0);
            Dval, Kack, CTS, Kval : out STD_LOGIC;
            Dout : out STD_LOGIC_VECTOR (3 downto 0)
        );
    end component;

    component CLKDIV is
        generic (div : natural := 50000000);
        port (
            clk_in : in STD_LOGIC;
            clk_out : out STD_LOGIC
        );
    end component;

    -- Signal declarations
    signal CLK_in, Reset_in, ACK_in : STD_LOGIC;
    signal KEYPAD_LIN_in, KEYPAD_COL_out : STD_LOGIC_VECTOR (3 downto 0);
    signal Dval_out, Kack_out, CTS_out, Kval_out : STD_LOGIC;
    signal Dout_out : STD_LOGIC_VECTOR (3 downto 0);

begin
    -- Instantiate the Keyboard_Reader
    UUT: Keyboard_Reader port map (
        CLK => CLK_in,
        Reset => Reset_in,
        ACK => ACK_in,
        KEYPAD_LIN => KEYPAD_LIN_in,
        KEYPAD_COL => KEYPAD_COL_out,
        Dval => Dval_out,
        Kack => Kack_out,
        CTS => CTS_out,
        Kval => Kval_out,
        Dout => Dout_out
    );

    -- Instantiate the CLKDIV
    uCLKDIV: CLKDIV generic map (2) port map (
        clk_in => CLK_in,
        clk_out => CLK_in
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        CLK_in <= '0';
        Reset_in <= '1';
        ACK_in <= '0';
        KEYPAD_LIN_in <= (others => '0');
        wait for 10 ns;

        -- Release reset
        Reset_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        CLK_in <= '1';
        KEYPAD_LIN_in <= "1111";
        wait for 10 ns;

        -- Apply more stimuli
        CLK_in <= '0';
        KEYPAD_LIN_in <= "0000";
        ACK_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;



end testbench;
