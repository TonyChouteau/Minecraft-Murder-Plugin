package fr.tonychouteau.murder.bukkit.util;

import fr.tonychouteau.murder.bukkit.game.Game;

import java.lang.Runnable;

public class MyRunnable implements Runnable {

    protected int times;
    protected Game game;
    protected boolean stop = false;

    public void setGame(Game game){
        this.game = game;
    }

    public void stop() {
        this.stop = true;
    }

    public boolean isStopped() {
        return this.stop;
    }

    public void setTimes(int times){
        this.times = times;
    }

    public int getTimes(){
        return this.times;
    }

    @Override
    public void run(){}
}