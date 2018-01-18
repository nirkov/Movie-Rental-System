package bgu.spl181.net.impl.protocols;

/**
 * @author nir kovrovski
 *
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter {

    public CommandInterpreter(){}

    public Queue<String> giveMeCommand(String msg){
        Queue<String> msgQueue = new LinkedList<>();
        if(!msg.equals("")){
            msg = msg.toString().trim().replaceAll("\\s{2,}", " ");
            if(msg.charAt(0)==' ') msg = msg.substring(1);
            if(msg.charAt(msg.length()-1)==' ') msg = msg.substring(1,msg.length()-1);

            Pattern changeprice   = Pattern.compile("^(REQUEST)(\\s+)(changeprice)(\\s+)(\".*?\")(\\s+)(?<=\\s|^)[-+]?\\d+(?=\\s|$)$", Pattern.DOTALL);
            Pattern addmovie      = Pattern.compile("^(REQUEST)(\\s+)(addmovie)(\\s+)(\".*?\")(\\s+)(?<=\\s|^)[-+]?\\d+(?=\\s|$)(\\s+)(?<=\\s|^)[-+]?\\d+(?=\\s|$)(?:)",Pattern.DOTALL);
            Pattern remmovie      = Pattern.compile("^(REQUEST)(\\s+)(remmovie)(\\s+)(\".*?\")", Pattern.DOTALL);
            Pattern info_movie    = Pattern.compile("^(REQUEST)(\\s+)(info)(\\s+)(\".*?\")$", Pattern.DOTALL);
            Pattern info          = Pattern.compile("^(REQUEST)(\\s+)(info)$", Pattern.DOTALL);
            Pattern rent          = Pattern.compile("^(REQUEST)(\\s+)(rent)(\\s+)(\".*?\")$", Pattern.DOTALL);
            Pattern returnn       = Pattern.compile("^(REQUEST)(\\s+)(return)(\\s+)(\".*?\")$", Pattern.DOTALL);
            Pattern balance_info  = Pattern.compile("^(REQUEST)(\\s+)(balance info)$", Pattern.DOTALL);
            Pattern balance_add   = Pattern.compile("^(REQUEST)(\\s+)(balance add)(\\s+)(?<=\\s|^)[-+]?\\d+(?=\\s|$)$", Pattern.DOTALL);
            Pattern login         = Pattern.compile("^(LOGIN)(\\s+)(.*?)(\\s+)(.*?)$", Pattern.DOTALL);
            Pattern register      = Pattern.compile("^(REGISTER)(\\s+)(.*?)(\\s+)(country=)(\".*?\")$", Pattern.DOTALL);
            Pattern signout       = Pattern.compile("^(SIGNOUT)", Pattern.DOTALL);


            Matcher changeprice_match = changeprice.matcher(msg);
            Matcher addmovie_match    = addmovie.matcher(msg);
            Matcher remmovie_match    = remmovie.matcher(msg);
            Matcher info_movie_match  = info_movie.matcher(msg);
            Matcher info_match        = info.matcher(msg);
            Matcher rent_match        = rent.matcher(msg);
            Matcher returnn_match     = returnn.matcher(msg);
            Matcher balance_info_match= balance_info.matcher(msg);
            Matcher balance_add_match = balance_add.matcher(msg);
            Matcher login_match       = login.matcher(msg);
            Matcher register_match    = register.matcher(msg);
            Matcher signout_match     = signout.matcher(msg);


            if(changeprice_match.find() | addmovie_match.find()| remmovie_match.find()     | info_match.find()
                    | rent_match.find() | returnn_match.find() | balance_info_match.find() | balance_add_match.find()
                    | login_match.find()| register_match.find()| info_movie_match.find()   | signout_match.find())
            {

                int j, i;
                boolean empty_name = false;
                String word = "";
                char[] charMessage = msg.toCharArray();
                for (i = 0; i < charMessage.length; i++) {
                    if (charMessage[i] == '"') {
                        for (j = i + 1; j < charMessage.length; j++) {
                            if (charMessage[j] == '"') break;
                        }
                        if(i != j+1 && msg.substring(i+1,j) != " "){
                            word = msg.substring(i + 1, j);
                        }else{
                            msgQueue.clear();
                            break;
                        }
                        if(j+1 < charMessage.length){
                            i = j+1;
                        }else{
                            msgQueue.add(word);
                            break;
                        }
                    }
                    if (charMessage[i] == ' ' &&  i != charMessage.length ) {
                        msgQueue.add(word);
                        word = "";
                    } else{
                        word = word + charMessage[i];
                        if( i+1 == charMessage.length) msgQueue.add(word);
                    }
                }
            }else msgQueue.add("default");
        }else msgQueue.add("default");
        return msgQueue;
    }

}


