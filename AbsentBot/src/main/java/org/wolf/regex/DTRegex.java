package org.wolf.regex;

import java.util.ArrayList;
import java.util.HashMap;

public class DTRegex {
    private HashMap<Integer, String> registeredExpressions;

    public DTRegex(){
        this.registeredExpressions = new HashMap<Integer, String>();
    }

    //Returns true if the expression was registered successfully. false if the key is already in use.
    public boolean registerExpression(Integer key, String expression){
        if (this.registeredExpressions.get(key) == null && key >= 0){
            this.registeredExpressions.put(key, expression);
            return true;
        }
        return false;
    }

    public void clear(Integer key){
        this.registeredExpressions.remove(key);
    }

    public Integer checkStringForExpression(String string){
        ArrayList<Integer> keys = new ArrayList<Integer>(this.registeredExpressions.keySet());
        keys.sort(null);

        for (int i = 0; i < keys.size(); i++){
            if (string.matches(this.registeredExpressions.get(keys.get(i)))) return keys.get(i);
        }

        return -1;
    }
}
