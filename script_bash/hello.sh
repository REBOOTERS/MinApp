#!/bin/bash

echo hello world

get_special_line() {
  line1=$(grep LifeCycle_PanelActivity "$1" | head -3 )
  echo "$line1"
  line2=$(grep LifeCycle_PanelActivity "$1" | head -3 | awk '{ print $1,$2}')
  echo "$line2"
#  line3=$(grep LifeCycle_PanelActivity "$1" | head -2 | awk '{ print $3,$4,$5,$6,$7,$8,$9,$10}')
#  echo "$line3"
}

#echo "$every_line"

for_each_dir() {
  for file in "$1"/*.txt;
  do
    echo "$file"
    get_special_line "$file"
  done
}

for_each_dir "logs/"

for num in {1..10}
do
  echo "$num"
done