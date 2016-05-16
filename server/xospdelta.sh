#!/bin/bash

# Script to generate delta files for OpenDelta - by Jorrit 'Chainfire' Jongma
# Modified for XOSPDelta by out386
# Before using this script, download or build the zip and xdelta3 binaries.
# Aslo, build the zipadjuat binary (located at https://github.com/omnirom/android_packages_apps_OpenDelta)
# Put all three binaries in the PATH, or set the variables below


# Get device either from $DEVICE set by calling script, or first parameter

if [ "$DEVICE" == "" ]; then
	if [ "$1" != "" ]; then
		DEVICE=$1
	fi
fi

if [ "$DEVICE" == "" ]; then
	echo "Abort: no device set" >&2
	exit 1
fi

# ------ CONFIGURATION ------

HOME=/home/out386/build
BIN_ZIPADJUST=zipadjust
XDELTA=xdelta3
FILE_MATCH=XOSP*OFFICIAL*.zip
PATH_CURRENT=$HOME/in/target/product/$DEVICE
PATH_LAST=$HOME/delta/last/$DEVICE


# ------ PROCESS ------

getFileName() {
	echo ${1##*/}
}

getFileNameNoExt() {
	echo ${1%.*}
}

getFileMD5() {
	TEMP=$(md5sum -b $1)
	for T in $TEMP; do echo $T; break; done
}

getFileSize() {
	echo $(stat --print "%s" $1)
}

nextPowerOf2() {
    local v=$1;
    ((v -= 1));
    ((v |= $v >> 1));
    ((v |= $v >> 2));
    ((v |= $v >> 4));
    ((v |= $v >> 8));
    ((v |= $v >> 16));
    ((v += 1));
    echo $v;
}

FILE_CURRENT=$(getFileName $(ls -1 $PATH_CURRENT/$FILE_MATCH))
FILE_LAST=$(getFileName $(ls -1 $PATH_LAST/$FILE_MATCH))
FILE_LAST_BASE=$(getFileNameNoExt $FILE_LAST)

if [ "$FILE_CURRENT" == "" ]; then
	echo "Abort: CURRENT zip not found" >&2
	exit 1
fi

if [ "$FILE_LAST" == "" ]; then
	echo "Abort: LAST zip not found" >&2
	mkdir -p $PATH_LAST
	cp $PATH_CURRENT/$FILE_CURRENT $PATH_LAST/$FILE_CURRENT
	exit 0
fi

if [ "$FILE_LAST" == "$FILE_CURRENT" ]; then
	echo "Abort: CURRENT and LAST zip have the same name" >&2
	exit 1
fi

rm -rf work
mkdir work
rm -rf out
mkdir out
$BIN_ZIPADJUST --decompress $PATH_CURRENT/$FILE_CURRENT work/current.zip
$BIN_ZIPADJUST --decompress $PATH_LAST/$FILE_LAST work/last.zip
SRC_BUFF=$(nextPowerOf2 $(getFileSize work/current.zip));
$XDELTA -B ${SRC_BUFF} -9evfS none -s work/last.zip work/current.zip out/diff

MD5_CURRENT=$(getFileMD5 $PATH_CURRENT/$FILE_CURRENT)
MD5_CURRENT_STORE=$(getFileMD5 work/current.zip)
MD5_LAST=$(getFileMD5 $PATH_LAST/$FILE_LAST)
MD5_LAST_STORE=$(getFileMD5 work/last.zip)
MD5_UPDATE=$(getFileMD5 out/diff)

SIZE_CURRENT=$(getFileSize $PATH_CURRENT/$FILE_CURRENT)
SIZE_CURRENT_STORE=$(getFileSize work/current.zip)
SIZE_LAST=$(getFileSize $PATH_LAST/$FILE_LAST)
SIZE_LAST_STORE=$(getFileSize work/last.zip)
SIZE_UPDATE=$(getFileSize out/diff)

DELTA=out/deltaconfig

echo "{" > $DELTA
echo "  \"version\": 2.1," >> $DELTA
echo "  \"source\": \"$FILE_LAST\"," >> $DELTA
echo "  \"target\": \"$FILE_CURRENT\"," >> $DELTA
echo "  \"targetMd5\": \"$MD5_CURRENT_STORE\"," >> $DELTA
echo "  \"sourceMd5\": \"$MD5_LAST\"," >> $DELTA
echo "  \"sourceDecMd5\": \"$MD5_LAST_STORE\"," >> $DELTA
echo "  \"deltaMd5\": \"$MD5_UPDATE\"," >> $DELTA
echo "  \"targetSize\": \"$SIZE_CURRENT_STORE\"" >> $DELTA
echo "}" >> $DELTA

zip -0 -j out/delta.$FILE_LAST out/diff $DELTA

mkdir publish >/dev/null 2>/dev/null
mkdir publish/$DEVICE >/dev/null 2>/dev/null
cp out/$FILE_LAST publish/$DEVICE/.

rm -rf work
rm -rf out

rm -rf $PATH_LAST/*
mkdir -p $PATH_LAST
cp $PATH_CURRENT/$FILE_CURRENT $PATH_LAST/$FILE_CURRENT

exit 0
