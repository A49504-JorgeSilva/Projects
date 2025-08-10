library IEEE;
use IEEE.std_logic_1164.all;

entity ShiftRegister_SR_TB is
end ShiftRegister_SR_TB;

architecture testbench of ShiftRegister_SR_TB is
    -- Component declarations
    component ShiftRegister_SR is
        port (
            Sin, CLK, enable, RST: in STD_LOGIC;
            D : out STD_LOGIC_VECTOR(4 downto 0)
        );
    end component;

    -- Signal declarations
    signal Sin_in, CLK_in, enable_in, RST_in : STD_LOGIC;
    signal D_out : STD_LOGIC_VECTOR(4 downto 0);

begin
    -- Instantiate the ShiftRegister_SR
    UUT: ShiftRegister_SR port map (
        Sin => Sin_in,
        CLK => CLK_in,
        enable => enable_in,
        RST => RST_in,
        D => D_out
    );

    -- Stimulus process
    stimulus: process
    begin
        -- Initialize signals
        Sin_in <= '0';
        CLK_in <= '0';
        enable_in <= '0';
        RST_in <= '1';
        wait for 10 ns;

        -- Release reset
        RST_in <= '0';
        wait for 10 ns;

        -- Apply stimuli
        Sin_in <= '1';
        enable_in <= '1';
        wait for 10 ns;

        -- Apply more stimuli
        Sin_in <= '0';
        CLK_in <= '1';
        wait for 10 ns;

        -- End the simulation
        wait;
    end process;


end testbench;
