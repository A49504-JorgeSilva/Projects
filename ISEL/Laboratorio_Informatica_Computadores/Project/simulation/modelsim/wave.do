onerror {resume}
quietly WaveActivateNextPane {} 0
add wave -noupdate /sdc/MCLK
add wave -noupdate /sdc/reset
add wave -noupdate /sdc/NOT_SS
add wave -noupdate /sdc/SCLK
add wave -noupdate /sdc/SDX
add wave -noupdate /sdc/Sclose
add wave -noupdate /sdc/Sopen
add wave -noupdate /sdc/Psensor
add wave -noupdate /sdc/OnOff
add wave -noupdate /sdc/busy
add wave -noupdate /sdc/Dout
add wave -noupdate /sdc/DXvalSignal
add wave -noupdate /sdc/SCLKSignal
add wave -noupdate /sdc/MCLKDivSignal
add wave -noupdate /sdc/acceptDoneSignal
add wave -noupdate /sdc/DataSignal
add wave -noupdate /sdc/DoutSignal
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/SS
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/clk
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/accept
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/eq5
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/reset
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/clr
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/wr
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/DXval
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/busy
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/CURRENT_STATE
add wave -noupdate /sdc/uSerialReceiver/uSerialControl/NEXT_STATE
add wave -noupdate /sdc/uDoorController/Dval
add wave -noupdate /sdc/uDoorController/clk
add wave -noupdate /sdc/uDoorController/reset
add wave -noupdate /sdc/uDoorController/Sclose
add wave -noupdate /sdc/uDoorController/Sopen
add wave -noupdate /sdc/uDoorController/Psensor
add wave -noupdate /sdc/uDoorController/OnOff
add wave -noupdate /sdc/uDoorController/done
add wave -noupdate /sdc/uDoorController/OpenClose
add wave -noupdate /sdc/uDoorController/Din
add wave -noupdate /sdc/uDoorController/Dout
add wave -noupdate /sdc/uDoorController/CURRENT_STATE
add wave -noupdate /sdc/uDoorController/NEXT_STATE
TreeUpdate [SetDefaultTree]
WaveRestoreCursors {{Cursor 1} {0 ps} 0}
quietly wave cursor active 0
configure wave -namecolwidth 247
configure wave -valuecolwidth 100
configure wave -justifyvalue left
configure wave -signalnamewidth 0
configure wave -snapdistance 10
configure wave -datasetprefix 0
configure wave -rowmargin 4
configure wave -childrowmargin 2
configure wave -gridoffset 0
configure wave -gridperiod 1
configure wave -griddelta 40
configure wave -timeline 0
configure wave -timelineunits ps
update
WaveRestoreZoom {24524 ps} {25447 ps}
