#!/bin/bash
#STATUS: [BASE]

###### DO NOT CHANGE ANYTHING UNLESS APPROVED ######
# Status will be changed to [META-MODIFIED] in the temporary file
# This script is used as a base and is meta-modified before job submission

# Slurm arguments [to be parsed] ({?} to be replaces with actual values)
#SBATCH -J {?}
#SBATCH --nodes={?}
#SBATCH --ntasks-per-node={?}
#SBATCH -o {?}./%j.out
#SBATCH -x cn22

# Tirion framework... Made for Formula Student Team Delft.
# TODO: Future to be changed with a fully functional Web UI.
# Default output is in the current folder

power_on_demand_license="JFUOnAckN/148GnNZ+f2nQ";
export CDLMD_LICENSE_FILE="1999@flex.cd-adapco.com"
export nodelist=""

function cleanup {                                                                                                                                                   
   echo "[!] Tirion framework exiting..."
   exit 0
}

function finishedJob {
    echo "[*] Exited gracefully."
    rm $nodelist
    exit 0
}

trap cleanup 2;
trap finishedJob 0;

if [ -n "$SLURM_NTASKS" ]; then
    initSLURMenv
fi

# {?} will be replaced with actual values.
macroPath={?};
simPath={?};
cores="$SLURM_NTASKS";
starccm="starccm+"
# starccm="/opt/CD-adapco/STAR-CCM+11.04.012-R8/star/bin/starccm+"

[ $macroPath == {?} ] && echo "Not designed to be run like this." && exit 0
[ $simPath == {?} ] && echo "Not designed to be run like this." && exit 0

function initSLURMenv {
    # ... Load other modules here
    module load dc-star-ccm+/14.04.013
    export CDLMD_LICENSE_FILE="1999@172.40.11.246"
    nodelist="./temp/slurmhosts.$SLURM_JOB_ID.txt"
    echo "[-] Building a machine file to $nodelist."
    srun hostname -s &> $nodelist
}

function main {
    echo "[-] Initializing Tirion framework...";
    
    sleep 1;

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

    sleep 1

    if [ -z $SLURM_NTASKS ]; then 
        $starccm\
            -jvmargs '-server'\
            -rsh ssh\
            -np $cores\
            -podkey $power_on_demand_license\
            -licpath $CDLMD_LICENSE_FILE\
            -power $simPath\
            -hardwarebatch\
            -batch $macroPath
    else
        echo "[-] Running in a SLURM Environment Cluster..." 
        $starccm\
             -jvmargs '-server'\
             -tokensonly\
             -rsh rsh\
             -mpi intel\
             -np $cores\
             -machinefile $nodelist\
             -batch $macroPath\
             $simPath
    fi

    # WIP
    # convert_to_videos
}

# WIP
function convert_to_videos {
    currentDir=$(pwd);
    cd $(dirname $simPath);
    
    simName=$(basename $simPath);
    simName=${simName::-4};

    cd "PostProcessing#${simName}";

    for f in *; do
        if [ -d "$f" ]; then
            cd $f;
            bash ./src/utils/video_output.sh '$f_*';
            echo $f;
            cd ..;
        fi
    done
}

main