library IEEE;
use IEEE.std_logic_1164.all;

entity Key_Decode_tb is
end;

architecture test of Key_Decode_tb is
    component Key_Decode
    port(
        Kack, CLK, Reset : in std_logic;
        Kval : out std_logic;
        KEYPAD_LIN : in std_logic_vector(3 downto 0);
        KEYPAD_CODE : out std_logic_vector(3 downto 0);
        KEYPAD_COL : out std_logic_vector(3 downto 0)
    );
    end component;
    
    signal Kack_signal, CLK_signal, Reset_signal : std_logic;
    signal Kval_signal : std_logic;
    signal KEYPAD_LIN_signal : std_logic_vector(3 downto 0);
    signal KEYPAD_CODE_signal : std_logic_vector(3 downto 0);
    signal KEYPAD_COL_signal : std_logic_vector(3 downto 0);
    
    constant clock_period : time := 10 ns;
    
    begin
        uut: Key_Decode port map(
            Kack => Kack_signal,
            CLK => CLK_signal,
            Reset => Reset_signal,
            Kval => Kval_signal,
            KEYPAD_LIN => KEYPAD_LIN_signal,
            KEYPAD_CODE => KEYPAD_CODE_signal,
            KEYPAD_COL => KEYPAD_COL_signal
        );
        
        CLK_signal <= not CLK_signal after clock_period / 2;
        
        stimulus: process
        begin
            -- Reset the module
            Reset_signal <= '1';
            Kack_signal <= '0';
            KEYPAD_LIN_signal <= (others => '0');
            wait for 100 ns;
            
            -- Release the reset signal
            Reset_signal <= '0';
            wait for 10 ns;
            
            -- Press a key
            KEYPAD_LIN_signal <= "0111";
            wait for 10 ns;
            
            -- Check the output values
            assert Kack_signal = '1' report "Error: Kack is not '1'" severity error;
            assert Kval_signal = '1' report "Error: Kval is not '1'" severity error;
            assert KEYPAD_CODE_signal = "1001" report "Error: KEYPAD_CODE is not '1001'" severity error;
            assert KEYPAD_COL_signal = "0111" report "Error: KEYPAD_COL is not '0111'" severity error;
            
            -- Release the key
            KEYPAD_LIN_signal <= (others => '0');
            wait for 10 ns;
            
            -- Check the output values
            assert Kack_signal = '0' report "Error: Kack is not '0'" severity error;
            assert Kval_signal = '0' report "Error: Kval is not '0'" severity error;
            assert KEYPAD_CODE_signal = (others => 'Z') report "Error: KEYPAD_CODE is not 'Z'" severity error;
            assert KEYPAD_COL_signal = (others => 'Z') report "Error: KEYPAD_COL is not 'Z'" severity error;
            
            wait;
        end process;
    end;