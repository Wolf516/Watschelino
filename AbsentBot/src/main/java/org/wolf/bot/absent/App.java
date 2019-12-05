package org.wolf.bot.absent;

import javax.security.auth.login.LoginException;

import org.wolf.sqlite.SQLiteManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws LoginException, IllegalArgumentException
    {
    	SQLiteManager.singleton().setupSchema();
    	AbsentBotManage.init();
    	//AbsentListManage.clearInputChannel();
    	
    }
}
