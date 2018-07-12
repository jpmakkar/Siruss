package com.example.hp2.sirus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class TicTacToe extends AppCompatActivity {

    CheckedTextView checkedTextView;
    TextView textView;
    EditText editText;
    ImageButton imageButton;
    int[] board=new int[10];
    int[] array=new int[10];
    int turn=1;
    ArrayList<Integer> player1=new ArrayList<>();
    ArrayList<Integer>player2=new ArrayList<>();
    boolean win=false;
    StringBuffer buffer=new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        editText=(EditText)findViewById(R.id.editTextuser);
        textView=(TextView)findViewById(R.id.textViewturn);
        imageButton=(ImageButton)findViewById(R.id.imagebuttonplay);
        checkedTextView=(CheckedTextView)findViewById(R.id.checkedTextViewgame);

        array[1]=8;
        array[2]=3;
        array[3]=4;
        array[4]=1;
        array[5]=5;
        array[6]=9;
        array[7]=6;
        array[8]=7;
        array[9]=2;

        for(int i=1;i<10;i++) {
            board[i]=2;
        }

        textView.setText("Turn : "+turn);

        for(int j=1;j<10;j++) {
            if(board[j]==2) {
                buffer.append("_ ");
            }else if(board[j]==5) {
                buffer.append("O ");
            }else if(board[j]==3) {
                buffer.append("X ");
            }
            if(j%3==0) {
                buffer.append("\n");
            }
        }
        checkedTextView.setText(buffer.toString());

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction()==KeyEvent.ACTION_DOWN && i==KeyEvent.KEYCODE_ENTER){
                    if(board[Integer.parseInt(editText.getText().toString().charAt(0)+"")]==2){
                        textView.setText("Turn : "+turn);
                        go(Integer.parseInt(editText.getText().toString().charAt(0)+""));
                        move();
                        buffer.delete(0,buffer.length());
                        for(int j=1;j<10;j++) {
                            if(board[j]==2) {
                                buffer.append("_ ");
                            }else if(board[j]==5) {
                                buffer.append("O ");
                            }else if(board[j]==3) {
                                buffer.append("X ");
                            }
                            if(j%3==0) {
                                buffer.append("\n");
                            }

                        }
                        checkedTextView.setText(buffer.toString());
                        editText.setText("");
                        if(!win && turn==9) {
                            imageButton.setVisibility(View.VISIBLE);
                            editText.setVisibility(View.GONE);
                            textView.setText("It's a Draw");
                        }
                    }else{
                        Toast.makeText(TicTacToe.this,"Already Filled",Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.GONE);
                turn=1;
                for(int i=1;i<10;i++) {
                    board[i]=2;
                }
                buffer.delete(0,buffer.length());
                player1.clear();
                player2.clear();
                textView.setText("Turn : "+turn);

                for(int j=1;j<10;j++) {
                    if(board[j]==2) {
                        buffer.append("_  ");
                    }else if(board[j]==5) {
                        buffer.append("O  ");
                    }else if(board[j]==3) {
                        buffer.append("X  ");
                    }
                    if(j%3==0) {
                        buffer.append("\n\n");
                    }
                }
                checkedTextView.setText(buffer.toString());
            }
        });
    }

    void  go(int n) {
        if(turn%2==0) {
            board[n]=5;
            player2.add(array[n]);
        }else {
            board[n]=3;
            player1.add(array[n]);
        }
        if(checkwin()==27) {
            textView.setText("You Win");
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
            win=true;
        }else if(checkwin()==125) {
            textView.setText("Sirus Win");
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
            win=true;
        }
        turn++;
    }


    int posswin(int n) {

        if(n==3) {
            for(int i=0;i<player1.size();i++) {
                for(int k=i+1;k<player1.size();k++) {
                    int sum=player1.get(i)+player1.get(k);
                    if(15-sum>0 && 15-sum<=9) {
                        for(int j=1;j<array.length;j++) {
                            if(array[j]==15-sum) {
                                if(board[j]==2) {
                                    return j;
                                }
                                break;
                            }
                        }
                    }
                }
            }

        }else {
            for(int i=0;i<player2.size();i++) {
                for(int k=i+1;k<player2.size();k++) {
                    int sum=player2.get(i)+player2.get(k);
                    if(15-sum>0 && 15-sum<=9) {
                        for(int j=1;j<array.length;j++) {
                            if(array[j]==15-sum) {
                                if(board[j]==2) {
                                    return j;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    void move() {
        switch(turn) {
            case 1:
                go(1);
                break;
            case 2:
                if(board[5]==2) {
                    go(5);
                }else {
                    go(1);
                }
                break;
            case 3:
                if(board[9]==2) {
                    go(9);
                }else {
                    go(3);
                }
                break;
            case 4:
                if(posswin(3)!=0) {
                    go(posswin(3));
                }else {
                    go(make2());
                }
                break;
            case 5:
                if(posswin(3)!=0) {
                    go(posswin(3));
                }else if(posswin(5)!=0) {
                    go(posswin(5));
                }
                else if(board[7]==2) {
                    go(7);
                }else {
                    go(3);
                }
                break;
            case 6:
                if(posswin(5)!=0) {
                    go(posswin(5));
                }else if(posswin(3)!=0) {
                    go(posswin(3));
                }else {
                    go(make2());
                }
                break;
            case 7:
                if(posswin(3)!=0) {
                    go(posswin(3));
                }else if(posswin(5)!=0) {
                    go(posswin(5));
                }else {
                    for(int i=1;i<10;i++) {
                        if(board[i]==2) {
                            go(i);
                            return;
                        }
                    }
                }
                break;
            case 8:if(posswin(5)!=0) {
                go(posswin(5));
            }else if(posswin(3)!=0) {
                go(posswin(3));
            }else {
                for(int i=1;i<10;i++) {
                    if(board[i]==2) {
                        go(i);
                        return;
                    }
                }
            }
                break;
            case 9:if(posswin(3)!=0) {
                go(posswin(3));
            }else if(posswin(5)!=0) {
                go(posswin(5));
            }else {
                for(int i=1;i<10;i++) {
                    if(board[i]==2) {
                        go(i);
                        return;
                    }
                }
            }
                break;
        }
    }

    int make2() {
        int x=0;
        if(board[5]==2) {
            x= 5;
        }else {
            if(board[2]==2) {
                x= 2;
            }else if(board[4]==2) {
                x= 4;
            }else if(board[6]==2) {
                x= 6;
            }else if(board[8]==2) {
                x= 8;
            }
        }
        return x;
    }

    int checkwin() {
        if(player1.size()>=3) {
            for(int i=0;i<player1.size()-2;i++) {
                if(player1.get(i)+player1.get(i+1)+player1.get(i+2)==15) {
                    return 27;
                }
            }
        }
        if(player2.size()>=3) {
            for(int i=0;i<player2.size()-2;i++) {
                if(player2.get(i)+player2.get(i+1)+player2.get(i+2)==15) {
                    return 125;
                }
            }
        }
        return 0;
    }
}
