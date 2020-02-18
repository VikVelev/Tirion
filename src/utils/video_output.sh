#!/bin/bash

[[ ! type "$ffmpeg" > /dev/null ]] && echo "FFMPEG Not installed" && exit;

# $1 is png pattern to be matched in the folders
# $2 is 'project' filename (e.g. SCpX)
# $3 is output directory
png_path=$1
file_name=$2
directory=$3
interpolate=$4

# Create a new folder to store every video, only if it doesn't exist already.
[[ -d dir ]] || mkdir $directory/Animated;

# Using h264 codec
# -r means framerate
ffmpeg -r 3 -i $png_path -c:v libx264 -pix_fmt yuv420p $directory/Animated/$file_name.mp4

# if the flag is set interpolate to 30 fps for better viewing experience.
if [ $interpolate -eq 1 ]; then;
    ffmpeg -i $directory/Animated/$file_name.mp4 -filter:v "minterpolate='fps=24'" $directory/Animated/$file_name.24fps.mp4 -threads $(nproc --all)
fi;
