package heap_package;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.management.openmbean.KeyAlreadyExistsException;
 
public class Heap{

	protected Node root;								// root of the heap
	protected Node[] nodes_array;                    // Stores the address of node corresponding to the keys
	private int max_size;                           // Maximum number of nodes heap can have 
	private static final String NullKeyException = "NullKey";      // Null key exception
	private static final String NullRootException = "NullRoot";    // Null root exception
	private static final String KeyAlreadyExistsException = "KeyAlreadyExists";   // Key already exists exception

	/* 
	   1. Can use helper methods but they have to be kept private. 
	   2. Not allowed to use any data structure. 
	*/
	private void shiftdown2(Node node){
		Node itr=node;
		if (node.right!=null && node.right.value>itr.value){
            itr=node.right;
		}
		if (node.left!=null && node.left.value>itr.value){
            itr=node.left;
		}
        if (itr!=node){
			int swap_val=node.value;
			int swap_key=node.key;
			node.key=itr.key;
			node.value=itr.value;
			itr.key=swap_key;
			itr.value=swap_val;
			nodes_array[node.key]=node;
			nodes_array[itr.key]=itr;
			shiftdown2(itr);
		}
	}
	private void shiftdown(int[] L,int[] M,int size,int j){
		int itr=j;
		int left=2*j+1;
		int right=2*j+2;
		if (right<size && L[right]>L[itr]){
            itr=right;
		}
		if (left<size && L[left]>L[itr]){
            itr=left;
		}
        if (itr!=j){
			int swap_keys=M[j];
            int swap_values=L[j];
            L[j]=L[itr];
            L[itr]=swap_values;
			M[j]=M[itr];
            M[itr]=swap_keys;
            shiftdown(L,M,size,itr);
        }
	}
	private void shiftup(Node node){
		if (node!=root){
			if (node.parent.value<node.value){
				Node itr=node.parent;
				int swap_val=node.value;
				int swap_key=node.key;
				node.key=itr.key;
				node.value=itr.value;
				itr.key=swap_key;
				itr.value=swap_val;
				nodes_array[node.key]=node;
				nodes_array[itr.key]=itr;
				shiftup(itr);
			}
		}
	}

	private void change_array(int[] L,int[] M,int size){
		for (int j=(size/2-1);j>=0;j=j-1){
			shiftdown(L,M,size,j);
		}
	}
	private int max(int a,int b){
		if (a>b){
			return a;
		}
		else{
			return b;
		}
	}
	private int get_height(Node[] Nodes,int j){
		if (2*j+2>Nodes.length-1){
			return Nodes[2*j+1].height+1;
		}
		else{
			return max(Nodes[2*j+1].height,Nodes[2*j+2].height)+1;
		}
	}
	private boolean get_complete(Node[] Nodes,int j){
		if (2*j+2>Nodes.length-1){
			return false;
		}
		else{
			if (Nodes[j].left.is_complete==false){
				return false;
			}
			else if (Nodes[j].right.is_complete==false){
				return false;
			}
			else{
				if (Nodes[j].left.height!=Nodes[j].right.height){
					return false;
				}
				else{
					return true;
				}
			}
		}
	}
	private int height(Node node){
		if (node==null){
			return 0;
		}
		else{
			return node.height;
		}
	}

	public Heap(int max_size, int[] keys_array, int[] values_array) throws Exception{

		/* 
		   1. Create Max Heap for elements present in values_array.
		   2. keys_array.length == values_array.length and keys_array.length number of nodes should be created. 
		   3. Store the address of node created for keys_array[i] in nodes_array[keys_array[i]].
		   4. Heap should be stored based on the value i.e. root element of heap should 
		      have value which is maximum value in values_array.
		   5. max_size denotes maximum number of nodes that could be inserted in the heap. 
		   6. keys will be in range 0 to max_size-1.
		   7. There could be duplicate keys in keys_array and in that case throw KeyAlreadyExistsException. 
		*/

		/* 
		   For eg. keys_array = [1,5,4,50,22] and values_array = [4,10,5,23,15] : 
		   => So, here (key,value) pair is { (1,4), (5,10), (4,5), (50,23), (22,15) }.
		   => Now, when a node is created for element indexed 1 i.e. key = 5 and value = 10, 
		   	  that created node address should be saved in nodes_array[5]. 
		*/ 

		/*
		   n = keys_array.length
		   Expected Time Complexity : O(n).
		*/
		this.max_size = max_size;
		this.nodes_array = new Node[this.max_size];
		int size=values_array.length;
		change_array(values_array, keys_array,size);
		Node[] Nodes=new Node[size];
		for (int j=0;j<size;j++){
			if (j==0){
				Node node=new Node(keys_array[j],values_array[j],null);
				Nodes[j]=node;
			}
			else{
				float k=(j-1)/2;
				Node node=new Node(keys_array[j],values_array[j],Nodes[(int)k]);
				Nodes[j]=node;
			}			
		}
		int stop=(size/2)-1;
		for (int j=0;j<=stop;j++){
			if (j==0){
				this.root=Nodes[j];
			}
			if (2*j+1<size){
				Nodes[j].left=Nodes[2*j+1];
			}
			if (2*j+2<size){
				Nodes[j].right=Nodes[2*j+2];
			}
		}
		for (int j=0;j<size;j++){
			nodes_array[Nodes[j].key]=Nodes[j];
		}
		int count=(size/2)-1;
		for (int j=size-1;j>-1;j--){
			if (j>count){
				Nodes[j].height=1;
				Nodes[j].is_complete=true;
			}
			else{
				Nodes[j].height=get_height(Nodes,j);
				Nodes[j].is_complete=get_complete(Nodes,j);
			}
		}
		if (getKeys().size()!=keys_array.length){
			throw new Exception(KeyAlreadyExistsException);
		}
	}
	private ArrayList<Integer> helper_gm(ArrayList<Integer> L,Node node,int max_val){
		L.add(node.key);
		if (node.left!=null){
			if (node.left.value==max_val){
				helper_gm(L,node.left,max_val);
			}
		}
		if (node.right!=null){
			if (node.right.value==max_val){
				helper_gm(L,node.right,max_val);
			}
		}
		return L;	
	}
	public ArrayList<Integer> getMax() throws Exception{

		/* 
		   1. Returns the keys with maximum value in the heap.
		   2. There could be multiple keys having same maximum value. You have
		      to return all such keys in ArrayList (order doesn't matter).
		   3. If heap is empty, throw NullRootException.

		   Expected Time Complexity : O(1).
		*/
		if (root==null){
			throw new Exception(NullRootException);
		}
		ArrayList<Integer> max_keys = new ArrayList<Integer>();    // Keys with maximum values in heap.
		max_keys=helper_gm(max_keys,root,root.value);

		// To be filled in by the student

		return max_keys;
	}

	public void insert(int key, int value) throws Exception{

		/* 
		   1. Insert a node whose key is "key" and value is "value" in heap 
		      and store the address of new node in nodes_array[key]. 
		   2. If key is already present in heap, throw KeyAlreadyExistsException.

		   Expected Time Complexity : O(logn).
		*/

		// To be filled in by the student
		if (nodes_array[key]!=null){
			throw new Exception(KeyAlreadyExistsException);
		}
		Node node=root;
		String add;
		while (true){
			if (node==null){
				Node head=new Node(key,value,node);
				root=head;
			}
			if (node.left==null){
				Node head=new Node(key,value,node);
				node.left=head;
				nodes_array[key]=head;
				shiftup(node.left);
				add="left";
				break;
			}
			if (node.right==null){
				Node head=new Node(key,value,node);
				node.right=head;
				nodes_array[key]=head;
				shiftup(node.right);
				add="right";
				break;
			}
			if (node.left.is_complete==false){
				node=node.left;
			}
			else if (node.right.is_complete==false){
				node=node.right;
			}
			else{
				if (node.is_complete==true){
					node=node.left;
				}
				else{
					node=node.right;
				}
			}
		}
		while (true){
			if (add=="left"){
				node.height=max(height(node.left),height(node.right))+1;
				node.is_complete=false;
			}
			else if (add=="right"){
				if (node.left.is_complete==false){
					node.is_complete=false;
				}
				else if (node.right.is_complete==false){
					node.is_complete=false;
				}
				else{
					if (node.left.height!=node.right.height){
						node.is_complete=false;
					}
					else{
						node.is_complete=true;
					}
				}
			}
			if (node.parent==null){
				break;
			}
			node=node.parent;
			
		}
	}

	public ArrayList<Integer> deleteMax() throws Exception{

		/* 
		   1. Remove nodes with the maximum value in the heap and returns their keys.
		   2. There could be multiple nodes having same maximum value. You have
		      to delete all such nodes and return all such keys in ArrayList (order doesn't matter).
		   3. If heap is empty, throw NullRootException.

		   Expected Average Time Complexity : O(logn).
		*/
		if (root==null){
			throw new Exception(NullRootException);
		}
		ArrayList<Integer> max_keys = getMax();   // Keys with maximum values in heap that will be deleted.
		String del=null;
		for (int j=0;j<max_keys.size();j++){
			Node node=root;
			nodes_array[root.key]=null;
			while (true){
				if (height(node.right)==1 && height(node.left)==1){
					root.value=node.right.value;
					root.key=node.right.key;
					node.right=null;
					shiftdown2(root);
					del="right";
					break;
				}
				if (height(node.left)==1){
					root.value=node.left.value;
					root.key=node.left.key;
					node.left=null;
					shiftdown2(root);
					del="left";
					break;
				}
				if (node.is_complete==true){
					node=node.right;
				}
				else{
					if (node.left.is_complete==false){
						node=node.left;
					}
					else if (node.right.is_complete==false){
						node=node.right;
					}
					else{
						node=node.left;
					}
				}
			}
			while (true){
				if (del=="right"){
					node.is_complete=false;
				}
				else if (del=="left"){
					node.height=max(height(node.left),height(node.right))+1;
					if (node.left==null){
						node.is_complete=true;
					}
					else if (node.right==null){
						node.is_complete=false;
					}
					else if (node.left.is_complete==false){
						node.is_complete=false;
					}
					else if (node.right.is_complete==false){
						node.is_complete=false;
					}
					else{
						if (node.left.height!=node.right.height){
							node.is_complete=false;
						}
						else{
							node.is_complete=true;
						}
					}
				}
				if (node.parent==null){
					break;
				}
				node=node.parent;
				}
			}
		return max_keys;
	}

	public void update(int key, int diffvalue) throws Exception{

		/* 
		   1. Update the heap by changing the value of the node whose key is "key" to value+diffvalue.
		   2. If key doesn't exists in heap, throw NullKeyException.

		   Expected Time Complexity : O(logn).
		*/

		// To be filled in by the student
		if (nodes_array[key]==null){
			throw new Exception(KeyAlreadyExistsException);
		}
		Node node=nodes_array[key];
		node.value=node.value+diffvalue;
		if (diffvalue>0){
			shiftup(node);
		}
		else{
			shiftdown2(node);
		}
	}

	public int getMaxValue() throws Exception{

		/* 
		   1. Returns maximum value in the heap.
		   2. If heap is empty, throw NullRootException.

		   Expected Time Complexity : O(1).
		*/

		// To be filled in by the student
		if (root==null){
			throw new Exception(NullRootException);
		}
		return root.value;
	}
	private ArrayList<Integer> helper_gk(ArrayList<Integer> L,Node node){
		L.add(node.key);
		if (node.left!=null){
			helper_gk(L,node.left);
		}
		if (node.right!=null){
			helper_gk(L,node.right);
		}
		return L;	
	}
	public ArrayList<Integer> getKeys() throws Exception{

		/*
		   1. Returns keys of the nodes stored in heap.
		   2. If heap is empty, throw NullRootException.
		 
		   Expected Time Complexity : O(n).
		*/
		if (root==null){
			throw new Exception(NullRootException);
		}
		ArrayList<Integer> keys = new ArrayList<Integer>();   // Stores keys of nodes in heap
		keys=helper_gk(keys, root);
		// To be filled in by the student

		return keys;
	}

	// Write helper functions(if any) here (They have to be private).
}