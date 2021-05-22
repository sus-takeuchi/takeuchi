import java.io.*;
public class Othello {
	static Player p1,p2,pw;//プレイヤー
	static int[][] ban = { //盤面 0=無し 1=黒 2=白 3=置ける
		{0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,1,2,0,0,0},
        {0,0,0,2,1,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0}};
	public static void main(String[] args) throws IOException {
		String[] kazu = {"１","２","３","４","５","６","７","８"};
		String[] color = {"","黒","白","現在置ける場所"};
		int[] status = new int[4];//盤面の状況を格納
		int tate,yoko = 0;//コマを置く縦位置、横位置
		int failed_cnt = 0; //2回おけなかったらゲーム終了
		p1 = new Player('h',1);//プレイヤー1 黒
		p2 = new Player('h',2);//プレイヤー1 白
		pw = p1;//作業用オブジェクト
		while(true){//ゲームメインループ
			for(int i = 0 ; i < status.length ; i++){//配列の初期化
				status[i] = 0;
			}
			for(int i=0 ; i < ban.length ; i++){ //置ける位置の初期化
				for(int j = 0 ; j < ban[i].length ; j++){
					if(ban[i][j] == 3){
						ban[i][j] = 0;
					}
				}
			}
			//置ける場所チェック
			for(int i = 0 ; i < ban.length ; i++){//縦のループ
				for(int j = 0 ; j < ban[i].length ; j++){//横のループ
					if(ban[i][j] == 0){//コマが置かれていない場所をチェック
						if(check_change(i,j,pw.color,"check")){//置ける時
							ban[i][j] = 3;
						}
					}
				}
			}
			//・盤面の表示
			System.out.print("　");
			for(int i = 0 ; i < ban.length ; i++){
				System.out.print(kazu[i]);
			}
			System.out.println();
			for(int i = 0 ; i < ban.length ; i++){
				System.out.print(kazu[i]);
				for(int j = 0 ; j < ban[i].length ; j++){
					switch(ban[i][j]){
					case 0://なにも無い
						System.out.print("ー");
						break;
					case 1://黒の時
						System.out.print("●");
						break;
					case 2://白の時
						System.out.print("◯");
						break;
					case 3://置ける時
						System.out.print("？");
						break;
					}
				}
				System.out.println();
			}
			//・盤面のチェック
			for(int i = 0 ; i < ban.length; i++){
				for(int j = 0 ; j < ban[i].length ; j++){
					status[ban[i][j]]++;
				}	
			}
			if(status[3] == 0){//置ける場所があるかチェック
				failed_cnt++;//置けないカウント
				System.out.println(status[pw.color]+"は置ける場所がありません。");
				cangePlayer();//プレイヤー交代
			}else{
				failed_cnt = 0;
			}
			int last = status[0] + status[3];//置ける場所の数
			if(last == 0 || failed_cnt == 2){
				break;//ゲームループから抜ける
			}
			for(int i = 1 ; i < status.length ; i++){//盤面の状態を表示
				System.out.print(color[i]+":"+status[i]+" ");
			}
			System.out.println();
			System.out.println(color[pw.color]+"の番です");
			System.out.println();
			int[] point = pw.getPoint();
			tate = point[0];
			yoko = point[1];
			if(ban[tate][yoko] != 3){
				System.out.println();
				System.out.println("指定された位置にはコマを置けません。置ける位置を指定して下さい");
				System.out.println();
				continue;
			}else{//コマを置き、ひっくり返す
				ban[tate][yoko] = pw.color;
				check_change(tate,yoko,pw.color,"change");//ひっくり返す
			}
			cangePlayer();//プレイヤー交代
		}
		System.out.println("ゲーム終了");
		//・勝敗の判定
		for(int i = 1 ; i < 3 ; i++){//コマの状態を表示
			System.out.print(color[i]+":"+status[i]+" ");
		}
		int win = 0;//勝敗
		if(status[1] > status[2]){
			win = 1;
		}else if(status[1] < status[2]){
			win = 2;
		}
		switch(win){//勝敗の表示
		case 0:
			System.out.println("引き分け");
			break;
		case 1:
		case 2:
			System.out.println(color[win]+"の勝ち！");
		}
	}
	static void cangePlayer(){//プレイヤー交代
		if(pw.color == 1){
			pw = p2;
		}else{
			pw = p1;
		}
	}
	//・ひっくり返す、置ける位置の確認
	static boolean check_change(int i,int j,int color,String mode){
		for(int r = -1 ; r < 2 ; r++){
			for(int c = -1 ; c < 2 ; c++){
				int rw = i + r;//縦のチェック位置
				int cw = j + c;//横のチェック位置
				if(rw < 0 || cw < 0 || rw > 7 || cw > 7){//配列からはみだしてないかチェック
					continue;
				}
				if(rw == i && cw == j){//自分自身の位置はチェックしない
					continue;
				}
				boolean loop_flg = true;//ループ継続フラグ
				int step = 0;//敵の色が連続する回数
				do{
					if(rw >= 0 && cw >= 0 && rw <= 7 && cw <= 7){
						if(ban[rw][cw] != 0 && ban[rw][cw] != color && ban[rw][cw] != 3){
							rw += r;
							cw += c;
							step++;
						}else{
							if(mode.equals("check")){//置ける場所チェック
								if(ban[rw][cw] == color){//自分のコマを見つけた時
									if(step != 0){
										return true;
									}											
								}
								loop_flg = false;
							}
							if(mode.equals("change")){
								if(step != 0 && ban[rw][cw] == pw.color){//自分のコマを見つけた時
									while(rw != i || cw != j){//ひっくり返す
										rw -= r;
										cw -= c;
										ban[rw][cw] = color;
									}
									loop_flg = false;
								}else{
									loop_flg = false;
								}
							}
						}
					}else{
						loop_flg = false;
					}
				}while(loop_flg==true);
			}
		}
		return false;
	}
}


class Player{
	char mode; // h = 人間 c = コンピュータ
	int color; // 1 = 黒 2 = 白
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Player(char mode,int color){
		this.mode = mode;
		this.color = color;
	}
	int[] getPoint() throws IOException{
		int[] point = {0,0};//point[0] = 縦 point[1] = 横
		String str;
		if(mode == 'h'){
			do{
				System.out.print("縦位置の置く場所を入力して下さい1～8:");
				str = br.readLine();
				if(str.isEmpty()){//何も入力されなかった時
					point[0]=1;
				}else{
					point[0] = Integer.parseInt(str);
				}
				point[0]--;
			}while(point[0] < 0 || point[0] > 7);
			do{
				System.out.print("横位置の置く場所を入力して下さい1～8:");
				str = br.readLine();
				if(str.isEmpty()){//何も入力されなかった時
					point[1]=1;
				}else{
					point[1] = Integer.parseInt(str);
				}
				point[1]--;
			}while(point[1] < 0 || point[1] > 7);
		}
		return point;
	}
}