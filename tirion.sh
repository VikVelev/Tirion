#!/bin/bash
# Tirion framework... Made for Formula Student Team Delft.
# Interface script for now. TODO: Future to be changed with a fully functional Web UI.
# Default output is in the current folder

function cleanup {                                                                                                                                                   
   echo "[!] Tirion framework exiting..."
   exit 0
}

function finishedJob {
    echo "[*] Job finished."
    exit 0
}


simPath=$1;
cores=$2;

function main {
    echo "[-] Initializing Tirion framework...";
    
    sleep 1;

    trap cleanup 2;
    trap finishedJob 0;

    macroPath="/home/viktorv/Projects/FSTeamDelft/Tirion/src/main/PostProcessing.java";
    license_path="1999@flex.cd-adapco.com";
    power_on_demand_license="TtRoFR472Ew3lLUCvel7JQ";

    if [ ! -n "$cores" ]; then
        cores=$(( $(grep -c ^processor /proc/cpuinfo) / 2 ));
        echo "No amount of cores specified, using half of all the cores by default.";
        echo "You can specify the amount of cores by adding an additional parameter after the .sim file";
    fi
    
    echo "[*] Initialized."
    echo "[*] Loaded:"
    echo "    -- Simulation File: $simPath"
    echo "    -- MainMacro Script: $macroPath"
    echo "[%] Running STAR-CCM+ with $cores cores configured..."

    sleep 2

    starccm+ -rsh ssh -np $cores -podkey $power_on_demand_license -licpath $license_path -power $simPath -batch $macroPath;
}

main
