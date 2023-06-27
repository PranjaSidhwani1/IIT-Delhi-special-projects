import sys
import copy

#****************************HELPER FUNCTION*******************************#

def number_lines(f):                    #defining helper function number_lines(f) which calculate number of lines in text file
    f=open("assignment5b.txt","rt")      #opening text file and assigning it to f
    n=0                                 #n=0 initialising value
    data=f.read()                       #data read f
    h=data.split("\n")                  #when there is \n in h
    for i in h:
        if i:
            n+=1                        #increase the number by 1
    f.close()                           #closing th text file
    return n                            #returning final value n
#TIME COMPLEXITY OF THIS HELPER FUNCTION IS O(n),WHERE n IS NUMBER OF LINES

def poselement(DATA,a):                 #defining a function poselement(DATA,a) which checks if element a is in DATA list
    for i in range (0,len(DATA)):       #for i in range 0 to len(DATA)
        if DATA[i]==str(a):             #if ith element of data is a
            return i                    #return i
        else:                           #else move to next element
            i=i+1
    return False                        #if not found then return False
#TIME COMPLEXITY OF THIS HELPER FUNCTION IS O(k),WHERE k IS NUMBER OF ELEMENT IN DATA

def loopcontrol(q,e):
    for i in range(0,e):
        if q[i][0]=='while':
            if q[i][1]==q[e][0]:
                return i
        else:
            i=i+1
    return False

def loopend(q,j):
    for i in range (j,len(q)):
        if q[i][0]==q[j][1]:
                return i
        else:
            i=i+1
    return False

def istuple(DATA,x):                    #defining istuple(DATA,x) which checks if x is in DATA list and return position of that element
    for i in range(0,len(DATA)):        #for i in range 0 to len(DATA)
        if type(DATA[i])==tuple:        #if ith element of DATA is tuple
            if x in DATA[i]:            #and if x is in DATA
                return (True,i)         #return (True,i)
        else:                           #else
            i=i+1                       #move to next element
    return (False,False)                #if not found then return (False,False)
#TIME COMPLEXITY OF THIS HELPER FUNCTION IS O(k), WHERE k IS NUMBER OF ELEMENTS IN DATA

#************************INSTRUCTION LIST***************************#

class instructions:
    "instructions"
    def __init__(self,line,operand,jump):
        self.line=line
        self.operand=operand
        self.jump=jump
    
f=open("assignment5b.txt")
line=f.read().splitlines()
for i in range (0,len(line)):
        line[i]=line[i].replace("    ",'')
n=number_lines(f)
W=[]
L=[]
for i in range (0,n):
    p=line[i].split(' ')
    W.append(p)
for i in range (0,n):
    if W[i][0]=='while':
        k=loopend(W,i)
        if W[i][2]=='<' or '>=':
            a=instructions("BLT",(W[i][1],W[i][3]),k+1)
        elif W[i][2]=='>' or '<=':
            a=instructions("BLE",(W[i][1],W[i][3]),k+1)
        else: 
            a=instructions("BL",(W[i][1],W[i][3]),k+1)
        L.append((a.line,a.operand,a.jump))
    else:
        k=loopcontrol(W,i)
        if k==False:
            a=instructions(W[i],None,None)
            L.append(line[i])
        else:
            a=instructions(W[i],None,k)
            L.append(line[i])
            L.append(("branch",k))
            print(L)
print("Instructions List="+str(L))
f.close

#*************************EXECUTION OF INSTRUCTION LIST*************************#

f=open("assignment5b.txt")               #again open text file
n=number_lines(f)                       #n=number of lines in f
i=1                                     #initialising value to i
DATA=[]                                 # creating DATA=[] EMPTY LIST
M=[]
for i in range (1,n+1):                 #for i in range (1,n+1)
    s=f.readline()                      #s=readline of f
    p=s.split()                         #creating list of s
    for i in range (0,len(p)):
        p[i]=p[i].replace("    ",'')
    
    M.append(p)
    q=copy.deepcopy(M)
j=0
while j<len(M):
    k=len(M[j])
    for i in range (1,k):               #for i in range 2 to k
        (g,h)=istuple(DATA,q[j][i])        #(g,h)= istuple(DATA,p[i])
        if g==True:                     #if p[i] in DATA
            d=DATA[h][1]                #d=DATA[h][i]
            M[j][i]=DATA[d]                #changing ith element which is variable to its corresponding value
    s= " ".join(M[j])                      #create s by joining p
    l=s.split(' ',2)                    #l=split s by at max 2
    for x in M[j]:                                    #for x element in p
            if (str.isdigit(x)==True or (x.startswith("-") and x[1:].isdigit()))and x not in DATA: #if x is a digit and x not in DATA
                DATA.append(x)
    for i in range (1,len(M[j])):
            if M[j][i].isalpha()==True:
                sys.exit("variable is not defined:"+ M[j][i])
    
    if l[0]=='while':
        s=" ".join(l)
        l=s.split(' ',1)
        t=l[1].split(':')

        if eval(t[0])==True:
            j=j+1
        else:
            k=loopend(q,j)
            j=k+1

    else:
        #calculating rhs term
        a=eval(l[2])                                   #a=calculated rhs value
        if str(a) not in DATA:                         #if a not in DATA
            DATA.append(str(a))                        #then append that in DATA
        #TIME COMPLEXITY FOR CALCULATING RHS TERM AND APPENDING IN DATA LIST IS O(k),WHERE k IS ELEMENT IN DATA

        c=poselement(DATA,a)                           #finding position of element a in DATA
        (v,i)=istuple(DATA,l[0])                       #i=position of variable in DATA and v=if it is present or not
        if v==True:                                    #if v=True
            if DATA[i][1]!=str(a):                     #if DATA[i][1] is not equal to a
                DATA[i]=(l[0],c)                       #then change DATA[i]=(l[0],c)
        else:                                          #else
            DATA.append((l[0],c))                      #append (l[0],c) in DATA
        k=loopcontrol(q,j)
        if k!=False:
            j=k
        else:
            j=j+1



#TIME COMPLEXITY FOR GETTING DATA LIST IS O(n)*O(k)
#in worst case scenario k=4*n
#T(n)=O(n^2)

    #program for printing

print("DATA LIST=" + str(DATA))                                    #print list DATA
L=list.copy(DATA)                              #L=copy DATA

for i in range (0,len(DATA)):                  #for i in range 0 to len(DATA)
    if type(DATA[i])==tuple:                   #if ith element of DATA is tuple
        print(str(DATA[i][0])+"="+str(DATA[DATA[i][1]])) #then print variable=value
        if DATA[DATA[i][1]] in L:               #if value is in L
            L.remove(DATA[DATA[i][1]])          #then remove that value from list
        L.remove(DATA[i])                       #finally remove that tuple
print("GARBAGE VALUE="+str(L))                  #print GARBAGE VALUE which is list L

f.close



