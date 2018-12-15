# apachebench. 
# ubuntu 18.04, 8GB.

# sudo -s
# ulimit -n 999999
# sysctl -w net.ipv4.tcp_syncookies=0
# sysctl -w net.ipv4.tcp_max_syn_backlog=20000
# sysctl -w sysctl net.ipv4.tcp_max_tw_buckets=32768

node="http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com:3000"
apache="http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com"
PITT="http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com:1111"

uri="/text.txt"

rm -rf benchlognew
mkdir benchlognew

addrs=($PITT $node $apache) # this is array
names=("PITT" "node" "apache")

timeout=1000

total_size=1
for((con=1000;con<=10000;con*=10));do # concurrency
  for((num=10;num<=100;num+=10));do # number of requests per client
    for((i=0;i<$total_size;i++));do
      addr=${addrs[i]}
      total_req=$(( $num * $con ))
      # echo $addr $uri ${names[i]} $total_req $con
      echo "ab -n $total_req -c $con -k -r -s $timeout $addr$uri > benchlognew/${namess[i]}-n${num}-c${con}"
      ab -n $total_req -c $con -k -r -s $timeout "$addr$uri" > benchlognew/"${names[i]}-n${num}-c${con} "
    done
  done
done

# ab -n 300000 -c 10000 -k -r -s 1000 http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com:1111/text.txt > \
# benchlog/PITT-n30-c10000 