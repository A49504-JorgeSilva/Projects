LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY testbench_keyscan IS
END testbench_keyscan;

ARCHITECTURE testbench OF testbench_keyscan IS

  -- Component declaration
  COMPONENT KEY_SCAN
  PORT( 
      Kscan: in std_logic;
      Clr: in std_logic;
      Clk: in std_logic;
      Kpress : out std_logic;
      KEYPAD_LIN : in std_logic_vector(3 downto 0);
      KEYPAD_CODE: out std_logic_vector(3 downto 0);
      KEYPAD_COL : out std_logic_vector(3 downto 0)
  );
  END COMPONENT;
  
  -- Inputs
  SIGNAL clk : std_logic := '0';
  SIGNAL kscan : std_logic := '1';
  SIGNAL clr : std_logic := '1';
  SIGNAL KEYPAD_LIN : std_logic_vector(3 downto 0) := (others => '0');
  
  -- Outputs
  SIGNAL Kpress : std_logic;
  SIGNAL KEYPAD_CODE : std_logic_vector(3 downto 0);
  SIGNAL KEYPAD_COL : std_logic_vector(3 downto 0);

BEGIN

  -- Instantiate the Unit Under Test (UUT)
  UUT: KEY_SCAN PORT MAP (
      Kscan => kscan,
      Clr => clr,
      Clk => clk,
      Kpress => Kpress,
      KEYPAD_LIN => KEYPAD_LIN,
      KEYPAD_CODE => KEYPAD_CODE,
      KEYPAD_COL => KEYPAD_COL
  );
  
  -- Clock generation
  clk <= not clk after 10 ns;
  
  -- Stimulus process
  stim_proc: process
  begin
      -- Reset the counter
      clr <= '0';
      wait for 20 ns;
      clr <= '1';
      
      -- Press a key and check the output
      kscan <= '0';
      KEYPAD_LIN <= "0111";
      wait for 10 ns;
      assert Kpress = '1' and KEYPAD_CODE = "0111" and KEYPAD_COL = "0001" report "Error: Wrong output for pressed key 7" severity error;
      
      -- Release the key and check the output
      kscan <= '1';
      wait for 10 ns;
      assert Kpress = '0' and KEYPAD_CODE = (others => '0') and KEYPAD_COL = (others => '0') report "Error: Wrong output for released key 7" severity error;
      
      -- Press another key and check the output
      kscan <= '0';
      KEYPAD_LIN <= "1101";
      wait for 10 ns;
      assert Kpress = '1' and KEYPAD_CODE = "1101" and KEYPAD_COL = "0001" report "Error: Wrong output for pressed key D" severity error;
      
      -- Release the key and check the output
      kscan <= '1';
      wait for 10 ns;
      assert Kpress = '0' and KEYPAD_CODE = (others => '0') and KEYPAD_COL = (others => '0') report "Error: Wrong output for released key D" severity error;
      
      wait;
  end process;

END testbench;