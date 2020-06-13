import random
f = open("input.txt","w+")
cloutlet = input("Enter Total Number of Cloudlets : ")
vm = input("Enter number of Virtual Machines : ")
f.write(cloutlet+"\n")
f.write(vm+"\n")
for i in range(int(cloutlet)):
	num = random.randint(1000,3000)
	f.write(str(num)+"\n")
f.close()