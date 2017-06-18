package com.example.pvanraalte1746.pvr_prog3210_assignment1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static TextView txtTurn;
    private Button btnRestart;
    private Button[][] btns;
    private static Boolean isCPUPlaying = true;
    private static TicTacToeButtonState buttonStates[][] = new TicTacToeButtonState[3][3];
    private static boolean isXTurn = true;
    private boolean winState; //prevents multiple dialogs displaying
    private static boolean hasShownCPUPrompt = false;

    @Override
    /*
    This method is called when the application starts
     */
    protected void onStart(){
        writeLog("Entering onStart");
        super.onStart();
        fillButtonStates();
    }

    @Override
    /*
    This method is called after onStart and whenever the screen orientation changes
     */
    protected void onCreate(Bundle savedInstanceState) {
        writeLog("Entering onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Instantiating and Initializing Variables
        winState = false;
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        int btn_Width, btn_Height; //_ so they don't get confused as actual buttons
        float btnHorizontalSpace, btnVerticalSpace, startBtnX, btn_fontSize,
                btnRestart_Height, btnRestart_Width;
        float startBtnY = (float) Math.ceil(height / 11);

        txtTurn = new TextView(this);
        txtTurn.setGravity(Gravity.CENTER);
        txtTurn.setTextSize(18f);

        //This RelativeLayout is used to add Buttons and TextViews
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout);

        btns = new Button[3][3];
        btnRestart = new Button(this);
        btnRestart.setText("Restart");
        btnRestart.setTextSize(18f);
        btnRestart.setBackgroundColor(Color.rgb(206, 198, 194));
        btnRestart.setOnClickListener(new View.OnClickListener() {
            //restarts the game when the restart button is pressed
            @Override
            public void onClick(View v) {
                restart();
            }
        });
        //endregion

        //region Handle Screen Orientation
        View view = getWindow().getDecorView();
        int orientation = getResources().getConfiguration().orientation;
        //orientation is more likely to be portrait
        if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            startBtnX = (float) Math.ceil(width / 80);
            btn_Width = (int) Math.ceil(width / 3.6);
            btn_Height = (int) Math.ceil(height / 6);
            btnHorizontalSpace = btn_Width + (float) Math.ceil(width / 30);
            btn_fontSize = 60f;

            btnRestart_Width = btn_Width * 1.25f;
            btnRestart_Height = btn_Height * 0.7f;
            btnRestart.setX(width / 2 - (btnRestart_Width / 1.75f));
            btnRestart.setY(height - (btnRestart_Height * 1.95f));
            btnRestart.setHeight((int) btnRestart_Height);
            btnRestart.setWidth((int) btnRestart_Width);

            txtTurn.setY(height - (height / 3.3f));
            txtTurn.setX(width / 2.75f);
        } else {
            //ORIENTATION --> LANDSCAPE
            startBtnX = (float) Math.ceil(width / 30);
            btn_Width = (int) Math.ceil(width / 4.9);
            btn_Height = (int) Math.ceil(height / 5.2);
            btnHorizontalSpace = btn_Width + (float) Math.ceil(width / 50);
            btn_fontSize = 46f;

            btnRestart_Width = btn_Width * 0.95f;
            btnRestart_Height = btn_Height * 0.6f;
            btnRestart.setX(width - (btnRestart_Width / 0.75f));
            btnRestart.setY(height - (btnRestart_Height * 3.15f));
            btnRestart.setHeight((int) btnRestart_Height);
            btnRestart.setWidth((int) btnRestart_Width);

            txtTurn.setY(height / 3);
            txtTurn.setX(width - (width / 4.1f));
        }
        btnVerticalSpace = btn_Height + (float) Math.ceil(height / 40);
        setTurnText(); //when orientation changes -> has no effect when app starts
        rl.addView(btnRestart);
        rl.addView(txtTurn);
        //endregion

        //region Setting Up Tic Tac Toe Buttons
        float currentBtnX = startBtnX;
        for(int i = 0; i <btns.length; i++){
            for(int j = 0; j <btns[i].length; j++){
                btns[i][j] = new Button(this);
                btns[i][j].setX(currentBtnX);
                btns[i][j].setY(startBtnY);
                btns[i][j].setTextSize(btn_fontSize);

                /*
                 * If there is an error when it attempts to get the state the
                 * button states need to be initiated.
                 */
                try{
                    buttonStates[0][0].getState();
                }
                catch(Exception ex) {
                    fillButtonStates();
                }

                if (buttonStates[i][j].getState().equals("X")) {
                    btns[i][j].setText("X");
                } else if (buttonStates[i][j].getState().equals("O")) {
                    btns[i][j].setText("O");
                } else {
                    btns[i][j].setText("");
                }

                btns[i][j].setBackgroundColor(Color.rgb(180, 180, 180));
                btns[i][j].setWidth(btn_Width);
                btns[i][j].setHeight(btn_Height);
                btns[i][j].setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                btns[i][j].setTag(i + "_" + j);

                btns[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //SystemClock.sleep(500);
                        writeLog("onClick called");
                        Button thisBtn = (Button) v;
                        try {
                            String tag = thisBtn.getTag().toString();
                            int i = Integer.parseInt(tag.split("_")[0]);
                            int j = Integer.parseInt(tag.split("_")[1]);

                            if (buttonStates[i][j].getState().equals(TicTacToeButtonState.State.NONE.toString())) {
                                if (isXTurn) {
                                    thisBtn.setText("X");
                                    buttonStates[i][j].setState(TicTacToeButtonState.State.X);
                                } else {
                                    thisBtn.setText("O");
                                    buttonStates[i][j].setState(TicTacToeButtonState.State.O);
                                }
                                endTurn();
                                if (isCPUPlaying) {
                                    cpuTurn();
                                }
                            }
                        } catch (Exception ex) {
                            writeLog("Error - " + ex.getMessage().toString());
                        }
                        String btnText = thisBtn.getText().toString();
                    }
                });
                rl.addView(btns[i][j]);
                currentBtnX += btnHorizontalSpace;
            }
            currentBtnX = startBtnX;
            startBtnY += btnVerticalSpace;
        }
        updateButtonStates();
        //endregion

        //region Player or CPU Prompt
        if(!hasShownCPUPrompt) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("New Game");
            alertBuilder.setMessage("What would you like to play against?");
            alertBuilder.setCancelable(false);

            alertBuilder.setNegativeButton(
                    "Another Player",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            isCPUPlaying = false;
                            setTurnText();
                            hasShownCPUPrompt = true;
                        }
                    });
            alertBuilder.setPositiveButton(
                    "CPU",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            isCPUPlaying = true;
                            setTurnText();
                            hasShownCPUPrompt = true;
                        }
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        }
        //endregion

    }

    /*
    This method toggles who's turn it is and checks if anyone has won the game
     */
    private void endTurn(){
        isXTurn = !isXTurn;

        if(isXTurn){
            if(isCPUPlaying)
                txtTurn.setText("Your Turn");
            else
                txtTurn.setText("Player 1's Turn");
        }
        else{
            if(isCPUPlaying)
                txtTurn.setText("CPU's Turn");
            else
                txtTurn.setText("Player 2's Turn");
        }



        String hasWon = hasGameEnded();

        writeLog("Has Won: " + hasWon);

        if(!winState && (hasWon.equals("X") || hasWon.equals("O") || hasWon.equals("TIE"))){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Game Ended");
            if(hasWon.equals("X"))
                alertBuilder.setMessage("X has won!");
            else if(hasWon.equals("O"))
                alertBuilder.setMessage("O has won!");
            else if(hasWon.equals("TIE"))
                alertBuilder.setMessage("Tie game!");
            alertBuilder.setCancelable(false);

            alertBuilder.setPositiveButton(
                    "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            restart();
                        }
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
            winState = true;
        }
    }

    /*
    This method handles the CPU's turn.
    It checks which squares are available and randomly picks one.
     */
    private void cpuTurn(){
        ArrayList<Point> availableSquares = new ArrayList<>();

        for(int i = 0; i < buttonStates.length; i++) {
            for (int j = 0; j < buttonStates[i].length; j++) {
                if(buttonStates[i][j].getState().equals("NONE")){
                    availableSquares.add(new Point(i, j));
                }
            }
        }
        Random rnd = new Random();
        int squareIndex;
        try {
            squareIndex = rnd.nextInt(availableSquares.size());
        }
        catch (Exception ex){
            squareIndex = 0;
        }

        Point selectedSquare = availableSquares.get(squareIndex);

        if (isXTurn) {
            btns[selectedSquare.x][selectedSquare.y].setText("X");
            buttonStates[selectedSquare.x][selectedSquare.y].setState(TicTacToeButtonState.State.X);
        } else {
            btns[selectedSquare.x][selectedSquare.y].setText("O");
            buttonStates[selectedSquare.x][selectedSquare.y].setState(TicTacToeButtonState.State.O);
        }
        endTurn();

        writeLog("NUMBER: " + squareIndex + " - " + availableSquares.size());
    }

    /*
    This method checks if anyone has won the game.
    @return "X" if X has won.
    @return "O" if O has won.
    @return "TIE" if all the squares have been selected, but no one has won.
    @return "NO" if no one has won.
     */
    private String hasGameEnded(){

        int consecutiveXs = 0;
        int consecutiveOs = 0;
        //region Horizontal Check
        for(int i = 0; i < buttonStates.length; i++){
            for(int j = 0; j < buttonStates[i].length; j++){
                if(buttonStates[i][j].getState().equals("X") && consecutiveOs == 0){
                    consecutiveXs++;
                    if(consecutiveXs > 2){
                        return "X";
                    }
                }
                else if(buttonStates[i][j].getState().equals("O") && consecutiveXs == 0){
                    consecutiveOs++;
                    if(consecutiveOs > 2){
                        return "O";
                    }
                }
                else{
                    break;
                }
            }
            consecutiveXs = 0;
            consecutiveOs = 0;
        }
        //endregion

        //region Vertical Check
        if((buttonStates[0][0].getState().equals("O") &&
             buttonStates[1][0].getState().equals("O") &&
             buttonStates[2][0].getState().equals("O")) ||
            (buttonStates[0][1].getState().equals("O") &&
             buttonStates[1][1].getState().equals("O") &&
             buttonStates[2][1].getState().equals("O")) ||
            (buttonStates[0][2].getState().equals("O") &&
             buttonStates[1][2].getState().equals("O") &&
             buttonStates[2][2].getState().equals("O")))
        {
            return "O";
        }

        if((buttonStates[0][0].getState().equals("X") &&
             buttonStates[1][0].getState().equals("X") &&
             buttonStates[2][0].getState().equals("X")) ||
            (buttonStates[0][1].getState().equals("X") &&
             buttonStates[1][1].getState().equals("X") &&
             buttonStates[2][1].getState().equals("X")) ||
            (buttonStates[0][2].getState().equals("X") &&
             buttonStates[1][2].getState().equals("X") &&
             buttonStates[2][2].getState().equals("X")))
        {
            return "X";
        }
        //endregion

        //region Diagonal Check
        if((buttonStates[0][0].getState().equals("O") &&
             buttonStates[1][1].getState().equals("O") &&
             buttonStates[2][2].getState().equals("O")) ||
            (buttonStates[2][0].getState().equals("O") &&
             buttonStates[1][1].getState().equals("O") &&
             buttonStates[0][2].getState().equals("O"))){
                return "O";
        }
        if((buttonStates[0][0].getState().equals("X") &&
             buttonStates[1][1].getState().equals("X") &&
             buttonStates[2][2].getState().equals("X")) ||
            (buttonStates[2][0].getState().equals("X") &&
             buttonStates[1][1].getState().equals("X") &&
             buttonStates[0][2].getState().equals("X"))){
            return "X";
        }
        //endregion

        for(int i = 0; i < buttonStates.length; i++) {
            for (int j = 0; j < buttonStates[i].length; j++) {
                if(buttonStates[i][j].getState().equals("NONE")){
                    return "NO";
                }
            }
        }

        return "TIE";
    }

    /*
    This method fills the buttonStates
     */
    private void fillButtonStates(){
        for (int i = 0; i < buttonStates.length; i++) {
            for (int j = 0; j < buttonStates[i].length; j++) {
                buttonStates[i][j] = new TicTacToeButtonState();
            }
        }
    }

    /*
    This method updates the button states based on what the text of the button is.
    This is useful for when the user changes the screen orientation.
     */
    private void updateButtonStates(){
        for (int i = 0; i < btns.length; i++) {
            for (int j = 0; j < btns[i].length; j++) {
                String btnText = btns[i][j].getText().toString();

                if(btnText.equals("X")){
                    buttonStates[i][j].setState(TicTacToeButtonState.State.X);
                }
                else if(btnText.equals("O")){
                    buttonStates[i][j].setState(TicTacToeButtonState.State.O);
                }
                else{
                    buttonStates[i][j].setState(TicTacToeButtonState.State.NONE);
                }
                writeLog("Update Button State - " + btnText);

            }
        }
    }

    /*
    This method sets the text of txtTurn based on if the CPU is playing
    and whose turn it is
     */
    private void setTurnText(){
        if(isCPUPlaying){
            if(isXTurn){
                txtTurn.setText("Your Turn");
            }
            else{
                txtTurn.setText("CPU's Turn");
            }
        }
        else{
            if(isXTurn){
                txtTurn.setText("Player 1's Turn");
            }
            else{
                txtTurn.setText("Player 2's Turn");
            }
        }
    }

    /*
    This method makes logs easily visible in logcat

    @parameter String log --> message to put in logcat
     */
    private void writeLog(String log){
        String TAG = "LOG + MainActivity";
        String LOG_SUFFIX = "       <------------------|";
        Log.d(TAG, log + LOG_SUFFIX);
    }

    /*
    This method restarts the activity
     */
    private void restart(){
        isXTurn = true;
        fillButtonStates();
        hasShownCPUPrompt = false;

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

}
