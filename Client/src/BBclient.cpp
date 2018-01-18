//
// Created by nir on 10/01/18.
//


#include "../include/connectionHandler.h"
#include "../include/GlobalVariables.h"
#include <boost/thread.hpp>
using namespace std;


class GetFromKeyboard {
private:
    ConnectionHandler* CONNECTION_HANDLER = nullptr;

public:
    GetFromKeyboard(ConnectionHandler* newConHan) : CONNECTION_HANDLER(newConHan){}

    void operator()(){
        int stop_after_2min = 0;
        while (!signout_server) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            int len = line.length();
            if(line == "SIGNOUT") signout_send = true;
            if (!(*CONNECTION_HANDLER).sendLine(line)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            while(signout_send & stop_after_2min < 20){
                boost::this_thread::sleep_for(boost::chrono::milliseconds(100));
                stop_after_2min = stop_after_2min + 1;
            }
            if(signout_send & stop_after_2min == 20){
                signout_send = false;
                stop_after_2min = 0;
            }
        }
    };
};


class GetFromServer {
private:
    ConnectionHandler* CONNECTION_HANDLER_SERVER = nullptr;

public:
    GetFromServer(ConnectionHandler* newConHan) : CONNECTION_HANDLER_SERVER(newConHan){}

    void operator()(){
        while (!signout_server) {
            std::string answer;
            if (!CONNECTION_HANDLER_SERVER->getLine(answer)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            answer = answer.substr(0,answer.length()-1);
            cout << answer << endl;
            if(answer == "ACK signout succeeded"){
                signout_send = false;
                signout_server = true;
                break;
            }
        }
    };
};

int main (int argc, char *argv[]) {

    string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    GetFromKeyboard fromKeyboard(&connectionHandler);
    GetFromServer fromServer(&connectionHandler);

    boost::thread getFromKeyBoard_Theard(fromKeyboard);
    boost::thread getFromServer_Theard(fromServer);

    getFromServer_Theard.join();
    getFromKeyBoard_Theard.join();


}

