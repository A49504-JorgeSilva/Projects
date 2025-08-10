library ieee;
use ieee.std_logic_1164.all;

entity KEY_CONTROL_TB is
end entity;

architecture sim of KEY_CONTROL_TB is

    signal CLK, Kack, Kpress, Reset, Kval, Kscan : std_logic;

    component KEY_CONTROL is
        PORT(
            CLK : in std_logic;
            Kack : in std_logic;
            Kpress : in std_logic;
            Reset : in std_logic;
            Kval : out std_logic;
            Kscan : out std_logic
        );
    end component;

begin

    UUT : KEY_CONTROL
    port map (
        CLK => CLK,
        Kack => Kack,
        Kpress => Kpress,
        Reset => Reset,
        Kval => Kval,
        Kscan => Kscan
    );

    CLK_GEN: process
    begin
        CLK <= '0';
        wait for 10 ns;
        CLK <= '1';
        wait for 10 ns;
    end process;

    STIMULI : process
    begin
        Reset <= '1';
        Kack <= '0';
        Kpress <= '0';
        wait for 10 ns;
        Reset <= '0';
        wait for 10 ns;
        Kpress <= '1';
        wait for 10 ns;
        Kack <= '1';
        wait for 10 ns;
        Kack <= '0';
        wait for 10 ns;
        Kpress <= '0';
        wait for 10 ns;
        wait;
    end process;

    MONITOR: process(CLK)
    begin
        if rising_edge(CLK) then
            report "Kscan = " & std_logic'image(Kscan) severity note;
            report "Kval = " & std_logic'image(Kval) severity note;
        end if;
    end process;

end architecture;