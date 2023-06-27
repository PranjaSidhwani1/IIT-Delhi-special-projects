import java.io.*;
import java.util.*;

public class SnakesLadder extends AbstractSnakeLadders {
	
	int N, M;
	int snakes[];
	int ladders[];
	ArrayList<ArrayList<Integer>> graph_front;
	ArrayList<ArrayList<Integer>> graph_back;
	int[] distance_front;					//distance of all point from start
	int[] distance_back;					//distance of all point from end
	ArrayList<Integer> ladders_only;		//ladders start point only
	private int min(int a,int b){
		if (a<b){
			return a;
		}
		else{
			return b;
		}
	}
	private int abs(int a){
		if (a>=0){
			return a;
		}
		else{
			return -a;
		}
	}
	private void BFS(ArrayList<ArrayList<Integer>> L,int start,int end,int[] distance){  //find bfs of the graph from start point
		LinkedList<Integer> queue=new LinkedList<Integer>();
		boolean[] is_visit=new boolean[L.size()];
		for (int j=0;j<L.size();j++){
			distance[j]=Integer.MAX_VALUE;
			is_visit[j]=false;
		}
		is_visit[start]=true;												
		distance[start]=0;
		queue.add(start);
		int p;
		while (queue.isEmpty()==false){
			p=queue.remove();
			for (int j=0;j<L.get(p).size();j++){
				if (is_visit[L.get(p).get(j)]==false){
					is_visit[L.get(p).get(j)]=true;
					distance[L.get(p).get(j)]=distance[p]+1;
					queue.add(L.get(p).get(j));								
				}
			}
		}
	}
	
	public SnakesLadder(String filename) throws Exception{
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		N = Integer.parseInt(br.readLine());
        M = Integer.parseInt(br.readLine());
	    snakes = new int[N];
		ladders = new int[N];
		ladders_only=new ArrayList<Integer>();
	    for (int i = 0; i < N; i++){
			snakes[i] = -1;
			ladders[i] = -1;
		}
		for(int i=0;i<M;i++){
            String e = br.readLine();
            StringTokenizer st = new StringTokenizer(e);
            int source = Integer.parseInt(st.nextToken());
            int destination = Integer.parseInt(st.nextToken());

			if(source<destination){

				ladders[source] = destination;
				ladders_only.add(source);
			}
			else{
				snakes[source] = destination;
			}
		}
		// catch(Exception e){
		// 	System.out.println("ok");
		// }
		graph_front=new ArrayList<ArrayList<Integer>>();    // start point is 0
		graph_back=new ArrayList<ArrayList<Integer>>();		// start point is N
		for (int i=0;i<N+1;i++){
			ArrayList<Integer> M=new ArrayList<Integer>(); 
			graph_front.add(M);
		}
		for (int i=0;i<N+1;i++){
			ArrayList<Integer> M=new ArrayList<Integer>(); 
			graph_back.add(M);
		}
		int k;
		for (int i=0;i<N;i++){
			if (ladders[i]!=-1){
				continue;
			}
			if (snakes[i]!=-1){
				continue;
			}
			for (int j=i+1;j<=min(i+6,N);j++){
				k=j;
				if (j<N){
					while (ladders[k]!=-1 || snakes[k]!=-1){
						if (ladders[k]!=-1){
							k=ladders[k];
						}
						if (snakes[k]!=-1){
							k=snakes[k];
						}
					}
				}
				graph_front.get(i).add(k);
			}
		}
		for (int i=0;i<graph_front.size();i++){
			for (int j=0;j<graph_front.get(i).size();j++){
				graph_back.get(graph_front.get(i).get(j)).add(i);
			}
		}
		distance_front =new int[graph_front.size()];
		BFS(graph_front,0,N,distance_front);			

		distance_back =new int[graph_front.size()];
		BFS(graph_back,N,0,distance_back);
		// for (int j=0;j<distance_front.length;j++){
		// 	System.out.print(j);
		// 	System.out.print(" ");
		// 	System.out.print(distance_front[j]);
		// 	System.out.println();
		// }
		// for (int j=0;j<distance_back.length;j++){
		// 	System.out.print(j);
		// 	System.out.print(" ");
		// 	System.out.print(distance_back[j]);
		// 	System.out.println();
		// }
	}

    
	public int OptimalMoves()
	{
		/* Complete this function and return the minimum number of moves required to win the game. */
		// for (int j=0;j<graph_back.size();j++){
		// 	System.out.print(j);
		// 	System.out.print(" ");
		// 	System.out.print(graph_back.get(j));
		// 	System.out.println();
		// }
		// for (int j=0;j<distance.length;j++){
		// 	System.out.print(j);
		// 	System.out.print(" ");
		// 	System.out.print(distance[j]);
		// 	System.out.println();
		// }
		if (distance_front[distance_front.length-1]==Integer.MAX_VALUE){
			return -1;
		}
		else{
			return distance_front[distance_front.length-1];
		}
	}

	public int Query(int x, int y)
	{
		/* Complete this function and 
			return +1 if adding a snake/ladder from x to y improves the optimal solution, 
			else return -1. */
			if (distance_front[distance_front.length-1]>distance_front[x]+distance_back[y]){
				return 1;
			}
			else{
				return -1;
			}
	}
	//My code for find best new snake is O(m^2) however it can be done in O(n) both can be optimal in different case
	//like O(n) when m=n
	//ans O(m^2) when m=0
	public int[] FindBestNewSnake()
	{
		int result[] = {-1,-1};
		/* Complete this function and 
			return (x, y) i.e the position of snake if adding it increases the optimal solution by largest value,
			if no such snake exists, return (-1, -1) */
		// ArrayList<Integer> x=new ArrayList<Integer>();
		// ArrayList<Integer> y=new ArrayList<Integer>();
		// ArrayList<Integer> distance_bottom=new ArrayList<Integer>();
		// ArrayList<Integer> distance_top=new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> ans=new ArrayList<ArrayList<Integer>>();
		int ladder_bottom;
		int ladder_top;
		for (int j=0;j<ladders_only.size();j++){
			for (int k=0;k<ladders_only.size();k++){
				if (j!=k){
					ladder_bottom=ladders_only.get(k);
					ladder_top=ladders[ladders_only.get(j)];
					ArrayList<Integer> L=new ArrayList<Integer>();
					L.add(ladder_top);
					L.add((ladder_bottom));
					L.add(distance_front[ladder_top]+distance_back[ladders[ladder_bottom]]);
					ans.add(L);
				}
			}
		}
		Collections.sort(ans,Comparator.comparingDouble(o -> o.get(2)));
		for (int j=0;j<ans.size();j++){
			if (ans.get(j).get(0)>ans.get(j).get(1)){
				result[0]=ans.get(j).get(0);
				result[1]=ans.get(j).get(1)	;
				break;
			}
		}
		// Arrays.sort(x, Comparator.comparingDouble(o -> o[1]));
		// Arrays.sort(y, Comparator.comparingDouble(o -> o[1]));
		// xi=x[0];
		// yi=y[0];
		return result;
	}
}