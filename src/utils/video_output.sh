#!/bin/bash

# $1 is png pattern to be matched in the folders
# $2 is 'project' filename (e.g. SCpX)
# $3 is output directory
png_path=$1
fileName=$2

# Create a new folder to store every video, only if it doesn't exist already.
[[ -d dir ]] || mkdir $3/Animated;

# Using h264 codec
# -r means framerate
ffmpeg -r 3 -i $png_path -c:v libx264 -pix_fmt yuv420p $3/Animated/$2.mp4

# if the flag is set interpolate to 30 fps for better viewing experience.
if [ $4 true ]; then;
    ffmpeg -i $3/Animated/$2.mp4 -filter:v "minterpolate='fps=24'" $3/Animated/$2.24fps.mp4 -threads $(nproc --all)
fi;
