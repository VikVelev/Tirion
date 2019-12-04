#/usr/bin/python

import argparse
import subprocess
import time
import re
import os
import atexit
import signal

file_name = './temp/tirion.job.%s.sh'%(int(time.time()))

@atexit.register
def cleanup():
    print("Cleaning up...")
    os.remove(file_name)    
    exit(0)
    
#(iterations, !symmetry, types, output)
signal.signal(signal.SIGINT, cleanup)
signal.signal(signal.SIGTERM, cleanup)
signal.signal(signal.SIGQUIT, cleanup)

parser = argparse.ArgumentParser(prog='Tirion', description='A framework for STARCCM+ CFD Processing. Written for Formula Student Team Delft.')

parser.add_argument('--name', metavar="-j", type=str, help='Job name.', required=True)
parser.add_argument('--simulation', metavar="-s", type=str, help='Path to the .sim file to run processing on.', required=True)
parser.add_argument('--iterations', metavar="-i", type=int, help='Number of iterations to run the simulation for.')
parser.add_argument('--nodes', metavar='-n', type=int, help='Number of nodes to request the resource management system for.', required=True)
parser.add_argument('--cores', metavar='-c', type=int, help='Number of cores per node.', required=True)
parser.add_argument('--symmetry', type=bool, help='A flag to enable symmetry. [!stub(wip)]')
parser.add_argument('--output', metavar='-o', type=str, help='Ouptut directory')
parser.add_argument('--type', metavar='-t', type=str, help='Type of processing: meshing | simulation | post-processing. Default is all.[!stub(wip)]')

args = parser.parse_args()

#The idea is to parse these and change .sh file, which you can open
script = open('./tirion.sh','r')
line_list = script.readlines()
script.close()

# Change the arguments
for i, line in enumerate(line_list):
    # Parse for the macro directory, job name, etc
    if line[0:2] == "#&":
        line_list[i] = "#&DEBEL\n"
        
    # Parse for macroPath, Simulation directory
    if re.match("macroPath", line) is not None:
        pass
    
    if re.match("simPath", line) is not None:
        pass

file_to_write = open(file_name, 'w')
file_to_write.writelines(line_list)
file_to_write.close()

# _ = None
# call slurm to submit the job and print the id, does slurm actually need the file, or is it copied?
# subprocess.call("bash -c './tirion.sh %s %s %s %s %s'" % (args.simulation, _, _, _, _), shell=True)
