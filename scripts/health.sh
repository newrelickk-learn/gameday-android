#/bin/bash
if [ 1 -eq "$(adb devices | grep ssh | wc -l)" ];
then
  echo "working"
  exit 0;
else
  echo "error"
  exit 1;
fi