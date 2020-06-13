package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Random; 
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class CloudSimExample6 {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	private static List<Vm> createVM(int userId, int vms) {

		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		long size = 10000; //image size (MB)
		int ram = 512; //vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[vms];

		for(int i=0;i<vms;i++){
			vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm,  new CloudletSchedulerSpaceShared());
			//for creating a VM with a space shared scheduling policy for cloudlets:
			//vm[i] = Vm(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

			list.add(vm[i]);
		}

		return list;
	}


	private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int[] cloudletlength){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		//cloudlet parameters
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			cloudlet[i] = new Cloudlet(i, cloudletlength[i], pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}


	    private static int getNum(ArrayList<Integer> v)  
    {  
        // Size of the vector  
        int n = v.size();  
      
        // Make sure the number is within  
        // the index range  
        int index = (int)(Math.random() * n);  
      
        // Get random number from the vector  
        int num = v.get(index);  
      
        // Remove the number from the vector  
        v.set(index, v.get(n - 1)); 
        v.remove(n - 1);  
      
        // Return the removed number  
        return num;  
    }  
      
    // Function to generate n  
    // non-repeating random numbers  
    private static ArrayList<Integer> generateRandom(int n)  
    {  
        ArrayList<Integer> v = new ArrayList<Integer>(n);  
      	ArrayList<Integer> ans = new ArrayList<Integer>(n);
        // Fill the vector with the values  
        // 1, 2, 3, ..., n  
        for (int i = 0; i < n; i++)  
            v.add(i + 1);  
      
        // While vector has elements  
        // get a random number from the vector and print it  
        while (v.size() > 0)  
        {  
            ans.add(getNum(v)-1);  
        }
        return ans;  
    }  


	private static ArrayList<Integer> createChromosome(int length,int range)
	{
		ArrayList<Integer> chromosome = new ArrayList<Integer>();
		ArrayList<Integer> part = new ArrayList<Integer>();
		Random rand = new Random();
		int c = 0;
		int remainder = length%range;
		int division = (length-remainder)/range;
		// System.out.println("Divison"+division+"remainder"+remainder);
		for(int i = 0;i<division;i++)
		{
			part = generateRandom(range);
			for(int j=0;j<part.size();j++)
			{
				chromosome.add(part.get(j));
				c = c+1;
			}
		}

		int temp;

		for(int i=c;i<length;i++)
		{
			temp = rand.nextInt(range);
			chromosome.add(temp);
		}
		return chromosome;
	}
	
	private static double calculateFitness(ArrayList<Integer> chromosome,int vmCount,int[] length,double filesize,int pe,int mips,int bw)
	{
		double fitness=0;
		double[] vm  = new double[vmCount];
		int vmId;
		double max;
		for(int i=0;i<chromosome.size();i++)
		{
			vmId = chromosome.get(i);
			fitness = (length[i]/1000.00)+(300.00/125000000);
			vm[vmId] = vm[vmId] + fitness; 
		}
		max = 0;
        for (int i = 0; i < vmCount; i++) 
        {
            if (vm[i] > max)
            {
                max = vm[i]; 
            }
        }
        for(int i=0;i<vmCount;i++)
        {
        	System.out.print(vm[i]+" ");
        	if(i==vmCount-1)
        	{
                System.out.print("--->");       		
        	}
        }

        for(int i=0;i<chromosome.size();i++)
        {
        	System.out.print(chromosome.get(i)+" ");
        	if(i==chromosome.size()-1)
        	{
                System.out.println("");       		
        	}
        }
         System.out.print("");
		return max;
	}
	
	private static ArrayList<Integer>SinglePointCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo,int vmcount)
	{
		ArrayList<Integer> chromosome = new ArrayList<Integer>();
		int size = chromosomeOne.size();
		for(int i=0;i<size;i++)
		{
			if(i<vmcount)
			{
				chromosome.add(chromosomeOne.get(i));
			}
			else
			{
				chromosome.add(chromosomeTwo.get(i));
			}
		}
		return chromosome;
	}
	private static ArrayList<Integer>MultiPointCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo,int k)
	{
		ArrayList<Integer> chromosome = new ArrayList<Integer>();
		int size = chromosomeOne.size();
		int value=k;
		int flag=0;
		for(int i=0;i<size;i++)
		{
			if(i<=value && flag==0)
			{
				chromosome.add(chromosomeOne.get(i));
				if(i==value) 
				{
					value = value + k;
					flag = 1;
				}
			}
			if(i<=value && flag==1)
			{
				chromosome.add(chromosomeTwo.get(i));
				if(i==value) 
				{
					value = value + k;
					flag = 0;
				}
			}
		}
		return chromosome;
	}
	private static ArrayList<Integer>uniformCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo)
	{
		ArrayList<Integer> chromosome = new ArrayList<Integer>();
		int size = chromosomeOne.size();
		for(int i=0;i<size;i++)
		{
			if(i%2==0)
			{
				chromosome.add(chromosomeOne.get(i));
			}
			else
			{
				chromosome.add(chromosomeTwo.get(i));
			}
		}
		return chromosome;
	}
	private static ArrayList<Integer>RandomCrossover(ArrayList<Integer> chromosomeOne,ArrayList<Integer> chromosomeTwo)
	{
		ArrayList<Integer> chromosome = new ArrayList<Integer>();
		int size = chromosomeOne.size();
		Random rand = new Random();
		for(int i=0;i<size;i++)
		{
			if(rand.nextInt(1)==1)
			{
				chromosome.add(chromosomeOne.get(i));
			}
			else
			{
				chromosome.add(chromosomeTwo.get(i));
			}
		}
		return chromosome;
	}
//	private static ArrayList<Integer> Mutation(ArrayList<Integer> chromosome,int vmCount)
//	{
//		Double[] vmProbability = new Double[vmCount];
//		int size = chromosome.size();
//		int vm;
//		double vmmin=0;
//		int vmminIndex=0;
//		double vmmax=0;
//		int vmmaxIndex=0;
//		for(int i = 0;i<size;i++)
//		{
//			vm=chromosome.get(i);
//			vmProbability[vm]=vmProbability[vm]+(1/size);
//		}
//		for(int i=0;i<vmProbability.length;i++)
//		{
//			if(vmProbability[i]<vmmin)
//			{
//				vmmin = vmProbability[i];
//				vmminIndex=i;
//			}
//			if(vmProbability[i]>vmmax)
//			{
//				vmmax = vmProbability[i];
//				vmmaxIndex = i;
//			}
//		}
//		int index = chromosome.indexOf(vmmaxIndex);
//		chromosome.set(index,vmminIndex);
//		return chromosome;
//	}
	////////////////////////// STATIC METHODS ///////////////////////

	/**
	 * Creates main() to run this example
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Log.printLine("Starting Round Robin Algorithm...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			Random rand = new Random();
			Scanner input = new Scanner(System.in);
			int num_user = 3;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			@SuppressWarnings("unused")
			Datacenter datacenter1 = createDatacenter("Datacenter_1");

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// taking no. of cloudlets and VMs as input
			int num_cloudlet = Integer.parseInt(input.nextLine());
			int num_vm = Integer.parseInt(input.nextLine());

			// Taking Cloudlet lengths as input
			int[] cloudletLength = new int[num_cloudlet];
			for (int i = 0; i <num_cloudlet ; i++) 
			{
  				cloudletLength[i] = Integer.parseInt(input.nextLine());
			}

			//Fourth step: Create VMs and Cloudlets and send them to broker
			vmlist = createVM(brokerId,num_vm);
			cloudletList = createCloudlet(brokerId,num_cloudlet,cloudletLength);
			
			//Genetic Algorithm Logic
			
			// Creating a container List to store Chromosomes
			ArrayList<ArrayList> chromosomeList = new ArrayList<ArrayList>();
			
			// creating a container List to store the fitness
			ArrayList<Double> fitnessList = new ArrayList<Double>();
			
			// Creating a temporary Variable to store the chromosome
			ArrayList<Integer> chromosome = new ArrayList<Integer>();
			

			
			int cloudletSize = cloudletList.size();
			int vmSize = vmlist.size();
			
			// Creating initial Population of chromosomes 
			for(int i=0;i<100;i++)
			{
				chromosome = createChromosome(num_cloudlet,num_vm);
				chromosomeList.add(chromosome);
			}
						
			double fitness;
			double length;
			double filesize;
			int indexofFittestChromosome = 0;
			int generation=10;
			// Running the algorithm for 20 generations
			for(int count=1;count<=generation;count++)
			{
							
				// calculating fitness of each chromosome
				for(int i=0;i<num_cloudlet;i++)
				{
					chromosome = chromosomeList.get(i);
					fitness = calculateFitness(chromosome,vmSize,cloudletLength,300,1,1000,1000);
					fitnessList.add(fitness);
				}
				System.out.println("Fittest Solution of Generation "+count+" is having makespan of "+Collections.min(fitnessList));
				
				double minfitness=fitnessList.get(0);

				for(int i = 0;i<fitnessList.size();i++)
				{
					//System.out.print(fitnessList.get(i)+" ");
					if(fitnessList.get(i)<minfitness)
					{
						minfitness=fitnessList.get(i);
						indexofFittestChromosome=i;
					}
					// if(i==fitnessList.size()-1)
					// {
					// 	System.out.println("");
					// }
				}

				for (int i = 0; i < chromosomeList.get(indexofFittestChromosome).size(); i++) {
					System.out.print(chromosomeList.get(indexofFittestChromosome).get(i)+" ");
					if(i==chromosomeList.get(indexofFittestChromosome).size()-1)
					{
						System.out.println("");
					}
				}
				if(count<generation)
				{
				// Discarding unsatisfactory chromosomes (solutions)
			        int index;
					for (int i = 0; i < (int)fitnessList.size()/2; i++)
			        {
			        	index = fitnessList.indexOf(Collections.max(fitnessList));
			            fitnessList.remove(index);
			            chromosomeList.remove(index);
			        } 			

					// Creating offsprings
					//Creating a container to store the offsprings
					ArrayList<ArrayList> offspringList = new ArrayList<ArrayList>();
					
					for(int i = 0;i<chromosomeList.size();i++)
					{
						offspringList.add(SinglePointCrossover(chromosomeList.get(rand.nextInt(chromosomeList.size())),chromosomeList.get(rand.nextInt(chromosomeList.size())),num_vm));
						//offspringList.add(SinglePointCrossover(chromosomeList.get(indexofFittestChromosome),chromosomeList.get(rand.nextInt(chromosomeList.size()))));
					}
					
					// Mutating the offspring chromosome
	//				ArrayList<Integer> tempOffspring=new ArrayList<Integer>();
	//				for(int i=0;i<offspringList.size();i++)
	//				{
	//					tempOffspring = Mutation(offspringList.get(i),vmSize);
	//					offspringList.set(i,tempOffspring);
	//				}
						chromosomeList.addAll(offspringList);
						offspringList.clear();
						offspringList = null;
						fitnessList.clear();
				}
			}
			Cloudlet c;
			ArrayList<Integer> solution = new ArrayList<Integer>();
			solution = chromosomeList.get(indexofFittestChromosome);
			for(int i=0;i<cloudletList.size();i++)
			{
				c = cloudletList.get(i);
				c.setVmId(solution.get(i));
			}
			
			
			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			// Fifth step: Starts the simulation
			CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(newList,vmSize);

			Log.printLine("CloudSimExample6 finished!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		//    Machines
		List<Host> hostList = new ArrayList<Host>();
		
		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
		//    create a list to store these PEs before creating
		//    a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into the list.
		//for a quad-core machine, a list of 4 PEs is required:
		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

		//Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));

		//4. Create Hosts with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList1,
    				new VmSchedulerTimeShared(peList1)
    			)
    		); // This is our first machine

		hostId++;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList2,
    				new VmSchedulerTimeShared(peList2)
    			)
    		); // Second machine


		//To create a host with a space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerSpaceShared(peList1)
    	//		)
    	//	);

		//To create a host with a oportunistic space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerOportunisticSpaceShared(peList1)
    	//		)
    	//	);


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.1;	// the cost of using storage in this resource
		double costPerBw = 0.1;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list,int vmCount) 
	{
		int size = list.size();
		double[] vm = new double[vmCount];
		Cloudlet cloudlet;
		double[] timetaken = new double[size];
		double totalTime = 0;
		double totalCost = 0;
		double maxTime = Double.MIN_VALUE;
		double minTime = Double.MAX_VALUE;
		double avgTime;
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time" +indent+"user id"+ indent + indent +"Cost");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + dft.format(cloudlet.getActualCPUTime()) +
						indent + indent + dft.format(cloudlet.getExecStartTime())+ indent + indent + indent + dft.format(cloudlet.getFinishTime())+indent +cloudlet.getUserId()+ indent + indent + indent
						+ dft.format(cloudlet.getProcessingCost()));
								timetaken[cloudlet.getCloudletId()] = cloudlet.getFinishTime()-cloudlet.getExecStartTime();
								totalCost = totalCost + (cloudlet.getProcessingCost());
				vm[cloudlet.getVmId()]=vm[cloudlet.getVmId()] + (cloudlet.getFinishTime()-cloudlet.getExecStartTime());
			}
		}
		Log.printLine("\n\n\nTime Taken \n\n\n");

		for(int i = 0;i<size;i++)
		{
			Log.print("Cloudlet ID : " + i + indent + timetaken[i]+"\n");
			totalTime = totalTime + timetaken[i];
			if(timetaken[i]<minTime)
			{
				minTime = timetaken[i];
			}
			if(timetaken[i]>maxTime)
			{
				maxTime = timetaken[i];
			}
		}
		double makespan=vm[0];
		for(int i=0;i<vm.length;i++)
		{
			if(vm[i]>makespan)
			{
				makespan=vm[i];
			}
		}
		avgTime = totalTime/size;
		Log.printLine();
		Log.print("Total Time : "   +totalTime+"\n");
		Log.print("Minimum Time : " +minTime+"\n");
		Log.print("Maximum Time : " +maxTime+"\n");
		Log.print("Average Time : " +avgTime+"\n");
		Log.print("Makespan : "+makespan+"\n");
		Log.print("Cost : "+totalCost+"\n");
		Log.printLine("\n\n\nSimulation Finished\n\n\n\n\n\n\n");
	}
}
