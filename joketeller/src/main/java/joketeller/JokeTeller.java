package joketeller;

import java.util.ArrayList;
import java.util.Random;

public class JokeTeller {

    public ArrayList<String> mJokeArray = new ArrayList<>();


    public JokeTeller(){
        mJokeArray.add("Three nazis walked into a BAR");
        mJokeArray.add("Q: What's a programmer's favorite hangout spot?\nA: Foo Bar");
        mJokeArray.add("Q: What do computer and air condicioners have in common?\nA: They become useless when you use widows");
        mJokeArray.add("Chuck Norris writes code that optimizes itself");
        mJokeArray.add("A dev had and problem.\nHe decided to use Java.\nNow he has a ProblemFactory");
        mJokeArray.add("; - hide and seek champion sine 1958.");
        mJokeArray.add("Programmer - a machine that turns coffee into code.");

    }

    public String getJoke(){

        if(mJokeArray!=null && !mJokeArray.isEmpty()) {
            int max = mJokeArray.size();
            int i = new Random().nextInt(max);

            return mJokeArray.get(i);
        }

        return "No jokes available";
    }


}
