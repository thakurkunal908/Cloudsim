

"""
command to run Algoritms in CMD
C:\\CloudsimSetup\\cloudsim-3.0.3>javac -classpath jars\\cloudsim-3.0.3.jar; examples\\org\\cloudbus\\cloudsim\\examples\\simulation.java

C:\\CloudsimSetup\\cloudsim-3.0.3>java -classpath jars\\cloudsim-3.0.3.jar;examples org.cloudbus.cloudsim.examples.simulation


To be ignored
"""


#Importing Dependencies
import tkinter as tk
import re
from tkinter import ttk
from tkinter.filedialog import askopenfilename,asksaveasfilename
from tkinter import messagebox as msg
import os.path,subprocess
from subprocess import STDOUT,PIPE
from matplotlib.figure import Figure
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg


#Java Compiling & Running

#Global Variables
input_data = []
output_data = [["","Total Time","Minimum Time","Maximum Time","Average Time","Makespan","Cost"]]


def compile_java(java_file,path,jar):
    command = "javac -classpath jars\\"+jar+"; examples\\org\\cloudbus\\cloudsim\\examples\\"+java_file
    subprocess.run(command,cwd=path)
def execute_java(java_file,stdin,path,jar,tab,algo):
	global output_data
	java_class,ext = os.path.splitext(java_file)
	cmd = "java -classpath jars\\"+jar+";examples org.cloudbus.cloudsim.examples."+java_class
	proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT,cwd=path)
	stdout,stderr = proc.communicate(stdin)
	output = stdout.decode("utf-8")
	totalTime = round(float(re.search("Total Time : ([0-9]*\.)?[0-9]+", output).group().split(":")[1].lstrip()),3)
	minTime = round(float(re.search("Minimum Time : ([0-9]*\.)?[0-9]+", output).group().split(":")[1].lstrip()),3)
	maxTime = round(float(re.search("Maximum Time : ([0-9]*\.)?[0-9]+", output).group().split(":")[1].lstrip()),3)
	avgTime = round(float(re.search("Average Time : ([0-9]*\.)?[0-9]+", output).group().split(":")[1].lstrip()),3)
	makespan = round(float(re.search("Makespan : ([0-9]*\.)?[0-9]+", output).group().split(":")[1].lstrip()),3)
	cost = round(float(re.search("Cost : ([0-9]*\.)?[0-9]+", output).group().split(":")[1].lstrip()),3)
	l = [algo,totalTime,minTime,maxTime,avgTime,makespan,cost]
	output_data.append(l)
	T = tk.Text(tab, height=100, width=200)
	T.pack(fill=tk.BOTH, side=tk.LEFT, expand=True)
	T.insert(tk.END, stdout)

#functionalities 
def open_file():
    """Open a file for editing."""
    global input_data
    filepath = askopenfilename(initialdir="c:",title="Select File",
        filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")]
    )
    if filepath:
    	with open(filepath,'rb') as reader:
    		input_data = reader.read()

def run():
	clear()
	global input_data
	if(input_data):
		file_name = 'ShortestJobFirst.java'
		compile_java(file_name,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar")
		execute_java(file_name,input_data,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar",tab1,"Shortest Job First")
		#RoundRobin
		file_name = 'CloudSimExample6.java'
		compile_java(file_name,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar")
		execute_java(file_name,input_data,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar",tab2,"Round Robin")
		file_name = 'CloudSimExample6.java'
		compile_java(file_name,"C:\\Genetic\\cloudsim-3.0.3","cloudsim-3.0.3.jar")
		execute_java(file_name,input_data,"C:\\Genetic\\cloudsim-3.0.3","cloudsim-3.0.3.jar",tab3,"Genetic Algorithm")
		drawTable(output_data,len(output_data),len(output_data[0]),tab5)
		input_data = []
	else:
		msg_ans = tk.messagebox.askyesnocancel(title="Input", message="Do you want to proceed without custom input?")
		if(msg_ans):
			file_name = 'ShortestJobFirst.java'
			compile_java(file_name,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim.jar")
			execute_java(file_name,input_data,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim.jar",tab1,"Shortest Job First")
			#RoundRobin
			file_name = 'CloudSimExample6.java'
			compile_java(file_name,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar")
			execute_java(file_name,input_data,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar",tab2,"Round Robin")
			drawTable(output_data,len(output_data),len(output_data[0]),tab5)
			input_data = []		
		else:
			if(input_data):
				file_name = 'ShortestJobFirst.java'
				compile_java(file_name,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim.jar")
				execute_java(file_name,input_data,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim.jar",tab1,"Shortest Job First")
				#RoundRobin
				file_name = 'CloudSimExample6.java'
				compile_java(file_name,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar")
				execute_java(file_name,input_data,"C:\\CloudsimSetup\\cloudsim-3.0.3","cloudsim-3.0.3.jar",tab2,"Round Robin")
				drawTable(output_data,len(output_data),len(output_data[0]),tab5)
				input_data = []
			else:
				tk.messagebox.showerror(title="Input Data Not Selected", message="Input Data Not Selected!!!\nPlease Select it from Open Option")

def clear():
	global output_data
	output_data = output_data[0:1]
	for tab in (tab1,tab2,tab3,tab4,tab5,tab6):
		for widget in tab.winfo_children():
			widget.destroy()
		tab.pack_forget()

def save_file():
    """Save the current file as a new file."""
    filepath = asksaveasfilename(
        defaultextension="txt",
        filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")],
    )
    if not filepath:
        return

#Comparision Table
def drawTable(data,height,width,tab):
	fr_table = tk.Frame(master=tab)
	fr_table.pack(pady=50)
	for i in range(height): #Rows
		for j in range(width): #Column
			text = data[i][j]
			if(i==0 or j==0):
				b = tk.Label(fr_table, text=text,relief=tk.GROOVE,padx=40, pady=10,borderwidth=2)
				b.grid(row=i, column=j,sticky='NSEW')
			else:
				b = tk.Label(fr_table, text=text,relief=tk.SUNKEN,padx=40, pady=10,borderwidth=2,bg='#FFF')
				b.grid(row=i, column=j,sticky='NSEW')

def exit():
	answer = msg.askyesno("Exit","Are you sure you want to Exit?")
	if(answer):
		window.destroy()

window = tk.Tk()
window.title("Final Year Project!!!")
window.rowconfigure(0, minsize=800, weight=1)
window.columnconfigure(1, minsize=800, weight=1)
fr_display = tk.Frame(master=window,relief=tk.SUNKEN,borderwidth=2)
fr_buttons = tk.Frame(master=window, relief=tk.GROOVE, borderwidth=4)
btn_open = tk.Button(fr_buttons, text="Open", command=open_file)
btn_run = tk.Button(fr_buttons,text="Run",command=run)
btn_clr = tk.Button(fr_buttons,text="Clear",command=clear)
btn_save = tk.Button(fr_buttons, text="Save", command=save_file)
btn_exit = tk.Button(fr_buttons, text="Exit",command=exit)
window.rowconfigure(0, minsize=800, weight=1)
window.columnconfigure(1, minsize=800, weight=1)
btn_open.grid(row=0, column=0, sticky="ew", ipadx=15,ipady=5,padx=20, pady=10)
btn_run.grid(row=1,column=0,sticky="ew", ipadx=15,ipady=5,padx=20, pady=10)
btn_clr.grid(row=2,column=0,sticky="ew", ipadx=15,ipady=5,padx=20, pady=10)
btn_save.grid(row=3, column=0, sticky="ew", ipadx=15,ipady=5,padx=20, pady=10)
btn_exit.grid(row=4,column=0,sticky="ew",ipadx=15,ipady=5,padx=20,pady=10)
fr_buttons.grid(row=0, column=0, sticky="ns")
fr_display.grid(row=0, column=1, sticky="nsew")
tabControl = ttk.Notebook(master=fr_display)
tab1 = ttk.Frame(tabControl)
tab2 = ttk.Frame(tabControl)
tab3 = ttk.Frame(tabControl)
tab4 = ttk.Frame(tabControl)
tab5 = ttk.Frame(tabControl)
tab6 = ttk.Frame(tabControl)
tabControl.add(tab1, text ='SSJF') 
tabControl.add(tab2, text ='Round Robin') 
tabControl.add(tab3, text ='Genetic Algorithm')
tabControl.add(tab4, text ='Ant Colony')
tabControl.add(tab5, text ='Comparison Table')
tabControl.add(tab6, text ='Graph')
tabControl.pack(expand = 1, fill ="both")
window.mainloop()
