# Usage:   ./r <testNumber>
clear
dt=$(date)
echo $dt
cat Tests/$1.tc
java  tg37.tinyc.TC Tests/$1.tc
echo DONE
