package com.skyshade.xox.xoxmygame;

import android.content.Context;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String appName = "XoX.SkyShade";
    TextView pl1TextView;
    TextView pl2TextView;
    playField plField;
    MediaPlayer sndPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pl1TextView = (TextView) findViewById(R.id.player1tv);
        pl2TextView = (TextView) findViewById(R.id.player2tv);
        sndPlayer = new MediaPlayer().create(MainActivity.this, R.raw.click);

        plField = new playField();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item1) {
            Toast.makeText(MainActivity.this,"Game restart",Toast.LENGTH_SHORT).show();
            plField = new playField();
        }

        return super.onOptionsItemSelected(item);
    }

    public class playField {
        private int rows = 3;
        private int cols = 3;
        private playCell[][] field = new playCell[rows][cols];
        private players gamePlayers = new players();

        //int defaultPaintFlags = pl1TextView.getPaintFlags();


        playField() {  // constructor of field
            Button[][] btnsArray = new Button[][]{ // массив из кнопок
                    {(Button) findViewById(R.id.cell_11),(Button) findViewById(R.id.cell_12),(Button) findViewById(R.id.cell_13)},
                    {(Button) findViewById(R.id.cell_21),(Button) findViewById(R.id.cell_22),(Button) findViewById(R.id.cell_23)},
                    {(Button) findViewById(R.id.cell_31),(Button) findViewById(R.id.cell_32),(Button) findViewById(R.id.cell_33)}
            };

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Log.i(appName,"Set cell: " + Integer.toString(i) + " " + Integer.toString(j));
                    Log.i(appName,"El id: " + Integer.toString(btnsArray[i][j].getId()));

                    this.field[i][j] = new playCell( btnsArray[i][j] );

                } // for
            } // for

            //pl1TextView.setText("Test1!");

            //1TextView.setPaintFlags(pl1TextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        } // playField

        public class playCell implements OnClickListener{
            private int cellState;
            private Button cellButton;
            public static final int CELLEMPTY = 0;
            public static final int OCELL = 1;
            public static final int XCELL = 2;

            playCell(Button cellButton) { // playCell constructor
                Log.i(appName,"Creating playCell");
                this.cellState = CELLEMPTY;
                this.cellButton = cellButton;
                Log.i(appName,"Button id : " + Integer.toString(this.cellButton.getId()));
                this.cellButton.setOnClickListener(this);
                this.cellButton.setText("");
            }

            public void setCellState(int state){
                Log.i(appName,"Setting cell to state: " + Integer.toString(state));
                if (state == playCell.XCELL) {
                    this.cellButton.setText("X");
                } else
                    this.cellButton.setText("O");
                this.cellState = state;
            } // setCellState

            int getCellState(){
                return this.cellState;
            }

            @Override
            public void onClick(View view) {
                //Button cellBtn = (Button) findViewById(view.getId());
                //cellBtn.setText("X");
                Log.i(appName,"On click event");
                sndPlayer.start();

                // make move
                if (getCellState() == CELLEMPTY){
                    setCellState(gamePlayers.makeMove());
                } else {
                    Log.i(appName,"Already filled!");
                }
                //setCellState(playCell.XCELL);

                checkField();
            }
        } // class playCell

        public class players {
            public static final int PLAYER_TURN = 1;
            public static final int NOT_PLAYER_TURN = 0;

            player playerA = new player("Player 1",playCell.XCELL,PLAYER_TURN,pl1TextView);
            player playerB = new player("Player 2",playCell.OCELL,NOT_PLAYER_TURN,pl2TextView);

            public players() {

            } // players

            public class player{
                public String name = "";
                public int usage = 0;
                public int turn = 0;
                public TextView playerTextView;
                //public int defaultPaint;

                public player(String name, int usage, int turn, TextView playerTextView) {
                    this.name = name;
                    this.usage = usage;
                    this.turn = turn;
                    this.playerTextView = playerTextView;
                    Log.i(appName,"Initialized player : " + this.name + " usage: " + Integer.toString(this.usage)
                    + " turn: " + Integer.toString(this.turn));
                    //defaultPaint = this.playerTextView.getPaintFlags();
                    if (this.turn == 1) {
                        this.playerTextView.setPaintFlags(this.playerTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    } else {
                        this.playerTextView.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    }

                }

                public void changeTurn(){
                    if (this.turn == PLAYER_TURN) {
                        Log.i(appName, this.name + " set player turn to: " + Integer.toString(NOT_PLAYER_TURN));
                        this.turn = NOT_PLAYER_TURN;
                        this.playerTextView.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    } else {
                        Log.i(appName, this.name + " set player turn to: " + Integer.toString(PLAYER_TURN));
                        this.turn = PLAYER_TURN;
                        this.playerTextView.setPaintFlags( this.playerTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    } //else

                } // changeTurn
            }

            void exchangeTurn(){
                Log.i(appName, "Exchanging players turn");
                playerA.changeTurn();
                playerB.changeTurn();
            }

            public int makeMove(){
                int currentPlayerSign=0;
                if (playerA.turn == 1){
                    currentPlayerSign=playerA.usage;
                    Log.i(appName, playerA.name + " turn, usage sign: " + Integer.toString(currentPlayerSign));
                    exchangeTurn();

                } else if (playerB.turn == 1){
                    currentPlayerSign=playerB.usage;
                    Log.i(appName, playerB.name + " turn, usage sign: " + Integer.toString(currentPlayerSign));
                    exchangeTurn();
                } // if


                return currentPlayerSign;
            } // makeMove
        } // class player


        public void checkField() {
            boolean isFieldFull = true;

            Log.i(appName,"Checking field");

            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++) {
                    if (this.field[i][j].cellState == playCell.CELLEMPTY) {
                        isFieldFull = false;
                    } // if
                } // for
            } //for

            // check if there some win

            if (isFieldFull) {
                Log.i(appName,"Game over!");
                Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                // do some stuff on game finish
            } // if
        } // checkField

        private boolean checkWin() {
            // TODO check win

            return true;
        }  // checkWin
    }

}
