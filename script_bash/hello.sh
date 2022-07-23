#!/bin/bash

echo hello world

get_special_line() {
  line1=$(grep LifeCycle_PanelActivity "$1" | head -1 )
  echo "$line1"
  line2=$(grep LifeCycle_PanelActivity "$1" | head -1 | awk '{ print $1,$2}')
  echo "$line2"
  line3=$(grep LifeCycle_PanelActivity "$1" | head -1 | awk '{ print $3,$4,$5,$6,$7}')
  echo "$line3"
}

#echo "$every_line"

for_each_dir() {
  for file in "$1"/*.txt;
  do
    echo "$file"
    get_special_line "$file"
  done

  # 17.1MB/1000000 TB/台 * 4100台 *1.137*108元/TB=8.6元

}

for_each_dir "logs/"