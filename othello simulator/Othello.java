import java.io.*;
import java.util.*;

import javax.print.attribute.standard.PrinterLocation;
import javax.swing.plaf.basic.BasicBorders;

public class Othello {
    int turn;
    int winner;
    int board[][];
    private int x_direction[] = {-1,-1,0,1,1,1,0,-1};
    private int y_direction[] = {0,1,1,1,0,-1,-1,-1};
    //add required class variables here
    // public static void main(String[] args) throws Exception{
    //     Othello L=new Othello("ok.txt");
    //     // System.out.println(L.minimax(L.board,0,2,true)[0]);
    //     // System.out.println(L.minimax(L.board,0,2,true)[1]);
    //     // System.out.println(L.bestMove(3));
    //     // System.out.println(L.bestMove(1));
    //     // System.out.println(L.fullGame(1));
    //     // System.out.println(L.minimax(L.board,0,2,true)[1]);
    //     //System.out.println(L.boardScore());
    // }

    public Othello(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        turn = sc.nextInt();
        board = new int[8][8];
        for(int i=0;i<8;++i) {
            for(int j=0;j<8;++j){
                board[i][j]=sc.nextInt();
            }
        }
        winner = -1;
        //Student can choose to add preprocessing here
    }

    public int boardScore() {
        /* Complete this function to return num_black_tiles - num_white_tiles if turn = 0, 
         * and num_white_tiles-num_black_tiles otherwise. 
        */
        int score=0;
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if (board[i][j]==0){
                    score=score+1;
                }
                else if (board[i][j]==1){
                    score=score-1;
                }
            }
        }
        if (turn==0){
            return score;
        }
        else{
            return -score;
        }
    }

    private int myboardScore(int[][] copyboard) {
        /* Complete this function to return num_black_tiles - num_white_tiles if turn = 0, 
         * and num_white_tiles-num_black_tiles otherwise. 
        */
        int score=0;
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if (copyboard[i][j]==0){
                    score=score+1;
                }
                else if (copyboard[i][j]==1){
                    score=score-1;
                }
            }
        }
        return score;
    }
    private int[] is_move(int[][] temp_board,int turn,int x,int y){
        int[] ans=new int[3];
        if(x<0 || x>=8 || y < 0 || y >= 8 || temp_board[x][y]!=-1){
            return null;
        }
        boolean bool=false;
        for(int j=0;j<x_direction.length;j++){
            int x_step=x_direction[j];
            int y_step=y_direction[j];
            int new_x=x+x_step;
            int new_y=y+y_step;
            int count=0;
            ans[0]=turn;
            while(new_x>=0 && new_x<8 && new_y>=0 && new_y<8){
                if(temp_board[new_x][new_y]==-1){
                    break;
                }
                else if(temp_board[new_x][new_y]!=turn){
                    new_x=new_x+x_step;
                    new_y=new_y+y_step;
                    count=count+1;
                }
                else{
                    if(count>0){
                        bool=true;
                    }
                    break;
                }
            }
        }
        if(bool==true){
            ans[1]=x;ans[2]=y;
            return ans;
        }
        else{
            return null;
        }
    }
    private boolean move(int[][] temp_board,int turn,int x,int y){
        if(x<0 || x>=8 || y < 0 || y >= 8 || temp_board[x][y]!=-1){
            return false;
        }
        boolean bool=false;
        for(int j=0;j<x_direction.length;j++){
            int x_step=x_direction[j];
            int y_step=y_direction[j];
            int new_x=x+x_step;
            int new_y=y+y_step;
            int count=0;
            while(new_x>=0 && new_x<8 && new_y>=0 && new_y<8){
                if(temp_board[new_x][new_y]==-1){
                    break;
                }
                else if(temp_board[new_x][new_y]!=turn){
                    new_x=new_x+x_step;
                    new_y=new_y+y_step;
                    count=count+1;
                }
                else{
                    if(count > 0){
                        bool=true;
                        int convertX=new_x-x_step;
                        int convertY=new_y-y_step;
                        while(convertX!=x || convertY!=y){
                            temp_board[convertX][convertY]=turn;
                            convertX=convertX-x_step;
                            convertY=convertY-y_step;
                        }
                    }
                    break;
                }
            }
        }
        if(bool==true){
            temp_board[x][y]=turn;
        }
        return bool;
    }
    private ArrayList<int[]> get_to_move(int[][] temp_board,int chance){
        ArrayList<int[]> moves=new ArrayList<int[]>();
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if (is_move(temp_board,chance,i,j)!=null){
                    int[] ans=is_move(temp_board,chance,i,j);
                    moves.add(ans);
                }
            }
        }
        return moves;
    }
    private int[] minimax (int[][]temp_board,int turn,int k){
        // printboard(temp_board);
        int score;
        int[] count=new int[2];
        if (k==0){
            count[0]=-1;
            count[1]=myboardScore(temp_board);
            return count;
        }
        ArrayList<int[]> to_moves=get_to_move(temp_board,turn);
        if (to_moves.size()==0){
            return minimax(temp_board,1-turn, k-1);
        }
        else{
        if (turn==0){
            count[0]=100;
            score=Integer.MIN_VALUE;
            count[1]=score;
        }
        else{
            count[0]=100;
            score=Integer.MAX_VALUE;
            count[1]=score;
        }
        for (int j=0;j<to_moves.size();j++){
            int[][] copyboard=mygetBoardCopy(temp_board);
            int[] ans=to_moves.get(j);
                // System.out.print(ans[5]);
                // System.out.print(" ");
                // System.out.println(ans[6]);
            move(copyboard,ans[0],ans[1],ans[2]);
            //move(copyboard,ans[0],ans[1],ans[2],ans[3],ans[4],ans[5],ans[6],ans[7]);
            // printboard(copyboard);
            // System.out.print(8*ans[5]+ans[6]);
            // System.out.print(" ");
            // System.out.println(count[0]);
            int[] output=minimax(copyboard,1-ans[0],k-1);
            // System.out.println(output[1]);
            if (turn==0){
                if (count[1]<output[1]){
                    count[0]=ans[1]*8+ans[2];
                    count[1]=output[1];
                }
                else if (count[1]==output[1]){
                    count[0]=Math.min(ans[1]*8+ans[2],count[0]);
                }
                // else{
                //     count[0]=ans[5]*8+ans[6];
                // }
            }
            else if (turn==1){
                if (count[1]>output[1]){
                    count[0]=ans[1]*8+ans[2];
                    count[1]=output[1];
                }
                else if (count[1]==output[1]){
                    count[0]=Math.min(ans[1]*8+ans[2],count[0]);
                }
                // else{
                //     count[0]=ans[5]*8+ans[6];
                // }
            }
        }
        }
        return count;
    }
    
    private void printboard(int[][] copyboard){
        for (int i=0;i<8;i++){
            for (int q=0;q<8;q++){
                System.out.print(copyboard[i][q]);
                System.out.print("  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // private ArrayList<ArrayList<Integer>> get_move(int[][] board){
    //     ArrayList<ArrayList<Integer>> moves=new ArrayList<ArrayList<Integer>>();
    //     for (int i=0;i<8;i++){
    //         for (int j=0;j<8;j++){
    //             if (is_move(turn,i,j)!=null){
    //                 ArrayList<Integer> m=new ArrayList<Integer>();
    //                 m.add(i);
    //                 m.add(j);
    //                 moves.add(m);
    //             }
    //         }
    //     }
    //     return moves;
    // }

    public int bestMove(int k) {
        /* Complete this function to build a Minimax tree of depth k (current board being at depth 0),
         * for the current player (siginified by the variable turn), and propagate scores upward to find
         * the best move. If the best move (move with max score at depth 0) is i,j; return i*8+j
         * In case of ties, return the smallest integer value representing the tile with best score.
         * 
         * Note: Do not alter the turn variable in this function, so that the boardScore() is the score
         * for the same player throughout the Minimax tree.
        */
        int[] ans=minimax(this.board,turn,k);
        return ans[0];

    }

    public int[][] mygetBoardCopy(int[][] temp_board) {
        int copy[][] = new int[8][8];
        for(int i = 0; i < 8; ++i)
            System.arraycopy(temp_board[i], 0, copy[i], 0, 8);
        return copy;
    }

    public ArrayList<Integer> fullGame(int k) {
        ArrayList<Integer> ans=new ArrayList<Integer>();
        int num;
        int x;
        int y;
        int count=0;
        boolean bool;
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if (this.board[i][j]==-1){
                    count=count+1;
                }
            }
        }
        for(int j=0;j<count;j++){
            num=bestMove(k);
            x=(int)(num/8);
            y=num%8;
            if (is_move(board,turn,x,y)==null){
                turn=1-turn;
                j=j-1;
                continue;
            }
            ans.add(num);
            bool=move(board,turn,x,y);
            turn=1-turn;
        }
        if (myboardScore(this.board)>0){
            winner=0;
        }
        else if (myboardScore(this.board)<0){
            winner=1;
        }
        return ans;
    }

    public int[][] getBoardCopy() {
        int copy[][] = new int[8][8];
        for(int i = 0; i < 8; ++i)
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        return copy;
    }

    public int getWinner() {
        return winner;
    }

    public int getTurn() {
        return turn;
    }
}