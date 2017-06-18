package com.example.pvanraalte1746.pvr_prog3210_assignment1;

/**
 * Created by Pvanraalte1746 on 9/27/2016.
 * This class is used to represent the squares in a tic tac toe game.
 */
public final class TicTacToeButtonState {
    public static enum State {
        X, O, NONE
    };

    State state = State.NONE;

    /*
    This is the constructor for this class
     */
    public TicTacToeButtonState(){

    }
    /*
    This method returns the state of the button.
    @return "X" or "O" or "None"
     */
    public String getState(){
        return state.toString();
    }
    /*
    This method sets the state of the button.
    @parameter state --> the state of the button
     */
    public void setState(State state){
        this.state = state;
    }
}
