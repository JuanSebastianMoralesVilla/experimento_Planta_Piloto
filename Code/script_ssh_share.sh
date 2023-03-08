#!/bin/bash
#Data input
source_folder='jars' #Route of the folder when is save the project
destination_folder='PDG_Experimento_PlantaPilito' #Route of the folder on the ssh server where the folder will be saved
# servers="*" #Servers where the file will be saved, set * to use all servers (an space between each of ones)
servers=("*")            #or set ("1" "2" "6" "12" .. "n") where the numbers are the server where the file will be saved
password="swarch"

#Compressing
echo "Compressing files.."
rm -rf "${source_folder}.zip" #Remove the old zip source_folder
zip -r "${source_folder}.zip" $source_folder #Compress the current source folder


#Setting the real servers
echo "Settings.."
declare -a real_servers
if [[ $servers = "*" ]]; then
    count=1
    count_servers=22
    while [ $count -le $count_servers ]; 
    do  
        real_servers[$count-1]="xhgrid${count}"
        ((count=count+1))
    done

    count=1
    while [ $count -le $count_servers ]; 
    do  
        real_servers[$count+21]="xhgrid${count}"
        ((count=count+1))
    done
else
    count=0
    for server in "${servers[@]}"; 
    do
        real_servers[$count]="xhgrid${server}"
        ((count=count+1))
    done
fi

#Sharing file
for server in "${real_servers[@]}"; do
    
    echo "creating folder in server ${server}"
    sshpass -p $password ssh swarch@${server} "rm -rf ${destination_folder} | mkdir -p ${destination_folder}" &
    
done

wait
echo ""
#Sharing file
for server in "${real_servers[@]}"; do
    
    echo "sharing file to server ${server}"
    sshpass -p $password scp "${source_folder}.zip" swarch@${server}:"/home/swarch/${destination_folder}" &
    
done

wait
echo ""
#Sharing file
for server in "${real_servers[@]}"; do
    
    echo "Uncompressing file on server ${server}"
    sshpass -p $password ssh swarch@${server} "cd ${destination_folder} && unzip -o ${source_folder}.zip" &
    
done

wait
echo ""
echo "==========================================="
echo "FINISHED"
echo "==========================================="