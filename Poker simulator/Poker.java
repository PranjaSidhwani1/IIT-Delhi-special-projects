import heap_package.Node;
import heap_package.Heap;
import java.util.ArrayList;

public class Poker{

	private int city_size;            // City Population
	public int[] money;		         // Denotes the money of each citizen. Citizen ids are 0,1,...,city_size-1. 
	private Heap loss;
	private Heap profit;
	private Heap total_loss;
	private Heap total_profit;
	/* 
	   1. Can use helper methods but they have to be kept private. 
	   2. Allowed to use only PriorityQueue data structure globally but can use ArrayList inside methods. 
	   3. Can create at max 4 priority queues.
	*/
	
	public void initMoney(){
		// Do not change this function.
		for(int i = 0;i<city_size;i++){
			money[i] = 100000;							// Initially all citizens have $100000. 
		}
	}

	public Poker(int city_size, int[] players, int[] max_loss, int[] max_profit){

		/* 
		   1. city_size is population of the city.
		   1. players denotes id of the citizens who have come to the Poker arena to play Poker.
		   2. max_loss[i] denotes the maximum loss player "players[i]"" can bear.
		   3. max_profit[i] denotes the maximum profit player "players[i]"" will want to get.
		   4. Initialize the heap data structure(if required). 
		   n = players.length 
		   Expected Time Complexity : O(n).
		*/
		// System.out.println("poker");
		// for (int j=0;j<players.length;j++){
		// 	System.out.print(players[j]);
		// 	System.out.print(",");
		// }
		// System.out.println("ok");
		// for (int j=0;j<players.length;j++){
		// 	System.out.print(max_loss[j]);
		// 	System.out.print(",");
		// }
		// System.out.println("ok");
		// for (int j=0;j<players.length;j++){
		// 	System.out.print(max_profit[j]);
		// 	System.out.print(",");
		// }
		try{
		this.city_size = city_size;
		this.money = new int[this.city_size];
		this.initMoney();
		int[] zero=new int[players.length];
		for (int j=0; j<players.length;j++ ){
			zero[j]=0;
		}
		for (int j=0;j<players.length;j++){
			max_loss[j]=-max_loss[j];
			max_profit[j]=-max_profit[j];
		}
		int[] players1=new int[players.length];
		for (int i=0;i<players.length;i++){
			players1[i]=players[i];
		}
		this.loss= new Heap(city_size,players,max_loss);
		this.profit=new Heap(city_size,players1,max_profit);
		this.total_loss=new Heap(city_size,players,zero);
		this.total_profit=new Heap(city_size,players,zero);
		// To be filled in by the student
	}
	catch (Exception e){
		System.out.println("sorry");
	}
	}

	public ArrayList<Integer> Play(int[] players, int[] bids, int winnerIdx){

		/* 
		   1. players.length == bids.length
		   2. bids[i] denotes the bid made by player "players[i]" in this game.
		   3. Update the money of the players who has played in this game in array "money".
		   4. Returns players who will leave the poker arena after this game. (In case no
		      player leaves, return an empty ArrayList).
                   5. winnerIdx is index of player who has won the game. So, player "players[winnnerIdx]" has won the game.
		   m = players.length
		   Expected Time Complexity : O(mlog(n))
		*/
		// System.out.println("play");
		// for (int j=0;j<players.length;j++){
		// 	System.out.print(players[j]);
		// 	System.out.print(",");
		// }
		// System.out.println(winnerIdx);
		// for (int j=0;j<players.length;j++){
		// 	System.out.print(bids[j]);
		// 	System.out.print(",");
		// }
		try{
		int winner = players[winnerIdx];					// Winner of the game.
		int win_money=0;
		for (int j=0; j<bids.length;j++){
			win_money=win_money+bids[j];
		}
		for (int j=0; j<bids.length;j++){
			if (j==winnerIdx){
				money[players[j]]=money[players[j]]+win_money-bids[j];
				total_profit.update(winner,win_money-bids[j]);
				profit.update(winner,win_money-bids[j]);
				loss.update(winner,-win_money+bids[j]);
			}
			else{
				money[players[j]]=money[players[j]]-bids[j];
				total_loss.update(players[j],bids[j]);
				profit.update(players[j],-bids[j]);
				loss.update(players[j],bids[j]);
			}
		}
		ArrayList<Integer> playersToBeRemoved = new ArrayList<Integer>();     // Players who will be removed after this game.
		ArrayList<Integer> remove_loss= new ArrayList<Integer>();
		ArrayList<Integer> remove_profit= new ArrayList<Integer>();

		while (loss.getMaxValue()>=0){
			remove_loss.addAll(loss.deleteMax());
		}
		playersToBeRemoved.addAll(remove_loss);
		while (profit.getMaxValue()>=0){
			remove_profit.addAll(profit.deleteMax());
		}
		playersToBeRemoved.addAll(remove_profit);
		ArrayList<Integer> ok= new ArrayList<Integer>();
		for (int j=0;j<remove_loss.size();j++){
			profit.update(remove_loss.get(j),Integer.MAX_VALUE);
			ok=profit.deleteMax();
		}
		for (int j=0;j<remove_profit.size();j++){
			loss.update(remove_profit.get(j),Integer.MAX_VALUE);
			ok=loss.deleteMax();
		}
		return playersToBeRemoved;
	}
	catch(Exception e){
		System.out.println("sorry");
	}
	return null;
	}

	public void Enter(int player, int max_loss, int max_profit){

		/*
			1. Player with id "player" enter the poker arena.
			2. max_loss is maximum loss the player can bear.
			3. max_profit is maximum profit player want to get. 
			Expected Time Complexity : O(logn)
		*/
		try{
		if (money[player]==100000){
			total_loss.insert(player,0);
			total_profit.insert(player,0);
		}
		profit.insert(player,-max_profit);
		loss.insert(player,-max_loss);
		// To be filled in by the student
	}
	catch(Exception e){
		System.out.println("sorry");
	}
	
	}

	public ArrayList<Integer> nextPlayersToGetOut(){

		/* 
		   Returns the id of citizens who are likely to get out of poker arena in the next game. 
		   Expected Time Complexity : O(1). 
		*/
		try{
		ArrayList<Integer> players = new ArrayList<Integer>();    // Players who are likely to get out in next game.
		if (loss.getMaxValue()>profit.getMaxValue()){
			return loss.getMax();
		}
		else if (loss.getMaxValue()<profit.getMaxValue()){
			return profit.getMax();
		}
		else{
			ArrayList<Integer> loss_keys=loss.getMax();
			ArrayList<Integer> profit_keys=profit.getMax();
			players.addAll(loss_keys);
			players.addAll(profit_keys);
			boolean bool=true;
			for (int j=0;j<loss_keys.size();j++){
				if (loss_keys.get(j)!=profit_keys.get(j)){
					bool=false;
					break;
				}
			}
			if (bool==false){
				return players;
			}
			return loss_keys;
		}
	}
	catch(Exception e){
		System.out.println("sorry");
	}
	return null;
	}

	public ArrayList<Integer> playersInArena(){

		/* 
		   Returns id of citizens who are currently in the poker arena. 
		   Expected Time Complexity : O(n).
		*/
		try{
		ArrayList<Integer> currentPlayers = new ArrayList<Integer>();    // citizens in the arena.


		// To be filled in by the student

		return loss.getKeys();
	}
	catch(Exception e){
		System.out.println("sorry");
	}
	return null;
	}

	public ArrayList<Integer> maximumProfitablePlayers(){

		/* 
		   Returns id of citizens who has got most profit. 
			
		   Expected Time Complexity : O(1).
		*/
		try{
		ArrayList<Integer> citizens = new ArrayList<Integer>();    // citizens with maximum profit.

		// To be filled in by the student
		
		return total_profit.getMax();
	}
	catch(Exception e){
		System.out.println("sorry");
	}
	return null;
	}

	public ArrayList<Integer> maximumLossPlayers(){

		/* 
		   Returns id of citizens who has suffered maximum loss. 
			
		   Expected Time Complexity : O(1).
		*/
		try{
		ArrayList<Integer> citizens = new ArrayList<Integer>();     // citizens with maximum loss.

		// To be filled in by the student

		return total_loss.getMax();
		}
		catch(Exception e){
			System.out.println("sorry");
		}
		return null;
	}			
}