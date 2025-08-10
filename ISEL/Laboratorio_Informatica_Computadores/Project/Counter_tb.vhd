LIBRARY IEEE;
USE IEEE.std_logic_1164.all;

ENTITY COUNTER_TB IS 
END COUNTER_TB;

ARCHITECTURE TESTBENCH OF COUNTER_TB IS

  -- Component declaration
  COMPONENT COUNTER
  PORT(
    CLK : in std_logic;
    E : in std_logic;
    CLR: in std_logic;
    T6 : out std_logic;
    R : out std_logic_vector (3 downto 0) :=(others => '0')
  );
  END COMPONENT;

  -- Signal declarations
  SIGNAL CLK : std_logic := '0';
  SIGNAL E : std_logic := '0';
  SIGNAL CLR : std_logic := '1';
  SIGNAL T6 : std_logic;
  SIGNAL R : std_logic_vector (3 downto 0);

BEGIN

  -- Instantiate the DUT (device under test)
  UUT: COUNTER PORT MAP(
    CLK => CLK,
    E => E,
    CLR => CLR,
    T6 => T6,
    R => R
  );

  -- Clock generator process
  CLK_PROCESS: PROCESS
  BEGIN
    CLK <= '0';
    WAIT FOR 5 NS;
    CLK <= '1';
    WAIT FOR 5 NS;
  END PROCESS CLK_PROCESS;

  -- Stimulus process
  STIMULUS_PROCESS: PROCESS
  BEGIN
    -- Reset the counter
    CLR <= '1';
    E <= '0';
    WAIT FOR 10 NS;
    
    -- Clear the reset
    CLR <= '0';
    WAIT FOR 10 NS;
    
    -- Enable the counter and check the output
    E <= '1';
    WAIT FOR 10 NS;
    ASSERT R = "0001" REPORT "Unexpected counter value";
    
    -- Check that the counter increments on each clock cycle
    WAIT FOR 5 NS;
    ASSERT R = "0010" REPORT "Unexpected counter value";
    WAIT FOR 5 NS;
    ASSERT R = "0011" REPORT "Unexpected counter value";
    WAIT FOR 5 NS;
    ASSERT R = "0100" REPORT "Unexpected counter value";
    
    -- Disable the counter
    E <= '0';
    WAIT FOR 10 NS;
    ASSERT R = "0100" REPORT "Unexpected counter value";
    
    -- Re-enable the counter and check that it starts from where it left off
    E <= '1';
    WAIT FOR 5 NS;
    ASSERT R = "0101" REPORT "Unexpected counter value";
    WAIT FOR 5 NS;
    ASSERT R = "0110" REPORT "Unexpected counter value";
    WAIT FOR 5 NS;
    ASSERT R = "0111" REPORT "Unexpected counter value";
    
    -- Reset the counter again
    CLR <= '1';
    WAIT FOR 10 NS;
    
    -- Check that the counter is at its initial value
    ASSERT R = "0000" REPORT "Unexpected counter value";
    
    -- End the simulation
    WAIT;
  END PROCESS STIMULUS_PROCESS;

END TESTBENCH;